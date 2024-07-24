package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.*;
import com.mygdx.game.battle.Battle;
import com.mygdx.game.battle.ENTITY_LIST;
import com.mygdx.game.battle.events.BattleEvent;
import com.mygdx.game.battle.events.BattleEventPlayer;
import com.mygdx.game.battle.render_controller.BattleScreenController;
import com.mygdx.game.entities.B2DSprite;
import com.mygdx.game.entities.BattleEntity;
import com.mygdx.game.entities.SlimeBoss;
import com.mygdx.game.entities.StaticNPC;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.handlers.MyContactListener;

import java.util.ArrayDeque;
import java.util.Queue;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.B2DVars.*;
import static com.mygdx.game.handlers.GameStateManager.FOREST;
import static com.mygdx.game.handlers.GameStateManager.MAZE;
import static com.mygdx.game.handlers.GameStateManager.MENU;

public class BattleState2 extends GameState implements BattleEventPlayer {
    private MyGdxGame game;
    private World world;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tmr;
    private float tileSize;
    private int tileMapWidth;
    private int tileMapHeight;
    // UI
    private Stage uiStage;
    private Stage controllerStage;
    private Table dialogRoot;
    private DialogBox dialogBox;
    private OptionBox2 optionBox;
    private Table selectionRoot;
    private Table statusBoxRoot;
    private SelectionBtnBox selectionBtnBox;
    private StatusBox statusBox;
    private PlayerStatusBox playerStatus;
    // END UI
    private BattleScreenController bcontroller;
    private InputMultiplexer multiplexer;
    private BattleEvent currentEvent;
    private Queue<BattleEvent> queue = new ArrayDeque<BattleEvent>();
    private Battle battle;
    private B2DSprite boss;
    private Music music;
    private static boolean done;
    private static boolean bossFight;
    private static boolean enemy2;
    private BoundedCamera fightCam;

    public BattleState2(GameStateManager gsm) {
        super(gsm);
        world = new World(new Vector2(0, 0), true);
        game = gsm.game();
        multiplexer = new InputMultiplexer();

        createMusic();
        if (bossFight) {
            music.stop();
            /*game.getExampleDatabase().initializeExamples3();
            game.getStepDatabase().initializeSteps3();*/
        } else if (enemy2) {
            /*game.getExampleDatabase().initializeExamples2();
            game.getStepDatabase().initializeSteps2();*/
        } else {
            /*game.getExampleDatabase().initializeExamples();
            game.getStepDatabase().initializeSteps();*/
            game.getExampleDatabase().initializeAllExamples();
            game.getStepDatabase().initializeAllSteps();
        }

        fightCam = new BoundedCamera();
        fightCam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT));
        fightCam.setBounds(0, 4864, 0, 2688);

        battle = new Battle(BattleEntity.generateEntity("Игрок", game.getStepDatabase(), game.getExampleDatabase()),
                BattleEntity.generateEntity("Враг", game.getStepDatabase(), game.getExampleDatabase()));
        battle.setEventPlayer(this);

        //battleRenderer = new BattleRenderer(game.getAssetManager());

        initUI();
        createLayers();
        createEnemy();
        initController();

        bcontroller = new BattleScreenController(battle, queue, dialogBox, optionBox, selectionBtnBox);
        multiplexer.addProcessor(bcontroller);
        Gdx.input.setInputProcessor(multiplexer);

        battle.beginBattle();
    }


    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        //System.out.println(bcontroller.getState() + " " + currentEvent);
        world.step(dt, 6, 2);
        //dcontroller.update(dt);
        //bcontroller.update(dt); <----- only selectionBox
        while (currentEvent == null || currentEvent.finished()) {
            if (queue.peek() == null) {
                currentEvent = null;
                if (battle.getState() == Battle.STATE.READY_TO_PROGRESS) {
                    bcontroller.restart(); // <-----
                } else if (battle.getState() == Battle.STATE.RUN) {
                    music.dispose();
                    gsm.setState(gsm.getLastState());
                } else if (battle.getState() == Battle.STATE.WIN) {
                    done = true;
                    music.dispose();
                    gsm.setState(gsm.getLastState());
                } else if (battle.getState() == Battle.STATE.LOSE) {
                    music.dispose();
                    gsm.setState(gsm.getLastState());
                }
                break;
            } else {
                currentEvent = queue.poll();
                currentEvent.begin(this);
            }
        }

        if (currentEvent != null) {
            currentEvent.update(dt);
        }

        uiStage.act(dt);
        boss.update(dt);
        bcontroller.update(dt);
        controllerStage.act(dt);
    }

    @Override
    public void render() { // update cam needs to be fixed (F11)
        //Gdx.input.setInputProcessor(bcontroller);
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fightCam.setPosition(V_WIDTH / 2f, V_HEIGHT / 2f);
        fightCam.update();

        //draw map
        tmr.setView(fightCam);
        tmr.render();
        //draw enemy
        boss.render(sb, 200, 200);

        /*sb.begin();
        battleRenderer.render(sb);
        sb.end();*/

        sb.setProjectionMatrix(fightCam.combined);

        uiStage.draw();
        controllerStage.draw();
    }

    private void initUI() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        dialogRoot = new Table();
        dialogRoot.setFillParent(true);
        uiStage.addActor(dialogRoot);

        selectionRoot = new Table();
        selectionRoot.setFillParent(true);
        uiStage.addActor(selectionRoot);

        statusBoxRoot = new Table();
        statusBoxRoot.setFillParent(true);
        uiStage.addActor(statusBoxRoot);

        dialogBox = new DialogBox(game.getSkin());
        dialogBox.setVisible(false);

        optionBox = new OptionBox2(game.getSkin());
        optionBox.setVisible(false);

        /*selectionBox = new SelectionBox(game.getSkin());
        selectionBox.setVisible(false);*/

        selectionBtnBox = new SelectionBtnBox(game.getSkin());
        selectionBtnBox.setVisible(false);

        playerStatus = new PlayerStatusBox(game.getSkin());
        playerStatus.setText(battle.getPlayer().getName());

        statusBox = new StatusBox(game.getSkin());
        statusBox.setText(battle.getEnemy().getName());

        Table dialogTable = new Table();
        dialogTable.add(dialogBox)
                .expand().align(Align.top)
                .space(8f)
                .row();
        dialogTable.add(optionBox)
                .expand().align(Align.bottomRight)
                .space(8f)
                .row();

        //selectionRoot.add(selectionBox).expand().align(Align.bottom).pad(5f);
        selectionRoot.add(selectionBtnBox).expand().align(Align.bottom).pad(5f);
        dialogRoot.add(dialogTable).expand().align(Align.top);
        statusBoxRoot.add(statusBox).expand().align(Align.topLeft).pad(10f);
        statusBoxRoot.add(playerStatus).expand().align(Align.topRight).pad(10f);

        multiplexer.addProcessor(uiStage);
    }

    private void createLayers() {
        tiledMap = new TmxMapLoader().load("sprites/mystic_woods_free_2.1/fightmap2.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap, 4); // !!!
        tileSize = (int) tiledMap.getProperties().get("tilewidth");

        tileMapWidth = (int) tiledMap.getProperties().get("width");
        tileMapHeight = (int) tiledMap.getProperties().get("height");
    }

    private void createEnemy() {
        BodyDef bdef = new BodyDef();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((V_WIDTH / 2f) / PPM - 5, (V_HEIGHT / 2f) / PPM);
        Body body = world.createBody(bdef);

        boss = new StaticNPC(body, "enemy", 5f);
        if (bossFight) {
            boss = new SlimeBoss(body);
        } else if (enemy2) {
            boss = new StaticNPC(body, "enemy2", 5f);
        }
        body.setUserData(boss);
    }

    private void initController() {
        controllerStage = new Stage(new ScreenViewport());
        controllerStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.DARK_GRAY);
        lstyle.background = game.getSkin().getDrawable("GUI_img");
        lstyle.background.setMinHeight(60f);

        Label runLabel = new Label("Убежать",lstyle);
        runLabel.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gsm.setState(gsm.getLastState());
            }
        });

        Table controllerRoot = new Table();
        controllerRoot.setFillParent(true);
        controllerRoot.add(runLabel).expand().pad(5f).align(Align.bottomLeft);
        controllerStage.addActor(controllerRoot);

        multiplexer.addProcessor(controllerStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void createMusic() {
        music = Gdx.audio.newMusic(Gdx.files.internal("music/battleTheme.mp3"));
        Global.bgSounds.add(music);
        music.setVolume(0.9f);
        music.setLooping(true);
        music.play();
    }

    public static boolean isDone() {
        return done;
    }

    public static void setDone(boolean done) {
        BattleState2.done = done;
    }

    @Override
    public void dispose() {
        music.stop();
        enemy2 = false;
        bossFight = false;
    }

    @Override
    public DialogBox getDialogBox() {
        return dialogBox;
    }

    @Override
    public StatusBox getStatusBox(ENTITY_LIST entityList) {
        if (entityList == ENTITY_LIST.PLAYER) {
            return playerStatus;
        } else if (entityList == ENTITY_LIST.ENEMY) {
            return statusBox;
        } else {
            return null;
        }
    }

    @Override
    public void queueEvent(BattleEvent event) {
        queue.add(event);
    }

    public static boolean isBossFight() {
        return bossFight;
    }

    public static void setBossFight(boolean bossFight) {
        BattleState2.bossFight = bossFight;
    }

    public static boolean isEnemy2() {
        return enemy2;
    }

    public static void setEnemy2(boolean enemy2) {
        BattleState2.enemy2 = enemy2;
    }
}
