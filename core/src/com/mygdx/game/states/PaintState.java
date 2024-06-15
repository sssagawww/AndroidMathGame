package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.*;
import static com.mygdx.game.UI.BtnBox.STATES.*;
import static com.mygdx.game.handlers.GameStateManager.*;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Dialog.Dialog;
import com.mygdx.game.Dialog.DialogController;
import com.mygdx.game.Dialog.DialogNode;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.OptionBox;
import com.mygdx.game.UI.OptionBox2;
import com.mygdx.game.UI.PaintMenu;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.multiplayer.MushroomsRequest;
import com.mygdx.game.paint.DistanceCalc;
import com.mygdx.game.paint.Figures.Figure;
import com.mygdx.game.paint.Figures.FiguresDatabase;
import com.mygdx.game.paint.PixelPoint;


import java.util.ArrayList;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class PaintState extends GameState implements InputProcessor {
    private ShapeRenderer shapeRenderer;
    private ShapeDrawer sd;
    private int rectWidth, rectHeight;
    private ArrayList<PixelPoint> points;
    private ArrayList<Integer> skippedPoints = new ArrayList<>();
    private FiguresDatabase figuresDatabase;
    private Stage uiStage;
    private Stage onlineStage;
    private PaintMenu paintMenu;
    private InputMultiplexer multiplexer;
    private Dialog dialog;
    private DialogController dcontroller;
    private DialogNode node;
    private DistanceCalc distanceCalc;
    private Array<Vector2> arr = new Array<>();
    private PolygonSpriteBatch batch;
    private BoundedCamera paintCam;
    private MushroomsRequest request;
    private Label readyLabel;
    private float requestTime = 0;
    private final int id = MyGdxGame.getPrefs().getInteger(PREF_ID);
    public static final String PAINT_GAME = "paintMiniGame";
    private static boolean online;
    private boolean btnClicked;
    private boolean ready = false;
    private String oppScore;
    private static boolean done;
    private int count = 0;
    private int oppCount = 0;
    private float time = 0;

    public PaintState(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        figuresDatabase = game.getFiguresDatabase();
        shapeRenderer = new ShapeRenderer();

        rectWidth = 10;
        rectHeight = 10;

        points = new ArrayList<>();
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);

        distanceCalc = new DistanceCalc(this);

        request = gsm.game().getRequest();
        if (online) {
            request.leave(id);
            request.join(id, PAINT_GAME, 0);
        }

        initUI();
        createSD();

        paintCam = new BoundedCamera();
        paintCam.setBounds(0, V_WIDTH, 0, V_HEIGHT);
        paintCam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT));

        Gdx.input.setInputProcessor(multiplexer);
    }

    public PaintState(GameStateManager gsm, ArrayList<FiguresDatabase.FIGURES_TYPES> figuresTypes) {
        this(gsm);
        if (figuresTypes != null) {
            figuresDatabase.loadFigures(figuresTypes);
        }
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        if(!MyGdxGame.active && online){
            request.leave(id);
        }
        uiStage.act(dt);
        if (online) onlineStage.act(dt);
        checkBtns();
        dcontroller.update(dt);

        if(done && online){
            time+=dt;
            if(time >= 2){
                time = 0;
                done = false;
                gsm.setState(gsm.getLastState());
            }
        }

        if (ready && (paintMenu.getBtnBox().isClicked() || btnClicked)) {
            request.setPlayerReady(id, false);
        }

        if (btnClicked || !online) {
            paintMenu.checkProgress(dt);
        }

        if (online && request.isDone()) {
            requestTime += dt;
            if (requestTime >= dt * 15) {
                requestTime = 0;
                request.postInfo(id, (float) distanceCalc.getAccuracy());
                ready = request.isReady();
            }
            oppScore = String.format("%.2f", 1 - request.getOpponentScore());
        }
        checkProgress();
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(1, 1, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        paintCam.update();
        sb.setProjectionMatrix(paintCam.combined);
        batch.setProjectionMatrix(paintCam.combined);
        //sd.update();

        //shapeRenderer.setProjectionMatrix(cam.combined);

        //подсказки
        sb.begin();
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < getFigureHints().size(); i++) {
            shapeRenderer.rect(getFigureHints().get(i).getX(), getFigureHints().get(i).getY(), rectWidth, rectHeight);
        }
        shapeRenderer.end();
        sb.end();

        //рисунок игрока
        batch.begin();
        arr.clear();
        for (int i = 0; i < getPoints().size(); i++) {
            if (skippedPoints.contains(i)) {
                sd.path(arr, JoinType.SMOOTH, true);
                arr.clear();
                continue;
            }
            Vector2 newPoint = new Vector2(getPoints().get(i).getX(), getPoints().get(i).getY());

            if (arr.isEmpty()) {
                arr.add(newPoint);
                arr.add(new Vector2(newPoint).add(1, 0));
            } else {
                Vector2 last = new Vector2(arr.get(arr.size - 2));
                if (last.sub(newPoint).len() > rectWidth * 2) {
                    arr.add(newPoint);
                } else {
                    arr.set(arr.size - 1, newPoint);
                }
            }
        }
        sd.path(arr, JoinType.SMOOTH, true);
        batch.end();

        uiStage.draw();
        if (online) onlineStage.draw();
    }

    @Override
    public void dispose() {
        if(online) {
            request.leave(id);
        }
        PaintState.setOnline(false);
    }

    private void createSD() {
        batch = new PolygonSpriteBatch();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture shapeDrawerTexture = new Texture(pixmap);
        pixmap.dispose();
        TextureRegion region = new TextureRegion(shapeDrawerTexture);

        sd = new ShapeDrawer(batch, region);
        sd.setDefaultLineWidth(10f);
        sd.setColor(Color.BLACK);
    }

    public void initUI() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        paintMenu = new PaintMenu(game.getSkin(), game);

        //временная кнопка выхода обратно
        Image menuImg = new Image(new Texture("controller/menuBtn.png"));
        menuImg.setScale(5, 5);
        menuImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gsm.setState(gsm.getLastState());
            }
        });

        onlineStage = new Stage(new ScreenViewport());
        onlineStage.getViewport().update(V_WIDTH, V_HEIGHT, true);
        if (online) {
            BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
            Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);
            lstyle.background = game.getSkin().getDrawable("menuBtn_down");
            readyLabel = new Label("Нажмите, если готовы", lstyle);
            readyLabel.setAlignment(Align.center);
            readyLabel.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    request.playerIsReady(id);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    readyLabel.setVisible(false);
                    btnClicked = true;
                }
            });

            Table onlineRoot = new Table();
            onlineRoot.setFillParent(true);
            onlineStage.addActor(onlineRoot);
            onlineRoot.add(readyLabel).width(V_WIDTH).height(V_HEIGHT / 4f).expand(true, false);
        }

        Table table = new Table();
        table.add(initBtn()).expand().align(Align.bottomRight).width(100f).height(100f).pad(15f);
        table.add(paintMenu).expand().align(Align.right);
        root.add(menuImg).expand().align(Align.topLeft).padTop(65f);
        root.add(table).expand().align(Align.bottomRight).padBottom(10f);

        Table dialogRoot = new Table();
        dialogRoot.setFillParent(true);
        uiStage.addActor(dialogRoot);

        DialogBox dialogueBox = new DialogBox(game.getSkin());
        dialogueBox.setVisible(false);

        OptionBox2 optionBox = new OptionBox2(game.getSkin());
        optionBox.setVisible(false);

        dialogRoot.add(dialogueBox).expand().align(Align.top).padTop(50f);

        dcontroller = new DialogController(dialogueBox, optionBox);
        dialog = new Dialog();

        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(onlineStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private Table initBtn() {
        ImageButton.ImageButtonStyle tableStyle = new ImageButton.ImageButtonStyle();
        ImageButton table = new ImageButton(tableStyle);
        tableStyle.up = game.getSkin().getDrawable("menuBtn_up");
        tableStyle.down = game.getSkin().getDrawable("menuBtn_down");

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        ImageButton btn = new ImageButton(style);
        style.up = game.getSkin().getDrawable("ok");
        style.down = game.getSkin().getDrawable("ok_down");

        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                /*if(online){
                    request.postInfo(id, playerName, (float) distanceCalc.getAccuracy());
                }*/
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (online) {
                    request.setPlayerReady(id, true);
                }
                paintMenu.getBtnBox().setState(CHECK);
                paintMenu.getBtnBox().setClicked(true);
            }
        });
        table.add(btn).width(100f).height(100f);
        return table;
    }

    private void checkBtns() {
        if (paintMenu.getBtnBox().getState() == CHECK) {
            if (distanceCalc.isSame()) {
                paintMenu.getBtnBox().setState(OK);
            } else {
                paintMenu.getBtnBox().setState(WRONG);
            }

            if (online && !ready) {
                paintMenu.getBtnBox().setState(CHECK);
            }
            paintMenu.setAccuracy(distanceCalc.getAccuracy());
        }
        switch (paintMenu.getBtnBox().getState()) {
            case CLEAR:
                //временное сохранение фигуры в json
                //figuresDatabase.saveJson(new Figure("Ромб", FiguresDatabase.FIGURES_TYPES.RHOMBUS, points, points));

                skippedPoints.clear();
                points.clear();
                arr.clear();
                paintMenu.getBtnBox().setState(NON);
                break;
            case OK:
                node = new DialogNode("Получилось! Молодец!", 0);
                if (online) {
                    if((1 - distanceCalc.getAccuracy()) >  1 - request.getOpponentScore()){
                        count++;
                    } else {
                        oppCount++;
                    }
                    node = new DialogNode(String.format("%.2f", 1 - distanceCalc.getAccuracy()) + " : " + oppScore, 0);
                }
                startDialogController();
                break;
            case WRONG:
                node = new DialogNode("Попробуй еще раз!", 0);
                if (online) {
                    if((1 - distanceCalc.getAccuracy()) >  1 - request.getOpponentScore()){
                        count++;
                    } else {
                        oppCount++;
                    }
                    node = new DialogNode(String.format("%.2f", 1 - distanceCalc.getAccuracy()) + " : " + oppScore, 0);
                }
                startDialogController();
                break;
            case DONE:
                paintMenu.getBtnBox().setState(FINISH);
                done = true;
                if(online){
                    if(count > oppCount){
                        node = new DialogNode("Вы победили!", 0);
                    } else {
                        node = new DialogNode("Вы проиграли!", 0);
                    }
                } else {
                    gsm.setState(gsm.getLastState());
                }
                dialog.addNode(node);
                dcontroller.startDialog(dialog);
                break;
        }
    }

    private void checkProgress() {
        if (paintMenu.getBtnBox().isClicked() && !ready && online) {
            readyLabel.setText("Ожидание игрока...");
            readyLabel.setVisible(true);
        } else if (ready) {
            readyLabel.setVisible(false);
        }
    }

    private void startDialogController() {
        if (paintMenu.getBtnBox().isClicked()) {
            dialog.addNode(node);
            dcontroller.startDialog(dialog);
        }
        arr.clear();
        points.clear();
        paintMenu.setResultImage();
        paintMenu.getBtnBox().setClicked(false);
    }

    public ArrayList<PixelPoint> getPoints() {
        return points;
    }

    public ArrayList<PixelPoint> getFigurePoints() {
        return figuresDatabase.getFigure(figuresDatabase.getCurFigure()).getPoints();
    }

    public ArrayList<PixelPoint> getFigureHints() {
        return figuresDatabase.getFigure(figuresDatabase.getCurFigure()).getHints();
    }

    public static boolean isDone() {
        return done;
    }

    public static void setDone(boolean done) {
        PaintState.done = done;
    }

    public static boolean isOnline() {
        return online;
    }

    public static void setOnline(boolean online) {
        PaintState.online = online;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (screenX < V_WIDTH / 1.55f && screenY < V_HEIGHT) {
            skippedPoints.add(points.size() - 1);
        }
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (screenX < V_WIDTH / 1.55f && screenY < V_HEIGHT) {
            points.add(new PixelPoint(screenX, V_HEIGHT - screenY));
            //arr.add(new Vector2(points.get(points.size()-1).getX(), points.get(points.size()-1).getY()));
            /*Vector2 newPoint = new Vector2(getPoints().get(points.size()-1).getX(), getPoints().get(points.size()-1).getY());
            if(!arr.contains(newPoint,false)){
                arr.add(newPoint);
            }*/
        }
        //System.out.println(points + " points");
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
