package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.*;
import static com.mygdx.game.UI.BtnBox.STATES.*;
import static com.mygdx.game.handlers.GameStateManager.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Dialog.Dialog;
import com.mygdx.game.Dialog.DialogController;
import com.mygdx.game.Dialog.DialogNode;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.OptionBox;
import com.mygdx.game.UI.PaintMenu;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.paint.DistanceCalc;
import com.mygdx.game.paint.Figures.Figure;
import com.mygdx.game.paint.Figures.FiguresDatabase;
import com.mygdx.game.paint.PixelPoint;

import java.util.ArrayList;
import java.util.Arrays;

public class PaintState extends GameState implements InputProcessor {
    private ShapeRenderer shapeRenderer;
    private int rectX, rectY;
    private int rectWidth, rectHeight;
    private ArrayList<PixelPoint> points;
    private FiguresDatabase figuresDatabase;
    private Stage uiStage;
    private PaintMenu paintMenu;
    private InputMultiplexer multiplexer;
    private Dialog dialog;
    private DialogController dcontroller;
    private DialogNode node;
    private DistanceCalc distanceCalc;

    public PaintState(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        figuresDatabase = game.getFiguresDatabase();
        shapeRenderer = new ShapeRenderer();
        rectX = 0;
        rectY = 0;
        rectWidth = 10;
        rectHeight = 10;

        points = new ArrayList<>();
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);

        distanceCalc = new DistanceCalc(this);

        initUI();

        //cam.setBounds(0, V_WIDTH, 0, V_HEIGHT); //?

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

        //shapeRenderer.setProjectionMatrix(cam.combined);

        sb.begin();
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < getFigureHints().size(); i++) {
            shapeRenderer.rect(getFigureHints().get(i).getX(), getFigureHints().get(i).getY(), rectWidth, rectHeight);
        }

        shapeRenderer.setColor(Color.BLACK);
        for (int i = 0; i < getPoints().size(); i++) {
            shapeRenderer.rect(getPoints().get(i).getX(), getPoints().get(i).getY(), rectWidth, rectHeight);
            //shapeRenderer.circle(points.get(i).getX(), points.get(i).getY(), rectWidth);
        }

        shapeRenderer.end();
        sb.end();

        uiStage.draw();
    }

    @Override
    public void dispose() {

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

                points.clear();
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
        points.clear();
        paintMenu.setResultImage();
        paintMenu.getBtnBox().setClicked(false);
    }

    public ArrayList<PixelPoint> getPoints() {
        return points;
    }

    /*public ArrayList<PixelPoint> getNewPoints() {
        ArrayList<PixelPoint> list = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            list.add(new PixelPoint(points.get(i).getX()*2, points.get(i).getY()*2));
        }
        return list;
    }*/

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
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(screenX < V_WIDTH/1.55f && screenY < V_HEIGHT){
            points.add(new PixelPoint(screenX, V_HEIGHT - screenY));
        }
        System.out.println(points + " points");
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
