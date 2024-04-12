package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.*;
import static com.mygdx.game.UI.BtnBox.STATES.*;
import static com.mygdx.game.handlers.GameStateManager.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Dialog.Dialog;
import com.mygdx.game.Dialog.DialogController;
import com.mygdx.game.Dialog.DialogNode;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.OptionBox;
import com.mygdx.game.UI.PaintMenu;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.paint.DistanceCalc;
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
    private PaintMenu paintMenu;
    private InputMultiplexer multiplexer;
    private Dialog dialog;
    private DialogController dcontroller;
    private DialogNode node;
    private DistanceCalc distanceCalc;
    private Array<Vector2> arr = new Array<>();
    private PolygonSpriteBatch batch;
    private BoundedCamera paintCam;

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

        initUI();
        createSD();

        paintCam = new BoundedCamera();
        paintCam.setBounds(0, V_WIDTH, 0, V_HEIGHT);
        paintCam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT));

        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        uiStage.act(dt);
        checkBtns();
        dcontroller.update(dt);
        paintMenu.checkProgress(dt);
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

        //было изначально
        /*shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 1; i < getPoints().size() - 1; i++) {
            //shapeRenderer.rect(getPoints().get(i).getX(), getPoints().get(i).getY(), rectWidth, rectHeight);
            if (!skippedPoints.contains(i))
                shapeRenderer.rectLine(getPoints().get(i).getX(), getPoints().get(i).getY(), getPoints().get(i + 1).getX(), getPoints().get(i + 1).getY(), rectWidth);
        }

        shapeRenderer.end();*/

        batch.begin();

        /*for (int i = 0; i < getPoints().size() - 1; i++) {
            //shapeRenderer.rect(getPoints().get(i).getX(), getPoints().get(i).getY(), rectWidth, rectHeight);
            if (!skippedPoints.contains(i) && i % 2 == 0){
                sd.line(getPoints().get(i).getX(), getPoints().get(i).getY(), getPoints().get(i + 1).getX(), getPoints().get(i + 1).getY(), rectWidth);
            }
        }*/

        int count = 0;
        arr.clear();
        for (int i = 0; i < getPoints().size(); i++) {
            if (skippedPoints.contains(i)) {
                count++;
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

            /*if (!arr.contains(newPoint, false)) {
                arr.add(newPoint);
            }*/
            //arr.add(new Vector2(getPoints().get(i).getX(), getPoints().get(i).getY()));
        }
        sd.path(arr, JoinType.SMOOTH, true);

        System.out.println(count);

        batch.end();

        uiStage.draw();
    }

    @Override
    public void dispose() {

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
                gsm.setState(PLAY);
            }
        });

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

        //уже не используется, но если переделать, то можно использовать в выборе из 2 варинтов
        OptionBox optionBox = new OptionBox(game.getSkin());
        optionBox.setVisible(false);

        dialogRoot.add(dialogueBox).expand().align(Align.top).padTop(50f);

        dcontroller = new DialogController(dialogueBox, optionBox);
        dialog = new Dialog();

        multiplexer.addProcessor(uiStage);
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
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
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
            paintMenu.setAccuracy(distanceCalc.getAccuracy());
        }
        switch (paintMenu.getBtnBox().getState()) {
            case CLEAR:
                // !!! ВРЕМЕННОЕ СОХРАНЕНИЕ НА КНОПКУ ФИГУРЫ В ДБ !!!
                //game.getDbWrapper().saveFigure(new Figure("Ромб", FiguresDatabase.FIGURES_TYPES.RHOMBUS, points, points));
                //System.out.println(game.getDbWrapper().getFigures());

                skippedPoints.clear();
                points.clear();
                arr.clear();
                paintMenu.getBtnBox().setState(NON);
                break;
            case OK:
                node = new DialogNode("Получилось! Молодец!", 0);
                startDialogController();
                break;
            case WRONG:
                node = new DialogNode("Попробуй еще раз!", 0);
                startDialogController();
                break;
            case DONE:
                gsm.setState(PLAY);
                break;
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
