package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.*;
import com.mygdx.game.handlers.Animation;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.multiplayer.MushroomsRequest;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.UI.BtnBox.STATES.*;
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
    private Statistics statistics;
    private BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
    private GlyphLayout layout;
    private BtnBox btnBox;
    private Table onlineBtns;
    private Cell cell;
    private Texture bgImg;
    private Animation animation;
    private ConnectionMenu connectionMenu;
    private Sprite bg;
    // END UI

    public Menu2(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        play = gsm.getPlay();
        multiplexer = new InputMultiplexer();

        /*Texture tex = MyGdxGame.res.getTexture("gnomik");
        TextureRegion[] sprites = TextureRegion.split(tex, 120, 129)[3];
        animation = new Animation(sprites, 1/10f);
        bgImg = new Texture("UI/bg.png");*/

        bg = new Sprite(new Texture("UI/menu.png"));
        font.setColor(Color.BLACK);
        layout = new GlyphLayout(font, "Quenta");

        init();
        createLayers();
        cam.setBounds(0, V_WIDTH, 0, V_HEIGHT); //? //4864 2688
    }


    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        uiStage.act(dt);
        //animation.update(dt);
        checkBtns();
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.setPosition(0, 0);
        cam.update();

        /*tmr.setView(cam);
        tmr.render();*/

        sb.setProjectionMatrix(cam.combined);

        sb.begin();

        sb.draw(bg, 0, 0, V_WIDTH, V_HEIGHT);

        /*sb.draw(bgImg, 0, 0, V_WIDTH, V_HEIGHT);
        sb.draw(animation.getFrame(), V_WIDTH - V_WIDTH/3f, 0);*/

        /*font.getData().setScale(2);
        layout.setText(font, "Quenta");
        font.draw(sb, layout, V_WIDTH / 2f - layout.width / 2, V_HEIGHT / 2f + layout.height + 80);*/
        sb.end();

        uiStage.draw();
    }

    private void createLayers() {
        tiledMap = new TmxMapLoader().load("sprites/mystic_woods_free_2.1/menu.tmx");
        //tmr = new OrthogonalTiledMapRenderer(tiledMap, 3.82f); // !!!
        tmr = new OrthogonalTiledMapRenderer(tiledMap, V_WIDTH / (tiledMap.getProperties().get("width", Integer.class) * 16f));
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

        btnBox = new BtnBox(game.getSkin());
        btnBox.addBtn("Новая игра", MENU_TO_PLAY);
        btnBox.addBtn("Продолжить", SAVE_GAME);
        btnBox.addBtn("Сетевая игра", ONLINE);
        btnBox.addBtn("Сохранить", SAVE);
        btnBox.addBtn("Статистика", STATISTICS);
        btnBox.addBtn("Выйти", EXIT);

        statistics = new Statistics(game.getSkin());
        statistics.setVisible(false);

        Table table = new Table();

        table.add(btnBox);
        root.add(table).expand().align(Align.bottom).padBottom(100f).expand();
        root.add(statistics).width(V_WIDTH / 2f).right().padRight(25f);
        createOnlineBtns();

        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createOnlineBtns() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.downFontColor = Color.BLACK;

        TextButton mushrooms = new TextButton("Сбор грибов", style);
        style.up = game.getSkin().getDrawable("menuBtn_up");
        style.down = game.getSkin().getDrawable("menuBtn_down");

        mushrooms.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!connectionMenu.getText().equals("") && !connectionMenu.getText().equals("Введите IP!") && !MushroomsRequest.isUnableToConnect()) {
                    gsm.setState(MUSHROOMS);
                } else {
                    connectionMenu.getIpField().setMessageText("Введите IP!");
                }
            }
        });

        TextButton drawings = new TextButton("Рисование", style);
        style.up = game.getSkin().getDrawable("menuBtn_up");
        style.down = game.getSkin().getDrawable("menuBtn_down");

        drawings.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!connectionMenu.getText().equals("") && !connectionMenu.getText().equals("Введите IP!") && !MushroomsRequest.isUnableToConnect()) {
                    PaintState.setOnline(true);
                    gsm.setState(PAINT);
                } else {
                    connectionMenu.getIpField().setMessageText("Введите IP!");
                }
            }
        });

        Image exitImage = new Image(game.getSkin().getDrawable("wrong"));
        exitImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                onlineBtns.setVisible(false);
            }
        });

        connectionMenu = new ConnectionMenu(game.getSkin(), gsm);

        Label.LabelStyle lStyle = new Label.LabelStyle(font, Color.BLACK);
        Label textLabel = new Label("Режимы рассчитаны на 2 игроков.", lStyle);
        textLabel.setFontScale(0.8f);

        onlineBtns = new Table(game.getSkin());
        onlineBtns.setVisible(false);
        onlineBtns.setBackground("menuBtn_up");

        onlineBtns.add(exitImage).align(Align.right).width(exitImage.getWidth() * 3).height(exitImage.getHeight() * 3).expand().row();
        onlineBtns.add(connectionMenu).row();
        onlineBtns.add(mushrooms).space(10f).row();
        onlineBtns.add(drawings).row();
        onlineBtns.add(textLabel).padBottom(exitImage.getHeight() * 3).padTop(exitImage.getHeight() * 3).row();
        cell = root.getCell(statistics);
    }

    private void checkBtns() {
        switch (btnBox.getState()) {
            case MENU_TO_PLAY:
                gsm.setState(BLACK_SCREEN);
                break;
            case EXIT:
                Gdx.app.exit();
                System.exit(0); // <--- очень резко закрывает, будто вылет
                break;
            case SAVE_GAME:
                game.save = true;
                gsm.setState(NEW_GAME);
                break;
            case ONLINE:
                onlineBtns.setVisible(!onlineBtns.isVisible());
                cell.setActor(onlineBtns);
                break;
            case SAVE:
                play.save();
                break;
            case STATISTICS:
                statistics.setVisible(!statistics.isVisible());
                cell.setActor(statistics);
                break;
        }
        btnBox.setState(NON);
    }

    @Override
    public void dispose() {

    }
}
