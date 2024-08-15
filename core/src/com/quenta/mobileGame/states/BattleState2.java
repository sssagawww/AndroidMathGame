package com.quenta.mobileGame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quenta.mobileGame.MyGdxGame;
import com.quenta.mobileGame.UI.*;
import com.quenta.mobileGame.battle.Battle;
import com.quenta.mobileGame.battle.ENTITY_LIST;
import com.quenta.mobileGame.battle.events.BattleEvent;
import com.quenta.mobileGame.battle.events.BattleEventPlayer;
import com.quenta.mobileGame.battle.render_controller.BattleScreenController;
import com.quenta.mobileGame.entities.BattleEntity;
import com.quenta.mobileGame.entities.GameNPC;
import com.quenta.mobileGame.handlers.BoundedCamera;
import com.quenta.mobileGame.handlers.GameStateManager;

import java.util.ArrayDeque;
import java.util.Queue;

import static com.quenta.mobileGame.handlers.B2DVars.*;

public class BattleState2 extends GameState implements BattleEventPlayer {
    private final MyGdxGame game;
    private final World world;
    private OrthogonalTiledMapRenderer tmr;
    private float tileSize;
    private int tileMapWidth;
    private int tileMapHeight;
    private Stage uiStage;
    private Stage controllerStage;
    private DialogBox dialogBox;
    private OptionBox2 optionBox;
    private SelectionBtnBox selectionBtnBox;
    private StatusBox statusBox;
    private StatusBox playerStatus;
    private final BattleScreenController bcontroller;
    private final InputMultiplexer multiplexer;
    private BattleEvent currentEvent;
    private final Queue<BattleEvent> queue = new ArrayDeque<>();
    private final Battle battle;
    private GameNPC boss;
    private Battle.ENEMY_STATE enemyState;
    private Music music;
    private static boolean done;
    private static boolean bossFight = false;
    private static boolean enemy2;
    private final BoundedCamera fightCam;

    public BattleState2(GameStateManager gsm) {
        super(gsm);
        world = new World(new Vector2(0, 0), true);
        game = gsm.game();
        multiplexer = new InputMultiplexer();

        createMusic();
        if (bossFight) {
            music.stop();
        }
        game.getExampleDatabase().initializeAllExamples();
        game.getStepDatabase().initializeAllSteps();

        fightCam = new BoundedCamera();
        fightCam.setToOrtho(false, (float) (Gdx.graphics.getWidth()), (float) (Gdx.graphics.getHeight()));
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
        world.step(dt, 6, 2);
        //dcontroller.update(dt);
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

        if (selectionBtnBox.isVisible()) {
            battle.setEnemyState(Battle.ENEMY_STATE.WAITING);
        }

        if (!selectionBtnBox.isVisible() && enemyState != battle.getEnemyState() && !enemy2) {
            checkEnemyAnim(battle.getEnemyState());
        } else if (enemyState != battle.getEnemyState() && !enemy2) {
            checkEnemyAnim(battle.getEnemyState());
        }

        uiStage.act(dt);
        boss.update(dt);
        bcontroller.update(dt);
        controllerStage.act(dt);
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fightCam.setPosition(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        fightCam.update();

        //отрисовка карты
        tmr.setView(fightCam);
        tmr.render();

        //отрисовка врага
        if (!enemy2) {
            boss.render(sb, 400, 400);
        } else {
            boss.render(sb, 200, 200);
        }

        sb.setProjectionMatrix(fightCam.combined);

        uiStage.draw();
        controllerStage.draw();
    }

    private void initUI() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Table dialogRoot = new Table();
        dialogRoot.setFillParent(true);
        uiStage.addActor(dialogRoot);

        Table selectionRoot = new Table();
        selectionRoot.setFillParent(true);
        uiStage.addActor(selectionRoot);

        Table statusBoxRoot = new Table();
        statusBoxRoot.setFillParent(true);
        uiStage.addActor(statusBoxRoot);

        dialogBox = new DialogBox(game.getSkin());
        dialogBox.setVisible(false);

        optionBox = new OptionBox2(game.getSkin());
        optionBox.setVisible(false);

        selectionBtnBox = new SelectionBtnBox(game.getSkin());
        selectionBtnBox.setVisible(false);

        playerStatus = new StatusBox(game.getSkin());
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

        selectionRoot.add(selectionBtnBox).expand().align(Align.bottom).pad(5f);
        dialogRoot.add(dialogTable).expand().align(Align.top);
        statusBoxRoot.add(statusBox).expand().align(Align.topLeft).pad(10f);
        statusBoxRoot.add(playerStatus).expand().align(Align.topRight).pad(10f);

        multiplexer.addProcessor(uiStage);
    }

    private void createLayers() {
        TiledMap tiledMap = new TmxMapLoader().load("sprites/mystic_woods_free_2.1/fightmap2.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap, 6);
        tileSize = (int) tiledMap.getProperties().get("tilewidth");

        tileMapWidth = (int) tiledMap.getProperties().get("width");
        tileMapHeight = (int) tiledMap.getProperties().get("height");
    }

    private void createEnemy() {
        BodyDef bdef = new BodyDef();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((Gdx.graphics.getWidth() / 2f) / PPM, (Gdx.graphics.getHeight() / 2f) / PPM);
        Body body = world.createBody(bdef);

        boss = new GameNPC(body, "enemyBattle");
        boss.setNewAnimation(0, 100, 100);
        if (bossFight) {
            boss = new GameNPC(body, "slimeBoss");
            boss.setNewAnimation(0, 32, 32);
        } else if (enemy2) {
            boss = new GameNPC(body, "enemy2");
        }
        body.setUserData(boss);
    }

    private void initController() {
        controllerStage = new Stage(new ScreenViewport());
        controllerStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);
        lstyle.font.getData().setScale(1.2f);
        lstyle.background = game.getSkin().getDrawable("GUI_img");
        lstyle.background.setMinHeight(60f);

        Label runLabel = new Label("Убежать", lstyle);
        runLabel.setAlignment(Align.center);
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
        controllerRoot.add(runLabel).expand().pad(5f).align(Align.bottomLeft).width(Gdx.graphics.getWidth() / 9f).height(Gdx.graphics.getHeight() / 13f);
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

    private void checkEnemyAnim(Battle.ENEMY_STATE state) {
        if(bossFight){
            if (state == Battle.ENEMY_STATE.WIN) {
                boss.setNewAnimation(2, 32, 32);
            } else if (state == Battle.ENEMY_STATE.HURT) {
                boss.setNewAnimation(3, 32, 32);
            } else if (state == Battle.ENEMY_STATE.LOSE) {
                boss.setNewAnimation(4, 32, 32);
            } else if (state == Battle.ENEMY_STATE.WAITING) {
                boss.setNewAnimation(0, 32, 32);
            } else if (state == Battle.ENEMY_STATE.ATTACK) {
                boss.setNewAnimation(1, 32, 32);
            } else if (state == Battle.ENEMY_STATE.MISS) {
                boss.setNewAnimation(3, 32, 32);
            }
        } else if(!enemy2){
            if (state == Battle.ENEMY_STATE.WIN) {
                boss.setNewAnimation(6, 100, 100);
            } else if (state == Battle.ENEMY_STATE.HURT) {
                boss.setNewAnimation(1, 100, 100);
            } else if (state == Battle.ENEMY_STATE.LOSE) {
                boss.setNewAnimation(8, 100, 100);
            } else if (state == Battle.ENEMY_STATE.WAITING) {
                boss.setNewAnimation(0, 100, 100);
            } else if (state == Battle.ENEMY_STATE.ATTACK) {
                boss.setNewAnimation(2, 100, 100);
            } else if (state == Battle.ENEMY_STATE.MISS) {
                boss.setNewAnimation(4, 100, 100);
            }
        }

        enemyState = state;
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

    public static void setBossFight(boolean bossFight) {
        BattleState2.bossFight = bossFight;
    }

    public static void setEnemy2(boolean enemy2) {
        BattleState2.enemy2 = enemy2;
    }
}
