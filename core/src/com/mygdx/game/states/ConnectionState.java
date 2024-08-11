package com.mygdx.game.states;

import static com.mygdx.game.UI.BtnBox.STATES.EXIT;
import static com.mygdx.game.UI.BtnBox.STATES.MENU_TO_PLAY;
import static com.mygdx.game.UI.BtnBox.STATES.ONLINE;
import static com.mygdx.game.UI.BtnBox.STATES.SAVE;
import static com.mygdx.game.UI.BtnBox.STATES.SAVE_GAME;
import static com.mygdx.game.UI.BtnBox.STATES.STATISTICS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.BtnBox;
import com.mygdx.game.UI.ConnectionMenu;
import com.mygdx.game.UI.Statistics;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.GameStateManager;

public class ConnectionState extends GameState {
    private MyGdxGame game;
    private InputMultiplexer multiplexer;
    private Table root;
    private Stage uiStage;
    private BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
    private BoundedCamera conCam;

    public ConnectionState(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        multiplexer = new InputMultiplexer();

        init();
        conCam = new BoundedCamera();
        conCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        conCam.setBounds(0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());
    }

    @Override
    public void update(float dt) {
        if(game.getRequest().isJoined()){
            game.getRequest().setJoined(false);
            gsm.setState(game.getRequest().getMiniGame());
        }

        uiStage.act(dt);
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        conCam.setPosition(0, 0);
        conCam.update();

        sb.setProjectionMatrix(conCam.combined);

        uiStage.draw();
    }

    private void init() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        root = new Table();
        root.setFillParent(true);
        root.setBackground(game.getSkin().getDrawable("menuBtn_down"));
        uiStage.addActor(root);

        Image menuImg = new Image(new Texture("controller/menuBtn.png"));
        menuImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gsm.setState(GameStateManager.MENU);
            }
        });

        ConnectionMenu connectionMenu = new ConnectionMenu(game.getSkin(), gsm);

        root.add(menuImg).align(Align.topLeft).width(menuImg.getWidth() * 5.8f).height(menuImg.getHeight() * 5.8f).align(Align.topLeft);
        root.add(connectionMenu).expand().align(Align.center);

        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void dispose() {

    }
    @Override
    public void handleInput() {

    }
}
