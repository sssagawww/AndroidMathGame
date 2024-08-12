package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.*;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.GameStateManager;

import static com.mygdx.game.UI.BtnBox.STATES.*;
import static com.mygdx.game.handlers.GameStateManager.*;
import static com.mygdx.game.MyGdxGame.*;

public class Menu2 extends GameState {
    private final MyGdxGame game;
    private final InputMultiplexer multiplexer;
    private Stage uiStage;
    private Statistics statistics;
    private final BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
    private final GlyphLayout layout;
    private BtnBox btnBox;
    private final Sprite bg;
    private final BoundedCamera menuCam;

    public Menu2(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        multiplexer = new InputMultiplexer();

        bg = new Sprite(new Texture("UI/menu2.png"));
        font.setColor(Color.BLACK);
        layout = new GlyphLayout(font, "Quenta");

        init();
        menuCam = new BoundedCamera();
        menuCam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        menuCam.setBounds(0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());
    }


    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        uiStage.act(dt);
        checkBtns();
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        menuCam.setPosition(0, 0);
        menuCam.update();

        sb.setProjectionMatrix(menuCam.combined);

        sb.begin();
        sb.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font.getData().setScale(Gdx.graphics.getHeight() / 250f);
        layout.setText(font, "Quenta");
        font.draw(sb, layout, Gdx.graphics.getWidth() / 2f - layout.width / 2, Gdx.graphics.getHeight() / 2f + layout.height);
        sb.end();

        uiStage.draw();
    }

    private void init() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        btnBox = new BtnBox(game.getSkin());
        btnBox.addBtn("Новая игра", MENU_TO_PLAY);
        btnBox.addBtn("Продолжить", SAVE_GAME);
        btnBox.addBtn("Сетевая игра", ONLINE);
        btnBox.addBtn("Сохранить", SAVE);
        btnBox.addBtn("Статистика", STATISTICS);
        btnBox.addBtn("Выйти", EXIT);

        statistics = new Statistics(game.getSkin());
        statistics.setVisible(false);

        root.add(btnBox).align(Align.bottom).padBottom(Value.percentHeight(.1f, root)).expand();
        root.add(statistics).width(Gdx.graphics.getWidth() / 2f).right().padRight(25f);

        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void checkBtns() {
        switch (btnBox.getState()) {
            case MENU_TO_PLAY:
                BlackScreen.setFinalTitles(false);
                MyGdxGame.getPrefs().clear();
                controller.resetInventory();
                gameTime = 0;
                game.getDbWrapper().clearAll();
                gsm.setState(BLACK_SCREEN);
                break;
            case EXIT:
                Gdx.app.exit();
                System.exit(0); // <--- очень резко закрывает, будто вылет
                break;
            case SAVE_GAME:
                game.save = true;
                if (!game.getDbWrapper().getProgress().isEmpty())
                    controller.getInventory().reload(game.getDbWrapper());
                gsm.setState(MyGdxGame.getPrefs().getInteger(PREF_STATE, BLACK_SCREEN));
                break;
            case ONLINE:
                gsm.setLastState(MENU);
                gsm.setState(CONNECTION);
                break;
            case SAVE:
                gsm.setLastState(MyGdxGame.getPrefs().getInteger(PREF_STATE, BLACK_SCREEN));
                game.saveProgress();
                break;
            case STATISTICS:
                statistics.setVisible(!statistics.isVisible());
                break;
        }
        btnBox.setState(NON);
    }

    @Override
    public void dispose() {

    }
}
