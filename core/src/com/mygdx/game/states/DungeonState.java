package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.B2DVars.BIT_PLAYER;
import static com.mygdx.game.handlers.B2DVars.BIT_TROPA;
import static com.mygdx.game.handlers.B2DVars.PPM;
import static com.mygdx.game.handlers.GameStateManager.DUNGEON;
import static com.mygdx.game.handlers.GameStateManager.MENU;
import static com.mygdx.game.handlers.GameStateManager.PAINT;
import static com.mygdx.game.handlers.GameStateManager.PLAY;

import static java.lang.Thread.sleep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.mygdx.game.UI.OptionBox;
import com.mygdx.game.UI.OptionBox2;
import com.mygdx.game.entities.B2DSprite;
import com.mygdx.game.entities.PlayEntities;
import com.mygdx.game.entities.Player2;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.Controllable;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.handlers.MyContactListener;

public class DungeonState extends GameState implements Controllable {

    private Player2 player;
    private float tileMapHeight;
    private float tileSize;
    private float tileMapWidth;
    private Skin skin_this;
    private BoundedCamera b2dCam;
    private World world;
    private Box2DDebugRenderer b2dr;
    private InputMultiplexer multiplexer;
    private MyContactListener cl;
    private Music music;
    private PlayEntities entities;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tmr;
    private BodyDef bdef;
    private JoyStick joyStick;
    private BoundedCamera joyCam;
    private ShapeRenderer shapeRenderer;
    private Vector3 mouse;
    private Controller controller;
    private Stage controllerStage;
    private boolean isStopped = false;
    private boolean canDraw = false;
    private Stage uiStage;
    private DialogController dcontroller;
    private float time = 0;
    private DialogBox dialogueBox;
    private boolean debug = false;
    private Dialog dialog = new Dialog();
    private int nextState;
    private boolean earnedAmulet = false;
    private Table dialogRoot;
    private OptionBox2 optionBox;
    private boolean reloading = false;
    private boolean earnedKey = false;
    private boolean opening;
    private String doorForOpen;
    private float openingTime = 0;

    public DungeonState(GameStateManager gsm) {
        super(gsm);

        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer(); //отрисовщик дебаг коллизии
        game = gsm.game();
        multiplexer = new InputMultiplexer();
        cl = new MyContactListener(this); //детектит коллизию
        world.setContactListener(cl);
        music = Gdx.audio.newMusic(Gdx.files.internal("music/song.wav"));
        skin_this = game.getSkin();

        initJoyStick();
        initController();
        createPlayer();
        createTiles();
        createNPC();

        //initFight();

        cam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
        b2dCam = new BoundedCamera(); //рисует дебаг коллизию?
        b2dCam.setToOrtho(false, V_WIDTH / PPM, V_HEIGHT / PPM); // /2?
        b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0, (tileMapHeight * tileSize) / PPM);
    }

    @Override
    public void update(float dt) {
        handleInput();
        world.step(dt, 6, 2);
        controllerStage.act(dt);
        player.update(dt);
        if (!reloading) {
            entities.update(dt);
        }
        player.updatePL();

        if (isStopped) {
            isStopped = false;
            multiplexer.addProcessor(controllerStage);
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (controller.isMenuPressed()) {
            gsm.setState(MENU);
        }

        if (Gdx.input.isTouched()) {
            mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            joyCam.unproject(mouse);
            joyStick.update(mouse.x, mouse.y);
        } else {
            joyStick.setDefaultPos();
        }

        //можно начать бой
        if (canDraw) {
            uiStage.act(dt);
            dcontroller.update(dt);
            time += dt;
            if (dialogueBox.isFinished() && time > 2f) {
                time = 0;
                if (nextState != -1) {
                    stop();
                }
            }
        }

        if (opening) {
            openingTime += dt;
            if (openingTime > 0.6) {
                openDoor(doorForOpen);
                opening = false;
                openingTime = 0;
            }
        }

    }

    private void stop() {
        if (nextState != -1) {
            gsm.setState(nextState);

        }
        music.dispose();
        isStopped = true;
        canDraw = false;
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.setPosition(player.getPosition().x * PPM + V_WIDTH / 35, player.getPosition().y * PPM + V_HEIGHT / 35);
        //cam.position.set(player.getPosition().x * PPM / 2, player.getPosition().y * PPM / 2, 0);
        cam.update();

        tmr.setView(cam);
        tmr.render();

        sb.setProjectionMatrix(cam.combined); //https://stackoverflow.com/questions/33703663/understanding-the-libgdx-projection-matrix - объяснение
        player.render(sb, 80f, 86.6f);
        if (!reloading) {
            entities.render(sb, 4.5f, 4.5f);
        }

        if (debug) {
            b2dCam.position.set(player.getPosition().x, player.getPosition().y, 0);
            b2dCam.update();
            b2dr.render(world, b2dCam.combined);
        }

        if (canDraw) {
            uiStage.draw();
        }

        controllerStage.draw();
        joyStick.render(shapeRenderer);
    }

    private void createTiles() {
        tiledMap = new TmxMapLoader().load("sprites/mystic_woods_free_2.1/dungeon.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap, 4); // !!! размер карты
        tileSize = (int) tiledMap.getProperties().get("tilewidth");

        tileMapWidth = (int) tiledMap.getProperties().get("width");
        tileMapHeight = (int) tiledMap.getProperties().get("height");

        TiledMapTileLayer walls = (TiledMapTileLayer) tiledMap.getLayers().get("walls");
        createLayer(walls, BIT_TROPA);
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
                        (col + 0.2f) * tileSize / 2.5f,
                        (row + 0.4f) * tileSize / 2.5f);
                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[3];
                v[0] = new Vector2(-tileSize / 6, -tileSize / 6);
                v[1] = new Vector2(-tileSize / 6, tileSize / 6);
                v[2] = new Vector2(tileSize / 6, tileSize / 6);
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

    private void createPlayer() {
        bdef = new BodyDef();
        PolygonShape ps = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(607f / PPM, 337f / PPM);

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

    private void createNPC() {
        MapLayer mlayer = tiledMap.getLayers().get("objects");
        if (mlayer == null) return;
        entities = new PlayEntities();

        for (MapObject mo : mlayer.getObjects()) {
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.StaticBody;
            float x = (float) mo.getProperties().get("x") / PPM * 4;
            float y = (float) mo.getProperties().get("y") / PPM * 4;
            bdef.position.set(x, y);

            Body body = world.createBody(bdef);
            FixtureDef cdef = new FixtureDef();
            CircleShape cshape = new CircleShape();
            cshape.setRadius(50f / PPM);
            cdef.shape = cshape;
            cdef.isSensor = false;
            cdef.filter.categoryBits = BIT_TROPA;
            cdef.filter.maskBits = BIT_PLAYER;
            cshape.dispose();

            body.createFixture(cdef).setUserData(mo.getName());
            entities.addEntity(body, mo.getName());
        }
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

    private void initFight() {
        skin_this = game.getSkin();
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        dialogRoot = new Table();
        dialogRoot.setFillParent(true);
        uiStage.addActor(dialogRoot);

        dialogueBox = new DialogBox(skin_this);
        dialogueBox.setVisible(false);

        optionBox = new OptionBox2(skin_this);
        optionBox.setVisible(false);

        Table dialogTable = new Table();
        dialogTable.add(dialogueBox)
                .expand().align(Align.bottom)
                .space(8f)
                .row();

        dialogRoot.add(dialogTable).expand().align(Align.bottom).pad(15f);

        dcontroller = new DialogController(dialogueBox, optionBox);
        multiplexer.addProcessor(uiStage); //не нагружает ли большое кол-во процессов программу?
        //multiplexer.addProcessor(dcontroller);
        Gdx.input.setInputProcessor(multiplexer);

        dialog = new Dialog();
    }

    @Override
    public void loadStage(String s) {
        DialogNode node1;
        initFight();

        gsm.setLastState(DUNGEON);

        switch (s) {
            case "door1":
                node1 = new DialogNode("Дверь заперта... Ее нужно взломать!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
//                nextState = PAINT;
//                canDraw = true;

                opening = true;
                doorForOpen = s;

                break;
            case "amuletChest":
                if (!earnedAmulet) {
                    node1 = new DialogNode("Поздравляем! Вы получили Амулет Времени!", 0);
                    dialog.addNode(node1);
                    dcontroller.startDialog(dialog);
                    //add amulet to the inventory
                    earnedAmulet = true;
                }

                break;
            case "keyChest":
                if (!earnedKey) {
                    node1 = new DialogNode("Вы получили ключ. Но от чего же он?", 0);
                    dialog.addNode(node1);
                    dcontroller.startDialog(dialog);
                    //add key to inventory
                    earnedKey = true;
                }
                break;
            case "door2":
                node1 = new DialogNode("Дверь заперта... Ее нужно взломать!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
//                nextState = PAINT;
//                canDraw = true;

                opening = true;
                doorForOpen = s;

                break;
            case "door3":
                node1 = new DialogNode("Дверь заперта на ключ...", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
//                nextState = PAINT;
//                canDraw = true;

                opening = true;
                doorForOpen = s;

                break;
            case "keyDoor":
                if (earnedKey) {
                    opening = true;
                    doorForOpen = s;
                    //delete key from the inventory
                } else {
                    node1 = new DialogNode("Дверь заперта... Возможно, ключ от нее где-то рядом", 0);
                    dialog.addNode(node1);
                    dcontroller.startDialog(dialog);
                }
                break;
            case "ladder":
                if (earnedAmulet) {
                    nextState = PLAY;
                    canDraw = true;
                } else {
                    node1 = new DialogNode("Вы не до конца исследовали локацию!", 0);
                    dialog.addNode(node1);
                    dcontroller.startDialog(dialog);
                    nextState = -1;
                    canDraw = true;
                }
                break;
            default:
                break;
        }
    }

    private void openDoor(String s) {

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        MapLayer mlayer = tiledMap.getLayers().get("objects");
        if (mlayer == null) return;

        for (int i = 0; i < entities.getEntityCount(); i++) {
            B2DSprite sprite = entities.getEntity(i);

            if (!sprite.getBody().getUserData().equals(s)) {
                continue;
            }

            sprite.getBody().getFixtureList().get(0).setSensor(true);
            sprite.getBody().getFixtureList().get(0).setUserData(sprite.getBody().getFixtureList().get(0).getUserData() + "_opened");

            TextureRegion[] regions = TextureRegion.split(MyGdxGame.res.getTexture(String.valueOf(sprite.getBody().getFixtureList().get(0).getUserData())), 32, 16)[0];
            sprite.setAnimation(regions, 1 / 12f);
        }


    }

    @Override
    public void removeCollisionEntity(Body body) {

    }

    @Override
    public void handleInput() {
    }

    @Override
    public void dispose() {

    }

    @Override
    public JoyStick getJoyStick() {
        return joyStick;
    }
}

