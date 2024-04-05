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
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.PaintMenu;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.paint.Figures.FiguresDatabase;
import com.mygdx.game.paint.PixelPoint;

import java.util.ArrayList;

public class PaintState extends GameState implements InputProcessor {
    private ShapeRenderer shapeRenderer;
    private int rectX, rectY;
    private int rectWidth, rectHeight;
    private ArrayList<PixelPoint> points;
    private FiguresDatabase figuresDatabase;
    private Stage uiStage;
    private PaintMenu paintMenu;
    private InputMultiplexer multiplexer;

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

        initUI();

        /*ArrayList<PixelPoint> pointsList = new ArrayList<>();
        pointsList.add(new PixelPoint(110,210));
        pointsList.add(new PixelPoint(110,220));
        pointsList.add(new PixelPoint(110,230));
        Figure figure = new Figure(1,pointsList,"line");
        figure.save();*/

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
        paintMenu.checkProgress(dt);
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(1, 1, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //shapeRenderer.setProjectionMatrix(cam.combined);

        sb.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        for (int i = 0; i < points.size(); i++) {
            shapeRenderer.rect(points.get(i).getX(), points.get(i).getY(), rectWidth, rectHeight);
            //shapeRenderer.circle(points.get(i).getX(), points.get(i).getY(), rectWidth);
        }

        /*for (int i = 0; i < drawFigure(0).size(); i++) {
            shapeRenderer.rect(drawFigure(0).get(i).getX(),  drawFigure(0).get(i).getY(), rectWidth, rectHeight);
        }*/
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
            }
        });
        table.add(btn).width(100f).height(100f);
        return table;
    }

    private void checkBtns() {
        switch (paintMenu.getBtnBox().getState()) {
            case CLEAR:
                points.clear();
                paintMenu.getBtnBox().setState(NON);
                break;
            case CHECK:
            case OK: //временно
            case WRONG: //временно
                points.clear();
                //checkDistance() например, метод, проверяющий совпадение пикселей и ставящий нужный стейт ok or wrong
                //этот метод вернул ok или wrong -> setResultImage() поставил нужную картинку -> checkProgress(), который обновляется в update(),
                //отсчитывает несколько секунд, чтобы показать картинку, и запускает таймер заново
                paintMenu.setResultImage();
                break;
            case DONE:
                gsm.setState(PLAY);
                break;
        }
    }

    public ArrayList<PixelPoint> drawFigure(int index) {
        return figuresDatabase.getFigure(index).getHints();
    }

    public ArrayList<PixelPoint> getPoints() {
        return points;
    }

    public ArrayList<Integer> getPointsX() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            list.add(points.get(i).getX());
        }
        return list;
    }

    public ArrayList<Integer> getPointsY() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            list.add(points.get(i).getY());
        }
        return list;
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
        points.add(new PixelPoint(screenX - rectWidth / 2 + 20, V_HEIGHT - screenY - rectHeight / 2));
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
