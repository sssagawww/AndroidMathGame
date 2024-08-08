package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.*;
import static com.mygdx.game.handlers.B2DVars.BIT_PLAYER;
import static com.mygdx.game.handlers.B2DVars.BIT_TROPA;
import static com.mygdx.game.handlers.B2DVars.PPM;
import static com.mygdx.game.handlers.GameStateManager.BATTLE;
import static com.mygdx.game.handlers.GameStateManager.FOREST;
import static com.mygdx.game.handlers.GameStateManager.MAZE;
import static com.mygdx.game.handlers.GameStateManager.MENU;
import static com.mygdx.game.handlers.GameStateManager.NEW_GAME;
import static com.mygdx.game.handlers.GameStateManager.PAINT;
import static com.mygdx.game.states.Play.PREF_X;
import static com.mygdx.game.states.Play.PREF_Y;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.mygdx.game.Dialog.Dialog;
import com.mygdx.game.Dialog.DialogController;
import com.mygdx.game.Dialog.DialogNode;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.Controller;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.JoyStick;
import com.mygdx.game.UI.OptionBox2;
import com.mygdx.game.entities.PlayEntities;
import com.mygdx.game.entities.Player2;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.Controllable;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.handlers.MyContactListener;

public class MazeState extends GameState implements Controllable {
    private Box2DDebugRenderer b2dr;
    private InputMultiplexer multiplexer;
    private MyContactListener cl;
    private Skin skin_this;
    private BoundedCamera b2dCam;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tmr;
    private int tileSize;
    private int tileMapWidth;
    private int tileMapHeight;
    private int[] backgroundLayers = {0, 1, 2, 3, 4, 5, 6};
    private int[] foregroundLayers = {7, 8};
    private World world;
    private BodyDef bdef;
    private Player2 player;
    private JoyStick joyStick;
    private BoundedCamera joyCam;
    private BoundedCamera mazeCam;
    private ShapeRenderer shapeRenderer;
    private Vector3 mouse;
    private Stage darkStage;
    private PlayEntities entities;
    private boolean isStopped;
    private boolean canDraw;
    private Stage uiStage;
    private Dialog dialog;
    private DialogController dcontroller;
    private float time;
    private DialogBox dialogueBox;
    private int nextState;
    private Table dialogRoot;
    private OptionBox2 optionBox;
    private Body removedBody;
    private boolean debug = false;
    public static boolean progress;
    public static boolean hoodedRun;
    private boolean touchStarted = false;
    private Vector2 touchStartPos = new Vector2();

    public MazeState(GameStateManager gsm) {
        super(gsm);
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        game = gsm.game();
        multiplexer = new InputMultiplexer();
        cl = new MyContactListener(this);
        world.setContactListener(cl);
        skin_this = game.getSkin();

        initJoyStick();
        initController();
        createPlayer();
        createTiles();
        createNPC();
        initDarkness();
        initFight();

        mazeCam = new BoundedCamera();
        mazeCam.setToOrtho(false, Gdx.graphics.getWidth() / (Gdx.graphics.getHeight() / 810f), 810);
        mazeCam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
        b2dCam = new BoundedCamera();
        b2dCam.setToOrtho(false, Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
        b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0, (tileMapHeight * tileSize) / PPM);
    }

    @Override
    public void update(float dt) {
        handleInput();
        world.step(dt, 6, 2);
        player.update(dt);
        entities.update(dt);
        player.updatePL();

        if (isStopped) {
            if (BattleState2.isDone()) {
                BattleState2.setDone(false);
                entities.removeEntity(removedBody);
                removedBody.getFixtureList().get(0).setUserData("collided");
                removedBody.getFixtureList().get(0).setSensor(true);
            }
            isStopped = false;
            multiplexer.addProcessor(controllerStage);
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (controller.isMenuPressed()) {
            gsm.setState(MENU);
            controller.setMenuPressed(false);
        }

        darkStage.act(dt);

        if (Gdx.input.isTouched() && !controller.isInventoryVisible() && !dialogueBox.isVisible()) {
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
            if (hoodedRun && dcontroller.isFinished() && progress && entities.getEntity(entities.getEntityCount() - 1).getBody().getFixtureList().get(0).getUserData().equals("hooded")) {
                removeCollisionEntity(entities.getEntity(entities.getEntityCount() - 1).getBody());
                entities.getEntity(entities.getEntityCount() - 1).getBody().getFixtureList().get(0).setSensor(true);
                entities.getEntity(entities.getEntityCount() - 1).getBody().getFixtureList().get(0).setUserData("collided");
                entities.removeEntity(removedBody);
                //entities.getEntity(entities.getEntityCount()-1).getBody().setLinearVelocity(-1,1);
            }
            if (dialogueBox.isFinished() && time > 2f && dcontroller.isFinished()) {
                time = 0;
                //entities.removeEntity(removedBody);
                stop();
            }
        }
        controllerStage.act(dt);
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mazeCam.setPosition(player.getPosition().x * PPM, player.getPosition().y * PPM);
        mazeCam.update();

        tmr.setView(mazeCam);
        tmr.render(backgroundLayers);

        sb.setProjectionMatrix(mazeCam.combined);
        entities.render(sb, 1.5f, 1.5f);
        player.render(sb, 80f, 86.6f);

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
        }

        controllerStage.draw();
    }

    private void createNPC() {
        MapLayer mlayer = tiledMap.getLayers().get("monsters");
        if (mlayer == null) return;
        entities = new PlayEntities();

        for (MapObject mo : mlayer.getObjects()) {
            if (mo.getName().equals("hooded") && hoodedRun) {
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
            cshape.setRadius(50f / PPM);
            if (mo.getName().equals("chest")) {
                cshape.setRadius(30f / PPM);
            }
            cdef.shape = cshape;
            cdef.isSensor = false;
            if (mo.getName().equals("chest")) {
                cdef.isSensor = true;
            }
            cdef.filter.categoryBits = BIT_TROPA;
            cdef.filter.maskBits = BIT_PLAYER;
            cshape.dispose();

            body.createFixture(cdef).setUserData(mo.getName());
            entities.addEntity(body, mo.getName());
        }
    }

    private void createPlayer() {
        bdef = new BodyDef();
        PolygonShape ps = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        if (game.getPrefs().getInteger(PREF_STATE, NEW_GAME) == MAZE) {
            bdef.position.x = game.getPrefs().getFloat(PREF_X, 607f / PPM);
            bdef.position.y = game.getPrefs().getFloat(PREF_Y, 337f / PPM);
        } else if (gsm.getLastState() == FOREST) {
            bdef.position.set(137f / PPM, 637f / PPM);
        } else {
            bdef.position.set(607f / PPM, 337f / PPM);
        }

        MazeState.hoodedRun = game.getPrefs().getBoolean(PREF_MAZE_HOODED, false);
        MazeState.progress = game.getPrefs().getBoolean(PREF_MAZE, false);
        Forest.progress = game.getPrefs().getBoolean(PREF_FOREST, false);
        DungeonState.progress = game.getPrefs().getBoolean(PREF_DUNGEON, false);

        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);

        ps.setAsBox(30f / PPM, 30f / PPM, new Vector2(-2f, -3.5f), 0);
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
        tiledMap = new TmxMapLoader().load("sprites/mystic_woods_free_2.1/labirint.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap, 4); // !!! размер карты
        tileSize = (int) tiledMap.getProperties().get("tilewidth");

        tileMapWidth = (int) tiledMap.getProperties().get("width");
        tileMapHeight = (int) tiledMap.getProperties().get("height");

        TiledMapTileLayer borders = (TiledMapTileLayer) tiledMap.getLayers().get("borders"); //слой с границами карты
        TiledMapTileLayer walls = (TiledMapTileLayer) tiledMap.getLayers().get("wallsMaze");
        TiledMapTileLayer next = (TiledMapTileLayer) tiledMap.getLayers().get("next");
        TiledMapTileLayer decor = (TiledMapTileLayer) tiledMap.getLayers().get("decor");

        createLayer(borders, BIT_TROPA);
        createLayer(walls, BIT_TROPA);
        createLayer(next, BIT_TROPA);
        createLayer(decor, BIT_TROPA);
    }

    private void createLayer(TiledMapTileLayer layer, short bits) {
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
                v[1] = new Vector2(-tileSize / 6f, tileSize / 4.4f);
                v[2] = new Vector2(tileSize / 4.8f, tileSize / 4.4f);
                v[3] = new Vector2(tileSize / 4.8f, -tileSize / 6f);
                cs.createChain(v);
                fdef.friction = 0;
                fdef.shape = cs;
                fdef.filter.categoryBits = BIT_TROPA;
                fdef.filter.maskBits = BIT_PLAYER;
                fdef.isSensor = false;
                world.createBody(bdef).createFixture(fdef).setUserData(layer.getName());
                cs.dispose();
            }
        }
    }

    private void initController() {
        multiplexer.addProcessor(controllerStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initJoyStick() {
        joyCam = new BoundedCamera();
        joyCam.setBounds(0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());
        joyCam.setToOrtho(false, (float) (Gdx.graphics.getWidth()), (float) (Gdx.graphics.getHeight())); //не хватало этой строчки

        joyStick = new JoyStick(200, 200, 200);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(joyCam.combined);
        mouse = new Vector3();
    }

    private void initDarkness() {
        Image image = new Image(new Texture("UI/darkness2.png"));
        Table root = new Table();
        root.setFillParent(true);
        root.add(image).center();
        darkStage = new Stage(new ScreenViewport());
        darkStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        darkStage.addActor(root);
    }

    private void initFight() {
        skin_this = game.getSkin();
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        dialogRoot = new Table();
        dialogRoot.setFillParent(true);
        uiStage.addActor(dialogRoot);

        dialogueBox = new DialogBox(skin_this);
        dialogueBox.setVisible(false);
        dialogueBox.addBtn();

        optionBox = new OptionBox2(skin_this);
        optionBox.setVisible(false);

        Table dialogTable = new Table();
        dialogTable.add(dialogueBox)
                .expand().align(Align.bottom)
                .space(8f)
                .row();

        dialogRoot.add(dialogTable).expand().align(Align.bottom).pad(15f);

        dcontroller = new DialogController(dialogueBox, optionBox);
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        dialog = new Dialog();
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void dispose() {
        player.stopSounds();
        isStopped = true;
        gsm.setLastState(MAZE);
        MyGdxGame.getPrefs().putFloat(PREF_X, player.getPosition().x).flush();
        MyGdxGame.getPrefs().putFloat(PREF_Y, player.getPosition().y).flush();
        game.saveProgress();
    }

    private void stop() {
        if (nextState != -1) {
            gsm.setState(nextState);
        }
        canDraw = false;
    }


    @Override
    public void loadStage(String s, Body contactBody) {
        DialogNode node1;
        gsm.setLastState(MAZE);
        switch (s) {
            case "enemy2":
                node1 = new DialogNode("Мрачный Мицелий атакует!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                BattleState2.setEnemy2(true);
                nextState = BATTLE;
                canDraw = true;
                removedBody = contactBody;
                break;
            case "enemy":
                node1 = new DialogNode("Булыжный Воин атакует!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = BATTLE;
                canDraw = true;
                removedBody = contactBody;
                break;
            case "npc":
                node1 = new DialogNode("Начнем испытание!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = PAINT;
                canDraw = true;
                break;
            case "chest":
                if (!progress) {
                    node1 = new DialogNode("Вы получили Кольцо Мудрости.", 0);
                    controller.getInventory().setImgVisibility(0, true);
                    dialog.addNode(node1);
                    dcontroller.startDialog(dialog);
                    nextState = -1;
                    canDraw = true;
                    progress = true;
                }
                break;
            case "hooded":
                node1 = new DialogNode("Ты справился! Ведь было несложно?", 0);
                DialogNode node2 = new DialogNode("Приключения ждут тебя впереди...", 1);
                DialogNode node3 = new DialogNode("А мне уже пора уходить.", 2);
                DialogNode node4 = new DialogNode("Пока!", 3);

                node1.makeLinear(node2.getId());
                node2.makeLinear(node3.getId());
                node3.makeLinear(node4.getId());

                dialog.addNode(node1);
                dialog.addNode(node2);
                dialog.addNode(node3);
                dialog.addNode(node4);
                dcontroller.startDialog(dialog);

                nextState = -1;
                canDraw = true;
                hoodedRun = true;
                break;
            case "next":
                nextState = FOREST;
                stop();
                break;
            default:
                break;
        }
    }

    @Override
    public void removeCollisionEntity(Body body) {
        removedBody = body;
    }

    @Override
    public Controller getController() {
        return controller;
    }

    @Override
    public JoyStick getJoyStick() {
        return joyStick;
    }
}
