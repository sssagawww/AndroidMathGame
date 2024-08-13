package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.*;
import static com.mygdx.game.UI.BtnBox.STATES.*;

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
import com.badlogic.gdx.scenes.scene2d.Touchable;
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
import com.mygdx.game.UI.OptionBox2;
import com.mygdx.game.UI.PaintMenu;
import com.mygdx.game.UI.ScoreTable;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.multiplayer.MushroomsRequest;
import com.mygdx.game.paint.DistanceCalc;
import com.mygdx.game.paint.Figures.FiguresDatabase;
import com.mygdx.game.paint.PixelPoint;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class PaintState extends GameState implements InputProcessor {
    private final ShapeRenderer shapeRenderer;
    private ShapeDrawer sd;
    private final int rectWidth;
    private final int rectHeight;
    private ArrayList<PixelPoint> points;
    private ArrayList<Integer> skippedPoints = new ArrayList<>();
    private FiguresDatabase figuresDatabase;
    private Stage uiStage;
    private Stage onlineStage;
    private PaintMenu paintMenu;
    private ScoreTable scoreTable;
    private InputMultiplexer multiplexer;
    private Dialog dialog;
    private DialogController dcontroller;
    private DialogNode node;
    private final DistanceCalc distanceCalc;
    private Array<Vector2> arr = new Array<>();
    private PolygonSpriteBatch batch;
    private final BoundedCamera paintCam;
    private final MushroomsRequest request;
    private Label readyLabel;
    private float requestTime = 0;
    private final int id = MyGdxGame.getPrefs().getInteger(PREF_ID);
    private static boolean online;
    private boolean btnClicked;
    private boolean ready = false;
    private float playerSum = 0;
    private boolean okBtnClicked;
    private static boolean done;
    private float time = 0;
    private int roomId;

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
            roomId = request.getRoomId();
            //request.leaveRoom(id, roomId);
            request.getOpponents().clear();
        }

        createSD();

        paintCam = new BoundedCamera();
        paintCam.setBounds(0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());
        paintCam.setToOrtho(false, (float) (Gdx.graphics.getWidth()), (float) (Gdx.graphics.getHeight()));

        Gdx.input.setInputProcessor(multiplexer);
    }

    public PaintState(GameStateManager gsm, ArrayList<FiguresDatabase.FIGURES_TYPES> figuresTypes) {
        this(gsm);
        if (figuresTypes != null) {
            figuresDatabase.loadFigures(figuresTypes);
        }
        initUI();
        if (online) {
            scoreTable.addPlayerScore(MushroomsRequest.getName(), 0);
            scoreTable.setLabelId(roomId);
        }
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        if (!MyGdxGame.active && online) {
            request.leaveRoom(id, roomId);
        }
        uiStage.act(dt);
        if (online) onlineStage.act(dt);
        checkBtns();
        dcontroller.update(dt);

        //конец игры
        if (done && online) {
            time += dt;
            if (time >= 5) {
                time = 0;
                done = false;
                gsm.setState(gsm.getLastState());
            }
        }

        //если все готовы и нажата кнопка готово - обнуляем готовность игрока
        if (ready && (okBtnClicked || btnClicked)) {
            request.setPlayerReady(id, false, roomId);
        }

        //обновление счетчика (в оффлайн - постоянно, онлайн - после того, как все зашли и готовы)
        if (btnClicked || !online) {
            paintMenu.checkProgress(dt);
        }

        if (online && request.isDone()) {
            requestTime += dt;
            checkUsers();
            if (requestTime >= dt * 10) {
                requestTime = 0;
                request.postInfo(id, playerSum, roomId);
                scoreTable.setPlayerScore(request.getOpponentNames(), request.getOpponentScores());
                scoreTable.setPlayerScore(MushroomsRequest.getName(), playerSum);
                ready = request.isEveryoneReady(roomId);
            }
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
        if (online) {
            request.leaveRoom(id, roomId);
            scoreTable.clear();
            game.getRequest().setJoined(false);
            game.getRequest().setCreated(false);
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
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        paintMenu = new PaintMenu(game.getSkin(), game);

        //кнопка выхода обратно
        Image menuImg = new Image(new Texture("controller/menuBtn.png"));
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
        onlineStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        //если мп, то добавляются доп. элементы ui
        if (online) {
            BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
            Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);
            lstyle.background = game.getSkin().getDrawable("menuBtn_down");

            //надпись с ожиданием игрока
            readyLabel = new Label("Нажмите, если готовы", lstyle);
            readyLabel.setAlignment(Align.center);
            readyLabel.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    request.playerIsReady(id, roomId);
                    return true;
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    readyLabel.setVisible(false);
                    btnClicked = true;
                }
            });

            //рейтинговая таблица
            scoreTable = new ScoreTable(game.getSkin());

            Table onlineRoot = new Table();
            onlineRoot.setFillParent(true);

            Table readyRoot = new Table();
            readyRoot.setFillParent(true);

            onlineStage.addActor(onlineRoot);
            onlineStage.addActor(readyRoot);
            onlineRoot.add(scoreTable).align(Align.topLeft).padTop(menuImg.getWidth() * 5.8f).padBottom(menuImg.getWidth()).expand().row();
            readyRoot.add(readyLabel).width(Gdx.graphics.getWidth()).height(Gdx.graphics.getHeight() / 4f).expand().center();
        }

        Table table = new Table();
        table.add(initBtn()).expand().align(Align.bottomRight).width(100f).height(100f).pad(15f);
        table.add(paintMenu).expand().align(Align.right);
        root.add(menuImg).align(Align.topLeft).width(menuImg.getWidth() * 5.8f).height(menuImg.getHeight() * 5.8f).padTop(10f).expand();
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
        //стиль для заднего фона галочки
        ImageButton.ImageButtonStyle tableStyle = new ImageButton.ImageButtonStyle();
        ImageButton table = new ImageButton(tableStyle);
        tableStyle.up = game.getSkin().getDrawable("menuBtn_up");
        tableStyle.down = game.getSkin().getDrawable("menuBtn_down");

        //кнопка-галочка
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        ImageButton btn = new ImageButton(style);
        style.up = game.getSkin().getDrawable("ok");
        style.down = game.getSkin().getDrawable("ok_down");

        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (online) {
                    request.setPlayerReady(id, true, roomId);
                }
                if (!points.isEmpty()) {
                    paintMenu.getBtnBox().setState(CHECK);
                    okBtnClicked = true;
                    btnClicked = true;
                }
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
            } else {
                playerSum += (float) (1f - distanceCalc.getAccuracy());
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
                    node = new DialogNode("Очки зачислены!", 0);
                }
                startDialogController();
                break;
            case WRONG:
                node = new DialogNode("Попробуй еще раз!", 0);
                if (online) {
                    node = new DialogNode("Очки зачислены!", 0);
                }
                startDialogController();
                break;
            case DONE:
                paintMenu.getBtnBox().setState(FINISH);
                done = true;
                if (online) {
                    //расчет победителя
                    HashMap<String, Float> players = new HashMap<>(request.getOpponents());
                    players.put(MushroomsRequest.getName(), playerSum);

                    String winner = Collections.max(players.entrySet(), Map.Entry.comparingByValue()).getKey();
                    node = new DialogNode(winner + " победил!", 0);
                } else {
                    gsm.setState(gsm.getLastState());
                }
                dialog.addNode(node);
                dcontroller.startDialog(dialog);
                break;
        }
    }

    private void checkProgress() {
        if (okBtnClicked && !ready && online) {
            readyLabel.setText("Ожидание игроков...");
            readyLabel.setVisible(true);
        } else if (ready) {
            readyLabel.setVisible(false);
        }
    }

    private void checkUsers() {
        ArrayList<String> names = request.getOpponentNames();
        ArrayList<Float> scores = request.getOpponentScores();
        for (int i = 0; i < names.size(); i++) {
            if (!scoreTable.getPlayers().containsKey(names.get(i)) && !names.get(i).equals(" ")) {
                scoreTable.addPlayerScore(names.get(i), scores.get(i));
            }
        }
    }

    private void startDialogController() {
        if (okBtnClicked) {
            dialog.addNode(node);
            dcontroller.startDialog(dialog);
        }
        arr.clear();
        points.clear();
        paintMenu.setResultImage();
        paintMenu.getBtnBox().setClicked(false);
        okBtnClicked = false;
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
        if (screenX < Gdx.graphics.getWidth() / 1.55f && screenY < Gdx.graphics.getHeight()) {
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
        if (screenX < Gdx.graphics.getWidth() / 1.55f && screenY < Gdx.graphics.getHeight()) {
            points.add(new PixelPoint(screenX, Gdx.graphics.getHeight() - screenY));
        }
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
