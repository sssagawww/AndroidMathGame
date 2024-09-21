package com.quenta.mobileGame.states;

import static com.quenta.mobileGame.handlers.B2DVars.BIT_PLAYER;
import static com.quenta.mobileGame.handlers.B2DVars.BIT_TROPA;
import static com.quenta.mobileGame.handlers.B2DVars.PPM;
import static com.quenta.mobileGame.handlers.GameStateManager.BATTLE;
import static com.quenta.mobileGame.handlers.GameStateManager.FOREST;
import static com.quenta.mobileGame.handlers.GameStateManager.MAZE;
import static com.quenta.mobileGame.handlers.GameStateManager.MENU;
import static com.quenta.mobileGame.handlers.GameStateManager.NEW_GAME;
import static com.quenta.mobileGame.handlers.GameStateManager.PAINT;
import static com.quenta.mobileGame.handlers.GameStateManager.PLAY;
import static com.quenta.mobileGame.handlers.GameStateManager.RHYTHM;
import static com.quenta.mobileGame.MyGdxGame.*;
import static com.quenta.mobileGame.states.Play.PREF_X;
import static com.quenta.mobileGame.states.Play.PREF_Y;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quenta.mobileGame.Dialog.Dialog;
import com.quenta.mobileGame.Dialog.DialogController;
import com.quenta.mobileGame.Dialog.DialogNode;
import com.quenta.mobileGame.MyGdxGame;
import com.quenta.mobileGame.UI.Controller;
import com.quenta.mobileGame.UI.DialogBox;
import com.quenta.mobileGame.UI.JoyStick;
import com.quenta.mobileGame.UI.OptionBox2;
import com.quenta.mobileGame.entities.GameNPC;
import com.quenta.mobileGame.entities.PlayEntities;
import com.quenta.mobileGame.entities.Player2;
import com.quenta.mobileGame.handlers.BoundedCamera;
import com.quenta.mobileGame.handlers.Controllable;
import com.quenta.mobileGame.handlers.GameStateManager;
import com.quenta.mobileGame.handlers.MyContactListener;

import java.util.HashMap;
import java.util.Map;

public class Forest extends GameState implements Controllable {
    private final MyGdxGame game;
    private boolean debug = false;
    private final World world;
    private final Box2DDebugRenderer b2dr;
    private final BoundedCamera b2dCam;
    private Player2 player;
    private PlayEntities entities;
    private HashMap<String, GameNPC> movableNPCs;
    private Body removedBody;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tmr;
    private float tileSize;
    private int tileMapWidth;
    private int tileMapHeight;
    private Stage uiStage;
    private Stage darkStage;
    private DialogBox dialogBox;
    private OptionBox2 optionBox;
    private Skin skin_this;
    private final InputMultiplexer multiplexer;
    private Dialog dialog;
    private DialogController dcontroller;
    public boolean canDraw;
    private float time = 0;
    private JoyStick joyStick;
    private ShapeRenderer shapeRenderer;
    private Vector3 mouse;
    private BoundedCamera joyCam;
    private final BoundedCamera forCam;
    private boolean isStopped;
    private int nextState;
    private final int[] backgroundLayers = {0, 1, 2, 3};
    private final int[] foregroundLayers = {4, 5, 6, 7, 8, 9, 10};
    private int mushrooms = 0;
    private GameNPC npc;
    private final Sound mushroomSound;
    public static boolean progress;
    private boolean touchStarted = false;
    private final Vector2 touchStartPos = new Vector2();

    public Forest(GameStateManager gsm) {
        super(gsm);
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        game = gsm.game();
        multiplexer = new InputMultiplexer();
        MyContactListener cl = new MyContactListener(this);
        world.setContactListener(cl);
        skin_this = game.getSkin();
        mushroomSound = Gdx.audio.newSound(Gdx.files.internal("music/pick_up_mushroom2.mp3"));

        initJoyStick();
        initController();
        createPlayer();
        createTiles();
        createNPC();
        createMovableNPC();
        initDarkness();
        initFight();

        forCam = new BoundedCamera();
        forCam.setToOrtho(false, Gdx.graphics.getWidth() / (Gdx.graphics.getHeight() / 810f), 810);
        forCam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
        b2dCam = new BoundedCamera();
        b2dCam.setToOrtho(false, Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
        b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0, (tileMapHeight * tileSize) / PPM);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        handleInput();
        world.step(dt, 6, 2);
        player.update(dt);
        entities.update(dt);
        for (Map.Entry<String, GameNPC> entry : movableNPCs.entrySet()) {
            movableNPCs.get(entry.getKey()).update(dt);
            movableNPCs.get(entry.getKey()).updatePos();
        }
        player.updatePL();

        if (isStopped) {
            isStopped = false;
            if (gsm.getLastState() == PLAY) {
                player.getBody().setTransform(207f / PPM, 737f / PPM, 0);
            } else if (gsm.getLastState() == MAZE) {
                player.getBody().setTransform(1107f / PPM, 167f / PPM, 0);
            }
            for (Map.Entry<String, GameNPC> entry : movableNPCs.entrySet()) {
                movableNPCs.get(entry.getKey()).setDirection(0, 0, 20, 64, 64);
            }
            multiplexer.addProcessor(controllerStage);
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (RhythmState.isDone()) {
            RhythmState.setDone(false);
            if (RhythmState.isStrength100()) controller.getInventory().setAchievementVisibility(2);
            controller.getInventory().setImgVisibility(1, true);
            progress = true;
            npc.setDirection(1f, 0.35f, 100f, 64, 64);
            for (int i = 0; i < entities.getEntityCount(); i++) {
                if (entities.getEntity(i).getBody().getUserData().equals("sword")) {
                    entities.getEntity(i).getBody().getFixtureList().get(0).setUserData("collided");
                    removeCollisionEntity(entities.getEntity(i).getBody());
                    npc.getBody().getFixtureList().get(0).setUserData("collided");
                }
            }
        }

        if (controller.isMenuPressed()) {
            gsm.setState(MENU);
            controller.setMenuPressed(false);
        }

        darkStage.act(dt);

        if (Gdx.input.isTouched() && !controller.isInventoryVisible() && !dialogBox.isVisible()) {
            // Получаем координаты касания
            mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            joyCam.unproject(mouse);

            // Если касание только началось
            if (!touchStarted) {
                touchStarted = true;
                touchStartPos.set(mouse.x, mouse.y);

                // Устанавливаем джойстик в точку касания
                joyStick.setPos(touchStartPos.x, touchStartPos.y);
            } else {
                // Обновляем положение джойстика с учетом движения пальца
                joyStick.update(mouse.x, mouse.y);
            }
        } else {
            // Сбрасываем состояние касания
            touchStarted = false;
            // Возвращаем джойстик в исходное положение
            joyStick.setDefaultPos();
        }

        if (canDraw) {
            uiStage.act(dt);
            dcontroller.update(dt);
            time += dt;
            if (dialogBox.isFinished() && time > 2f && dcontroller.isFinished()) {
                time = 0;
                stop();
            }
        }

        controllerStage.act(dt);
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        forCam.setPosition(player.getPosition().x * PPM, player.getPosition().y * PPM);
        forCam.update();

        tmr.setView(forCam);
        tmr.render(backgroundLayers);

        sb.setProjectionMatrix(forCam.combined);
        player.render(sb, 80f, 86.6f);
        entities.render(sb, 1.5f, 1.5f);
        for (Map.Entry<String, GameNPC> entry : movableNPCs.entrySet()) {
            GameNPC npc = movableNPCs.get(entry.getKey());
            npc.render(sb, npc.getWidth() * 1.5f, npc.getHeight() * 1.5f);
        }

        tmr.render(foregroundLayers);

        if (debug) {
            b2dCam.position.set(player.getPosition().x, player.getPosition().y, 0);
            b2dCam.update();
            b2dr.render(world, b2dCam.combined);
        }

        darkStage.draw();

        joyStick.render(shapeRenderer);

        if (canDraw) {
            uiStage.draw();
            if (optionBox.isClicked()) {
                npc.setDirection(-1, -0.3f, 40, 64, 64);
                player.getBody().setLinearVelocity(-1, -0.3f);
            }
        }

        controllerStage.draw();
    }

    @Override
    public void dispose() {
        player.stopSounds();
        isStopped = true;
        gsm.setLastState(FOREST);
        MyGdxGame.getPrefs().putFloat(PREF_X, player.getPosition().x).flush();
        MyGdxGame.getPrefs().putFloat(PREF_Y, player.getPosition().y).flush();
        game.saveProgress();
    }

    private void createPlayer() {
        BodyDef bdef = new BodyDef();
        PolygonShape ps = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        if (getPrefs().getInteger(PREF_STATE, NEW_GAME) == FOREST) {
            bdef.position.x = getPrefs().getFloat(PREF_X, 207f / PPM);
            bdef.position.y = getPrefs().getFloat(PREF_Y, 737f / PPM);
        } else if (gsm.getLastState() == MAZE) {
            bdef.position.set(1107f / PPM, 167f / PPM);
        } else {
            bdef.position.set(207f / PPM, 737f / PPM);
        }

        MazeState.progress = getPrefs().getBoolean(PREF_MAZE, false);
        MazeState.hoodedRun = getPrefs().getBoolean(PREF_MAZE_HOODED, false);
        Forest.progress = getPrefs().getBoolean(PREF_FOREST, false);
        DungeonState.progress = getPrefs().getBoolean(PREF_DUNGEON, false);

        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);

        ps.setAsBox(30f / PPM, 30f / PPM, new Vector2(-2f, -1.5f), 0);
        fdef.shape = ps;
        fdef.filter.categoryBits = BIT_PLAYER;
        fdef.filter.maskBits = BIT_TROPA;
        body.createFixture(fdef).setUserData("player");
        ps.dispose();

        player = new Player2(body);
        player.setState(this);
        body.setUserData(player);
    }

    private void createTiles() {
        tiledMap = new TmxMapLoader().load("sprites/mystic_woods_free_2.1/forest.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap, 4f);
        tileSize = (int) tiledMap.getProperties().get("tilewidth");

        tileMapWidth = (int) tiledMap.getProperties().get("width");
        tileMapHeight = (int) tiledMap.getProperties().get("height");

        TiledMapTileLayer trees = (TiledMapTileLayer) tiledMap.getLayers().get("treescollision");
        TiledMapTileLayer next = (TiledMapTileLayer) tiledMap.getLayers().get("next");
        TiledMapTileLayer sign = (TiledMapTileLayer) tiledMap.getLayers().get("sign");
        TiledMapTileLayer decor = (TiledMapTileLayer) tiledMap.getLayers().get("decor2");
        TiledMapTileLayer npcCollision = (TiledMapTileLayer) tiledMap.getLayers().get("npcCol");
        createLayer(trees, BIT_TROPA, BIT_PLAYER, true);
        createLayer(next, BIT_TROPA, BIT_PLAYER, true);
        createLayer(sign, BIT_TROPA, BIT_PLAYER, true);
        createLayer(decor, BIT_TROPA, BIT_PLAYER, true);
        createLayer(npcCollision, BIT_PLAYER, BIT_TROPA, false);
    }

    private void createLayer(TiledMapTileLayer layer, short categoryBits, short maskBits, boolean data) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                if (cell == null) {
                    continue;
                }
                if (cell.getTile() == null) {
                    continue;
                }

                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set(
                        (col + 0.45f) * tileSize / 2.5f,
                        (row + 0.4f) * tileSize / 2.5f);
                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[4];
                v[0] = new Vector2(-tileSize / 6f, -tileSize / 6f);
                v[1] = new Vector2(-tileSize / 6f, tileSize / 12f);
                v[2] = new Vector2(tileSize / 4.8f, tileSize / 12f);
                v[3] = new Vector2(tileSize / 4.8f, -tileSize / 6f);
                cs.createChain(v);
                fdef.friction = 0;
                fdef.shape = cs;
                fdef.filter.categoryBits = categoryBits;
                fdef.filter.maskBits = maskBits;
                fdef.isSensor = false;
                if (data) {
                    world.createBody(bdef).createFixture(fdef).setUserData(layer.getName());
                } else {
                    world.createBody(bdef).createFixture(fdef);
                }
                cs.dispose();
            }
        }
    }

    private void createNPC() {
        MapLayer mlayer = tiledMap.getLayers().get("npcLayer");
        if (mlayer == null) return;
        entities = new PlayEntities();

        for (MapObject mo : mlayer.getObjects()) {
            if (mo.getName().equals("sword") && progress) {
                continue;
            }
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;

            float x = (float) mo.getProperties().get("x") / PPM * 4f;
            float y = (float) mo.getProperties().get("y") / PPM * 4f;

            bdef.position.set(x, y);

            Body body = world.createBody(bdef);
            FixtureDef cdef = new FixtureDef();
            CircleShape cshape = new CircleShape();
            cshape.setRadius(30f / PPM);
            cdef.shape = cshape;
            cdef.isSensor = true;
            cdef.filter.categoryBits = BIT_TROPA;
            cdef.filter.maskBits = BIT_PLAYER;
            cshape.dispose();

            body.createFixture(cdef).setUserData(mo.getName());
            entities.addEntity(body, mo.getName());
        }
    }

    private void createMovableNPC() {
        MapLayer mlayer = tiledMap.getLayers().get("movableNpcs");
        if (mlayer == null) return;
        movableNPCs = new HashMap<>();

        for (MapObject mo : mlayer.getObjects()) {
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.DynamicBody;
            float x = (float) mo.getProperties().get("x") / PPM * 4f;
            float y = (float) mo.getProperties().get("y") / PPM * 4f;
            bdef.position.set(x, y);

            Body body = world.createBody(bdef);
            FixtureDef cdef = new FixtureDef();
            CircleShape cshape = new CircleShape();
            cshape.setRadius(50f / PPM);
            cdef.shape = cshape;
            cdef.isSensor = true;
            cdef.filter.categoryBits = BIT_TROPA;
            cdef.filter.maskBits = BIT_PLAYER;
            cshape.dispose();

            body.createFixture(cdef).setUserData(mo.getName());
            GameNPC npc = new GameNPC(body, mo.getName());
            movableNPCs.put(mo.getName(), npc);
        }
        movableNPCs.get("npcForest").setNewAnimation(0, 64, 64);
        npc = movableNPCs.get("npcForest");
    }

    private void initFight() {
        skin_this = game.getSkin();
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Table dialogRoot = new Table();
        dialogRoot.setFillParent(true);
        uiStage.addActor(dialogRoot);

        dialogBox = new DialogBox(skin_this);
        dialogBox.setVisible(false);
        dialogBox.addBtn();

        optionBox = new OptionBox2(skin_this);
        optionBox.setVisible(false);

        Table dialogTable = new Table();
        dialogTable.add(optionBox)
                .expand().align(Align.right)
                .padRight((Gdx.graphics.getWidth() / 1.05f) / 5f)
                .space(8f)
                .row();
        dialogTable.add(dialogBox)
                .expand().align(Align.bottom)
                .space(8f)
                .row();

        dialogRoot.add(dialogTable).expand().align(Align.bottom).pad(15f);

        dcontroller = new DialogController(dialogBox, optionBox);
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(dcontroller);
        Gdx.input.setInputProcessor(multiplexer);

        dialog = new Dialog();
    }

    private void initController() {
        multiplexer.addProcessor(controllerStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initJoyStick() {
        joyCam = new BoundedCamera();
        joyCam.setBounds(0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());
        joyCam.setToOrtho(false, (float) (Gdx.graphics.getWidth()), (float) (Gdx.graphics.getHeight()));

        joyStick = new JoyStick(250, 250, 250);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(joyCam.combined);
        mouse = new Vector3();
    }

    private void initDarkness() {
        Image image = new Image(new Texture("UI/darkness2.png"));
        Table root = new Table();
        root.setFillParent(true);
        root.add(image).width(Gdx.graphics.getWidth()).height(Gdx.graphics.getHeight()).center();
        darkStage = new Stage(new ScreenViewport());
        darkStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        darkStage.addActor(root);
    }

    @Override
    public void loadStage(String s, Body contactBody) {
        DialogNode node1;
        gsm.setLastState(FOREST);
        switch (s) {
            case "enemy":
                node1 = new DialogNode("Враг атакует!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = BATTLE;
                canDraw = true;
                break;
            case "npc":
                node1 = new DialogNode("Начнем испытание!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = PAINT;
                canDraw = true;
                break;
            case "sword":
                node1 = new DialogNode("Вы решили вытянуть Меч Силы.", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = RHYTHM;
                canDraw = true;
                break;
            case "npcForest":
                if (mushrooms == -1) break;
                if (mushrooms >= 6) {
                    node1 = new DialogNode("Ого! Ты все собрал!", 0);
                    mushrooms = -1;
                } else {
                    node1 = new DialogNode("Ох, вот бы собрать побольше грибов...", 0);
                }
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = -1;
                canDraw = true;
                break;
            case "sign":
                node1 = new DialogNode("\"В древности, когда магия текла как река...", 0);
                DialogNode node2 = new DialogNode("Король Альдрик правил процветающими землями...", 1);
                DialogNode node3 = new DialogNode("Но однажды он погиб в схватке...", 2);
                DialogNode node4 = new DialogNode("А закаленный в огне Меч Силы, оружие короля...", 3);
                DialogNode node5 = new DialogNode("Остался навеки в камне...", 5);
                DialogNode node6 = new DialogNode("И только истинный герой может извлечь меч оттуда...\"", 6);

                node1.makeLinear(node2.getId());
                node2.makeLinear(node3.getId());
                node3.makeLinear(node4.getId());
                node4.makeLinear(node5.getId());
                node5.makeLinear(node6.getId());

                dialog.addNode(node1);
                dialog.addNode(node2);
                dialog.addNode(node3);
                dialog.addNode(node4);
                dialog.addNode(node5);
                dialog.addNode(node6);
                dcontroller.startDialog(dialog);
                nextState = -1;
                canDraw = true;
                break;
            case "mushroom":
                mushroomSound.play(1f);
                nextState = -1;
                if (mushrooms == 0) {
                    controller.getInventory().addItem("Чудесный\nгриб");
                    mushrooms++;
                    break;
                }
                mushrooms++;
                if (mushrooms >= 6) {
                    controller.getInventory().setAchievementVisibility(0);
                }
                controller.getInventory().getItem("Чудесный\nгриб").addItemCount();
                break;
            case "next":
                if (player.getPosition().x < 800f / PPM) nextState = PLAY;
                else if (MazeState.progress) nextState = MAZE;
                else break;
                stop();
                break;
            case "null":
                npc.setDirection(0, 0, 100, 64, 64);
                node1 = new DialogNode("Ты вытащил Меч Силы!?!?!?", 0);
                node2 = new DialogNode("Пошли в деревню.", 1);
                node3 = new DialogNode("Ты обязан рассказать об этом.", 2);
                node4 = new DialogNode("Давай быстрее.", 3);
                node5 = new DialogNode("Все равно идем.", 4);

                node1.makeLinear(node2.getId());
                node2.makeLinear(node3.getId());
                node3.addChoice("Пойдем!", 3);
                node3.addChoice("Нет.", 4);

                dialog.addNode(node1);
                dialog.addNode(node2);
                dialog.addNode(node3);
                dialog.addNode(node4);
                dialog.addNode(node5);
                dcontroller.startDialog(dialog);

                nextState = PLAY;
                canDraw = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void removeCollisionEntity(Body body) {
        removedBody = body;
        entities.removeEntity(removedBody);
    }

    @Override
    public Controller getController() {
        return controller;
    }

    private void stop() {
        if (nextState != -1) {
            gsm.setState(nextState);
        }
        canDraw = false;
    }

    @Override
    public JoyStick getJoyStick() {
        return joyStick;
    }
}
