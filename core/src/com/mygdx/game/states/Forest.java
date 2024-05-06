package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.B2DVars.BIT_PLAYER;
import static com.mygdx.game.handlers.B2DVars.BIT_TROPA;
import static com.mygdx.game.handlers.B2DVars.PPM;
import static com.mygdx.game.handlers.GameStateManager.BATTLE;
import static com.mygdx.game.handlers.GameStateManager.FOREST;
import static com.mygdx.game.handlers.GameStateManager.MAZE;
import static com.mygdx.game.handlers.GameStateManager.MENU;
import static com.mygdx.game.handlers.GameStateManager.PAINT;
import static com.mygdx.game.handlers.GameStateManager.PLAY;
import static com.mygdx.game.handlers.GameStateManager.RHYTHM;

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
import com.mygdx.game.entities.MovableNPC;
import com.mygdx.game.entities.PlayEntities;
import com.mygdx.game.entities.Player2;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.Controllable;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.handlers.MyContactListener;

import java.util.HashMap;
import java.util.Map;

public class Forest extends GameState implements Controllable {
    private MyGdxGame game;
    private boolean debug = false;
    private World world;
    private Box2DDebugRenderer b2dr;
    private BoundedCamera b2dCam;
    private MyContactListener cl;
    private Player2 player;
    private PlayEntities entities;
    private HashMap<String, MovableNPC> movableNPCs;
    private Body removedBody;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tmr;
    private float tileSize;
    private int tileMapWidth;
    private int tileMapHeight;
    private Stage uiStage;
    private Stage controllerStage;
    private Stage darkStage;
    private Table dialogRoot;
    private DialogBox dialogBox;
    private OptionBox2 optionBox;
    private Skin skin_this;
    private InputMultiplexer multiplexer;
    private Dialog dialog;
    private DialogController dcontroller;
    public boolean canDraw;
    private float time = 0;
    private Controller controller;
    private JoyStick joyStick;
    private ShapeRenderer shapeRenderer;
    private Vector3 mouse;
    private BoundedCamera joyCam;
    private BoundedCamera forCam;
    private boolean isStopped;
    private int nextState;
    private int[] backgroundLayers = {0, 1, 2};
    private int[] foregroundLayers = {3, 4, 5, 6, 7, 8};
    private int mushrooms = 0;
    private MovableNPC npc;

    public Forest(GameStateManager gsm) {
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
        createMovableNPC();
        initDarkness();
        initFight();

        forCam = new BoundedCamera();
        forCam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT));
        forCam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
        b2dCam = new BoundedCamera();
        b2dCam.setToOrtho(false, V_WIDTH / PPM, V_HEIGHT / PPM);
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
        for (Map.Entry<String, MovableNPC> entry : movableNPCs.entrySet()) {
            movableNPCs.get(entry.getKey()).update(dt);
            movableNPCs.get(entry.getKey()).updatePos();
        }
        player.updatePL();

        if (isStopped) {
            isStopped = false;
            for (Map.Entry<String, MovableNPC> entry : movableNPCs.entrySet()) {
                movableNPCs.get(entry.getKey()).setDirection(0, 0, 20, 64, 64);
            }
            multiplexer.addProcessor(controllerStage);
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (RhythmState.isDone()) {
            RhythmState.setDone(false);
            controller.getInventory().setImgVisibility(1, true);
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
        }

        darkStage.act(dt);

        if (Gdx.input.isTouched() && !controller.isInventoryVisible() && !dialogBox.isVisible()) {
            mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            joyCam.unproject(mouse);
            joyStick.update(mouse.x, mouse.y);
        } else {
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
        forCam.setPosition(player.getPosition().x * PPM + V_WIDTH / 35, player.getPosition().y * PPM + V_HEIGHT / 35);
        forCam.update();

        tmr.setView(forCam);
        tmr.render(backgroundLayers);

        sb.setProjectionMatrix(forCam.combined);
        player.render(sb, 80f, 86.6f);
        entities.render(sb, 1.5f, 1.5f);
        for (Map.Entry<String, MovableNPC> entry : movableNPCs.entrySet()) {
            MovableNPC npc = movableNPCs.get(entry.getKey());
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
    }

    private void createPlayer() {
        BodyDef bdef = new BodyDef();
        PolygonShape ps = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        if (gsm.getLastState() == MAZE) {
            bdef.position.set(1107f / PPM, 137f / PPM);
        } else {
            bdef.position.set(207f / PPM, 737f / PPM);
        }

        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);

        ps.setAsBox(40f / PPM, 50f / PPM, new Vector2(-5.4f, -3.6f), 0);
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
        TiledMapTileLayer npcCollision = (TiledMapTileLayer) tiledMap.getLayers().get("npcCol");
        createLayer(trees, BIT_TROPA, BIT_PLAYER, true);
        createLayer(next, BIT_TROPA, BIT_PLAYER, true);
        createLayer(sign, BIT_TROPA, BIT_PLAYER, true);
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
                        (col + 0.1f) * tileSize / 2.5f,
                        (row + 0.2f) * tileSize / 2.5f);
                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[3];
                v[0] = new Vector2(-tileSize / 6, -tileSize / 10);
                v[1] = new Vector2(-tileSize / 6, tileSize / 10);
                v[2] = new Vector2(tileSize / 6, tileSize / 10);
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
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;

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
            MovableNPC npc = new MovableNPC(body, mo.getName());
            movableNPCs.put(mo.getName(), npc);
        }
        movableNPCs.get("npcForest").setNewAnimation(0, 64, 64);
        npc = movableNPCs.get("npcForest");
    }

    private void initFight() {
        skin_this = game.getSkin();
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        dialogRoot = new Table();
        dialogRoot.setFillParent(true);
        uiStage.addActor(dialogRoot);

        dialogBox = new DialogBox(skin_this);
        dialogBox.setVisible(false);

        optionBox = new OptionBox2(skin_this);
        optionBox.setVisible(false);

        Table dialogTable = new Table();
        dialogTable.add(optionBox)
                .expand().align(Align.right)
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
        controllerStage = new Stage(new ScreenViewport());
        controllerStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        controller = new Controller(skin_this);
        controller.setVisible(true);

        Table controllerRoot = new Table();
        controllerRoot.setFillParent(true);
        controllerRoot.add(controller).expand().align(Align.bottomLeft);
        controllerStage.addActor(controllerRoot);

        multiplexer.addProcessor(controllerStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initJoyStick() {
        joyCam = new BoundedCamera();
        joyCam.setBounds(0, V_WIDTH, 0, V_HEIGHT);
        joyCam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT));

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
        darkStage.getViewport().update(V_WIDTH, V_HEIGHT, true);
        darkStage.addActor(root);
    }

    public void loadStage(String s) {
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
                nextState = -1;
                mushrooms++;
                break;
            case "next":
                if (player.getPosition().x < 800f / PPM) nextState = PLAY;
                else nextState = MAZE;
                stop();
                break;
            case "null":
                npc.setDirection(0, 0, 40, 64, 64);
                node1 = new DialogNode("Ты вытащил Меч Силы!?!?!?", 0);
                node2 = new DialogNode("Ты обязан рассказать об этом.", 1);
                node3 = new DialogNode("Пошли в деревню.", 2);
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
