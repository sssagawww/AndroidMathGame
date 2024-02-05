package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.*;
import com.mygdx.game.handlers.GameStateManager;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.GameStateManager.*;

public class Menu2 extends GameState {
    private MyGdxGame game;
    private Play play;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tmr;
    private float tileSize;
    private int tileMapWidth;
    private int tileMapHeight;
    private InputMultiplexer multiplexer;
    // UI
    private Table root;
    private Stage uiStage;
    private MenuOptionBox optionBox;
    //private MenuBtn2 btn;
    private BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
    /*private MenuBtn btn;
    private MenuBtn btn2;
    private MenuBtn btn3;*/
    private MenuBtnBox btnBox;

    // END UI
    public Menu2(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        play = gsm.getPlay();
        multiplexer = new InputMultiplexer();
        cam.setBounds(0, V_WIDTH, 0, V_HEIGHT); //? //4864 2688

        init();
        createLayers();
    }


    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        uiStage.act(dt);

        checkBtns();

        /*if(btn.isPressed()){
            gsm.setState(NEW_GAME);
        }
        if(btn2.isPressed()){
            System.exit(0);
        }*/
        /*if (Gdx.input.isKeyPressed(Input.Keys.Q)){
            optionBox.moveUp();
        } else if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            optionBox.moveDown();
        } else if (Gdx.input.justTouched()) {
            if (optionBox.getIndex() == 0) {
                gsm.setState(NEW_GAME);
            } else if (optionBox.getIndex() == 2) {
                System.exit(0);
            } else if (optionBox.getIndex() == 1) {
                game.save = true;
                gsm.setState(NEW_GAME);
            }
        }*/
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.setPosition(0, 0);
        cam.update();

        tmr.setView(cam);
        tmr.render();

        sb.setProjectionMatrix(cam.combined);

        sb.begin();
        font.getData().setScale(2);
        font.setColor(Color.BLACK);
        font.draw(sb, "MathGame", 415, 435);
        sb.end();

        uiStage.draw();
    }

    private void createLayers() {
        tiledMap = new TmxMapLoader().load("sprites/mystic_woods_free_2.1/menu.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap, 3.82f); // !!!
        tileSize = (int) tiledMap.getProperties().get("tilewidth");

        tileMapWidth = (int) tiledMap.getProperties().get("width");
        tileMapHeight = (int) tiledMap.getProperties().get("height");
    }

    private void init() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        btnBox = new MenuBtnBox(game.getSkin());
        btnBox.addBtn("Начать", MenuBtnBox.MENU_STATE.MENU_TO_PLAY);
        btnBox.addBtn("Продолжить", MenuBtnBox.MENU_STATE.SAVE);
        btnBox.addBtn("Выйти", MenuBtnBox.MENU_STATE.EXIT);

        Table table = new Table();

        table.add(btnBox);
        root.add(table).expand().align(Align.bottom).padBottom(100f);

        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void checkBtns() {
        switch (btnBox.getState()){
            case MENU_TO_PLAY:
                gsm.setState(NEW_GAME);
                break;
            case EXIT:
                System.exit(0);
                break;
            case SAVE:
                game.save = true;
                gsm.setState(NEW_GAME);
        }
    }

    @Override
    public void dispose() {

    }
}
