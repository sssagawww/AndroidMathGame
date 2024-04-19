package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.GameStateManager.PLAY;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Dialog.Dialog;
import com.mygdx.game.Dialog.DialogController;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.OptionBox;
import com.mygdx.game.UI.PaintMenu;
import com.mygdx.game.UI.RhythmMenu;
import com.mygdx.game.handlers.GameStateManager;

import java.util.ArrayList;

public class RhythmState extends GameState {
    private InputMultiplexer multiplexer;
    private Stage uiStage;
    private RhythmMenu rhythmMenu;

    public RhythmState(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        multiplexer = new InputMultiplexer();

        initUI();

        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        uiStage.act(dt);
        rhythmMenu.update(dt);
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(1, 1, 1, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        uiStage.draw();
    }

    @Override
    public void dispose() {

    }

    public void initUI() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        Table root = new Table();
        root.setBackground(game.getSkin().getDrawable("bg"));
        root.setFillParent(true);
        uiStage.addActor(root);

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
                //setPlayerImage(i++ % 2);
            }
        });

        rhythmMenu = new RhythmMenu(game.getSkin());

        Table table = new Table();
        root.add(menuImg).expand().align(Align.topLeft).padTop(65f);
        root.add(rhythmMenu).align(Align.center).width(V_HEIGHT / 1.5f).height(V_HEIGHT / 1.5f);
        root.add(table).expand().align(Align.center);

        multiplexer.addProcessor(uiStage);
    }
}
