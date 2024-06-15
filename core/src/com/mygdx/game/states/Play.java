package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Dialog.*;
import com.mygdx.game.Dialog.Dialog;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.Controller;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.Inventory;
import com.mygdx.game.UI.JoyStick;
import com.mygdx.game.UI.OptionBox2;
import com.mygdx.game.db.Progress;
import com.mygdx.game.entities.MovableNPC;
import com.mygdx.game.entities.PlayEntities;
import com.mygdx.game.entities.Player2;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.Controllable;
import com.mygdx.game.handlers.MyContactListener;
import com.mygdx.game.handlers.GameStateManager;

import static com.mygdx.game.MyGdxGame.*;
import static com.mygdx.game.handlers.B2DVars.*;
import static com.mygdx.game.handlers.GameStateManager.*;

import java.util.HashMap;
import java.util.Map;

public class Play extends GameState implements Controllable {
    private MyGdxGame game;
    private boolean debug = false;
    private World world;
    private Box2DDebugRenderer b2dr;
    private BoundedCamera b2dCam;
    private MyContactListener cl;
    private Player2 player;
    private PlayEntities entities;
    private HashMap<String, MovableNPC> movableNPCs;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tmr;
    private float tileSize;
    private int tileMapWidth;
    private int tileMapHeight;
    private int[] backgroundLayers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    private int[] foregroundLayers = {13};
    private Stage uiStage;
    private Table dialogRoot;
    private DialogBox dialogBox;
    private OptionBox2 optionBox;
    private Skin skin_this;
    private InputMultiplexer multiplexer;
    private Dialog dialog;
    private DialogController dcontroller;
    private Music music;
    private Music rabbitSound;
    private Preferences prefs;
    public boolean canDraw;
    public boolean savePlay;
    private float time = 0;
    public BodyDef bdef;
    public Body contactBody;
    //private Stage controllerStage;
    //private Controller controller;
    // -------- JoyStick ----------
    private JoyStick joyStick;
    private ShapeRenderer shapeRenderer;
    private Vector3 mouse;
    private BoundedCamera joyCam;
    // --------- END JoyStick ---------
    private boolean isStopped;
    private int nextState;
    private int prevState = -123;
    private boolean contact = false;
    private TiledMapTileLayer dungeonLayer;
    private static final String PREF_X = "x";
    private static final String PREF_Y = "y";
    public static final String PREF_MAZE = "mazeProgress";
    public static final String PREF_FOREST = "forestProgress";
    public static final String PREF_DUNGEON = "dungeonProgress";

    public Play(GameStateManager gsm) {
        super(gsm);
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer(); //отрисовщик дебаг коллизии
        game = gsm.game();
        multiplexer = new InputMultiplexer();
        cl = new MyContactListener(this); //детектит коллизию
        world.setContactListener(cl);
        //music = Gdx.audio.newMusic(Gdx.files.internal("music/song.wav"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music/birds.mp3"));
        rabbitSound = Gdx.audio.newMusic(Gdx.files.internal("music/rabbit.mp3"));
        prefs = game.getPrefs();
        savePlay = game.save;
        skin_this = game.getSkin();

        initUI();
        initJoyStick();
        initController();
        createPlayer();
        createTiles();
        createNPC();
        createMovableNPC();
        createKinematicNPC();
        createMusic();

        cam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
        b2dCam = new BoundedCamera(); //рисует дебаг коллизию?
        b2dCam.setToOrtho(false, V_WIDTH / PPM, V_HEIGHT / PPM); // /2?
        b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0, (tileMapHeight * tileSize) / PPM);
        System.out.println("V_HEIGHT: " + MyGdxGame.V_HEIGHT + " player: " + player.getPosition().x + " " + player.getPosition().y);
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

        movableNPCs.get("rabbit").randomDirection(30, dt);

        player.updatePL();

        if (controller.getSoundSettings().getSliderBg().isDragging()){
            music.setVolume(getBgVolume());
        }
        if (controller.getSoundSettings().getSliderSoundEff().isDragging()){
            rabbitSound.setVolume(controller.getSoundSettings().getSliderSoundEff().getPercent());
        }

        //нужно обновление размера экрана, и тогда будет resize всех компонентов?

        //если этот state был выгружен, то при запуске все процессы должны возобновиться
        if (isStopped) {
            isStopped = false;
            //игрок выходит из подземелья не там, где зашёл
            if (gsm.getLastState() == DUNGEON) {
                player.getBody().setTransform(205f, 80f, 0);
            } else if (gsm.getLastState() == FOREST) {
                player.getBody().setTransform(475, 185f, 0);
            }

            if(gsm.getLastState() != PLAY && gsm.getLastState() != NEW_GAME){
                music.setLooping(true);
                music.play();
            }

            player.getBody().setLinearVelocity(0, 0);
            for (Map.Entry<String, MovableNPC> entry : movableNPCs.entrySet()) {
                movableNPCs.get(entry.getKey()).setDirection(0, 0, 20, 58, 58);
            }
            cam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
            multiplexer.addProcessor(controllerStage);
            multiplexer.addProcessor(dcontroller);
            multiplexer.addProcessor(uiStage);
            Gdx.input.setInputProcessor(multiplexer);

            storyNext();
        }

        if (controller.isMenuPressed()) {
            gsm.setState(MENU);
            controller.setMenuPressed(false);
        }

        //обновление джойстика
        if (Gdx.input.isTouched() && !controller.isInventoryVisible() && !dialogBox.isVisible()) {
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
            //активация ловушки
            if (nextState == DUNGEON && time > 1f) {
                player.getBody().setLinearVelocity(0, -1.5f);
                TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("trap");
                layer.setVisible(true);
            }
            if (dialogBox.isFinished() && time > 2f && dcontroller.isFinished()) {
                time = 0;
                //коллизия ловушки
                if (nextState == DUNGEON) {
                    dungeonLayer = (TiledMapTileLayer) tiledMap.getLayers().get("trap");
                    createLayer(dungeonLayer, BIT_TROPA, BIT_PLAYER, false);
                }
                stop();
            }
        }
        controllerStage.act(dt);
    }

    private float getBgVolume() {
        return controller.getSoundSettings().getSliderBg().getPercent();
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.setPosition(player.getPosition().x * PPM + V_WIDTH / 35, player.getPosition().y * PPM + V_HEIGHT / 35);
        //cam.position.set(player.getPosition().x * PPM / 2, player.getPosition().y * PPM / 2, 0);
        cam.update();

        //рисует карту из Tiled
        tmr.setView(cam);
        tmr.render(backgroundLayers);

        //рисует игрока и нпс
        sb.setProjectionMatrix(cam.combined); //https://stackoverflow.com/questions/33703663/understanding-the-libgdx-projection-matrix - объяснение
        player.render(sb, 80f, 86.6f);

        entities.render(sb, 1.5f, 1.5f);

        for (Map.Entry<String, MovableNPC> entry : movableNPCs.entrySet()) {
            MovableNPC npc = movableNPCs.get(entry.getKey());
            npc.render(sb, npc.getWidth() * 1.5f, npc.getHeight() * 1.5f);
        }

        tmr.render(foregroundLayers);

        //рисует коллизию
        if (debug) {
            b2dCam.position.set(player.getPosition().x, player.getPosition().y, 0);
            b2dCam.update();
            b2dr.render(world, b2dCam.combined);
        }

        joyStick.render(shapeRenderer);

        //рисует UI, когда произошло взаимодействие
        if (canDraw) {
            uiStage.draw();
            if (optionBox.getBtnId() == 0 && optionBox.isClicked() && contactBody.getFixtureList().get(0).getUserData().equals("hooded")) {
                movableNPCs.get("hooded").setDirection(1, -0.5f, 20, 58, 58);
                contact = true;
            } else if (contactBody.getFixtureList().get(0).getUserData().equals("npc")) {
                checkDeal();
            }
        }

        controllerStage.draw();
    }

    private void createPlayer() {
        bdef = new BodyDef();
        PolygonShape ps = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        if (savePlay) {
            bdef.position.x = prefs.getFloat(PREF_X, 607f / PPM);
            bdef.position.y = prefs.getFloat(PREF_Y, 337f / PPM);
            MazeState.progress = prefs.getBoolean(PREF_MAZE, false);
            Forest.progress = prefs.getBoolean(PREF_FOREST, false);
            DungeonState.progress = prefs.getBoolean(PREF_DUNGEON, false);
            game.save = false;
            savePlay = false;
        } else {
            prefs.clear();
            bdef.position.set(607f / PPM, 337f / PPM);
        }

        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);

        ps.setAsBox(35f / PPM, 45f / PPM, new Vector2(-5.4f, -3.6f), 0);
        fdef.shape = ps;
        fdef.filter.categoryBits = BIT_PLAYER;
        fdef.filter.maskBits = BIT_TROPA;
        body.createFixture(fdef).setUserData("player");
        ps.dispose();

        //create foot sensor - дополнительная коллизия внизу игрока
        /*ps.setAsBox(10f / PPM, 10f / PPM, new Vector2(0, -50f/PPM), 0);
        fdef.shape = ps;
        fdef.filter.categoryBits = BIT_PLAYER;
        fdef.filter.maskBits = BIT_BLOCK;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("foot");*/

        player = new Player2(body);
        player.setState(this);
        body.setUserData(player);
    }

    private void createTiles() {
        tiledMap = new TmxMapLoader().load("sprites/mystic_woods_free_2.1/map.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap, 4); // !!! размер карты
        tileSize = (int) tiledMap.getProperties().get("tilewidth");

        tileMapWidth = (int) tiledMap.getProperties().get("width");
        tileMapHeight = (int) tiledMap.getProperties().get("height");

        TiledMapTileLayer borders = (TiledMapTileLayer) tiledMap.getLayers().get("borders");
        createLayer(borders, BIT_TROPA, BIT_PLAYER, false);
        TiledMapTileLayer npcCollision = (TiledMapTileLayer) tiledMap.getLayers().get("col");
        createLayer(npcCollision, BIT_PLAYER, BIT_TROPA, false);
        TiledMapTileLayer playerInvCol = (TiledMapTileLayer) tiledMap.getLayers().get("playerInvCol");
        createLayer(playerInvCol, BIT_TROPA, BIT_PLAYER, false);
        TiledMapTileLayer water = (TiledMapTileLayer) tiledMap.getLayers().get("water");
        createLayer(water, BIT_TROPA, BIT_PLAYER, false);
        TiledMapTileLayer animated = (TiledMapTileLayer) tiledMap.getLayers().get("animated");
        createLayer(animated, BIT_TROPA, BIT_PLAYER, false);
        TiledMapTileLayer nextForest = (TiledMapTileLayer) tiledMap.getLayers().get("nextForest");
        createLayer(nextForest, BIT_TROPA, BIT_PLAYER, true);
        TiledMapTileLayer signDungeon = (TiledMapTileLayer) tiledMap.getLayers().get("signDungeon");
        createLayer(signDungeon, BIT_TROPA, BIT_PLAYER, true);
        TiledMapTileLayer signBoss = (TiledMapTileLayer) tiledMap.getLayers().get("signBoss");
        createLayer(signBoss, BIT_TROPA, BIT_PLAYER, true);
        TiledMapTileLayer signVillage = (TiledMapTileLayer) tiledMap.getLayers().get("signVillage");
        createLayer(signVillage, BIT_TROPA, BIT_PLAYER, true);
        TiledMapTileLayer signMaze = (TiledMapTileLayer) tiledMap.getLayers().get("signMaze");
        createLayer(signMaze, BIT_TROPA, BIT_PLAYER, true);
        TiledMapTileLayer nextBoss = (TiledMapTileLayer) tiledMap.getLayers().get("nextBoss");
        createLayer(nextBoss, BIT_TROPA, BIT_PLAYER, true);

        if(DungeonState.progress){
            dungeonLayer = (TiledMapTileLayer) tiledMap.getLayers().get("trap");
            createLayer(dungeonLayer, BIT_TROPA, BIT_PLAYER, false);
            dungeonLayer.setVisible(true);
        }
    }

    //коллизия слоя на карте (слой создаётся в Tiled)
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
    }

    private void createKinematicNPC() {
        MapLayer mlayer = tiledMap.getLayers().get("kinematicNpc");
        if (mlayer == null) return;

        for (MapObject mo : mlayer.getObjects()) {
            BodyDef bdef = new BodyDef();
            bdef.type = BodyDef.BodyType.KinematicBody;
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
    }

    private void createMusic() {
        music.setVolume(0.3f);
        music.setLooping(true);
        music.play();
    }

    private void initUI() {
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
        multiplexer.addProcessor(uiStage); //не нагружает ли большое кол-во процессов программу?
        multiplexer.addProcessor(dcontroller);
        Gdx.input.setInputProcessor(multiplexer);

        dialog = new Dialog();
    }

    private void initController() {
        /*controllerStage = new Stage(new ScreenViewport());
        controllerStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        //controller = new Controller(skin_this);
        controller.setVisible(true);

        Table controllerRoot = new Table();
        controllerRoot.setFillParent(true);
        controllerRoot.add(controller).expand().align(Align.bottomLeft);
        controllerStage.addActor(controllerRoot);*/

        if (savePlay) {
            controller.getInventory().reload(game.getDbWrapper());
        } else {
            gameTime = 0;
            game.getDbWrapper().clearAll();
        }

        System.out.println(game.getDbWrapper().getProgress() + " saved progress");
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

    @Override
    public void dispose() {
        save();
        music.stop();
        rabbitSound.stop();
        player.stopSounds();
        isStopped = true;
    }

    public void save() {
        prefs.putFloat(PREF_X, player.getPosition().x).flush();
        prefs.putFloat(PREF_Y, player.getPosition().y).flush();
        prefs.putBoolean(PREF_MAZE, MazeState.progress).flush();
        prefs.putBoolean(PREF_FOREST, Forest.progress).flush();
        prefs.putBoolean(PREF_DUNGEON, DungeonState.progress).flush();

        //сохранение прогресса (инвентаря)
        saveInventory();
    }

    public void saveInventory() {
        Inventory inventory = controller.getInventory();
        Progress progress = new Progress(inventory.getImgVisibility(0), inventory.getImgVisibility(1), inventory.getImgVisibility(2), inventory.getArtefacts(),
                inventory.getAchievementsVisibility(), inventory.getItems(), gameTime);
        game.getDbWrapper().saveProgress(progress);
    }

    @Override
    public void loadStage(String s, Body contactBody) {
        if (contactBody != null) this.contactBody = contactBody;
        DialogNode node1;
        gsm.setLastState(PLAY);
        switch (s) {
            case "enemy":
                node1 = new DialogNode("Враг атакует!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = BATTLE;
                canDraw = true;
                break;
            case "npc":
                npcDialog();

                nextState = -1;
                canDraw = true;
                break;
            case "hooded":
                if (contact || MazeState.progress) break;
                node1 = new DialogNode("Приветствую, путник!", 0);
                DialogNode node2 = new DialogNode("Не ожидал встретить здесь кого-то.", 1);
                DialogNode node3 = new DialogNode("Не хочешь исследовать со мной руины?", 2);
                DialogNode node4 = new DialogNode("Тогда пойдем!", 3);
                DialogNode node5 = new DialogNode("Ладно, если что...", 5);
                DialogNode node6 = new DialogNode("Ты можешь присоединиться в любой момент.", 6);

                node1.makeLinear(node2.getId());
                node2.makeLinear(node3.getId());
                node3.addChoice("Звучит интересно!", 3);
                node3.addChoice("Нет, не хочу.", 5);
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
            case "signBoss":
                node1 = new DialogNode("Северо-запад: Светлая цитадель", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = -1;
                canDraw = true;
                break;
            case "signVillage":
                node1 = new DialogNode("Северо-восток: деревня Альбора", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = -1;
                canDraw = true;
                break;
            case "signMaze":
                node1 = new DialogNode("Юго-восток: неизвестные руины", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = -1;
                canDraw = true;
                break;
            case "signDungeon":
                contactBody.getFixtureList().get(0).setUserData("collided");
                entities.removeEntity(contactBody);
                node1 = new DialogNode("Осторожно! Провал грунта!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = DUNGEON;
                canDraw = true;
                break;
            case "nextForest":
                nextState = FOREST;
                /*if(prevState == nextState) break;
                prevState = FOREST;*/
                //entities.getEntity(entities.getCurEntity()).setVisible(true); //почему-то вылетает из-за этого
                stop();
                break;
            case "nextBoss":
                if(controller.getInventory().getArtefacts() >= 3){
                    nextState = BOSSFIGHT;
                    stop();
                }
                break;
            case "null":
                movableNPCs.get("hooded").setDirection(0, 0, 20, 58, 58);
                node1 = new DialogNode("Вот мы и пришли.", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);

                nextState = MAZE;
                canDraw = true;
                break;
            case "rabbit":
                rabbitSound.play();
                controller.getInventory().setAchievementVisibility(1);
                movableNPCs.get("rabbit").setTime(0);
                movableNPCs.get("rabbit").setDirection(-movableNPCs.get("rabbit").getVelx(), -movableNPCs.get("rabbit").getVely(), 50, 58, 58);
                break;
            default:
                break;
        }
    }

    private void checkDeal() {
        if (optionBox.getBtnId() == 0 && dcontroller.getCurNode().getId() == 5) {
            if (controller.getInventory().getItem("Чудесный\nгриб") != null && controller.getInventory().getItem("Чудесный\nгриб").getCount() >= 6) {
                controller.getInventory().removeItem("Чудесный\nгриб");
                controller.getInventory().addItem("Волшебное\nзелье");
                contactBody.getFixtureList().get(0).setUserData("collided");
            }
        } else if (optionBox.getBtnId() == 0 && dcontroller.getCurNode().getId() == 7) {
            if (controller.getInventory().getItem("Чудесный\nгриб") != null && controller.getInventory().getItem("Чудесный\nгриб").getCount() >= 6) {
                controller.getInventory().getItem("Чудесный\nгриб").setCount(1);
                controller.getInventory().addItem("Волшебное\nзелье");
                contactBody.getFixtureList().get(0).setUserData("collided");
                controller.getInventory().setAchievementVisibility(4);
            }
        }
    }

    private void storyNext() {
        if (controller.isAllArtefacts()) {
            controller.setAllArtefacts(5);
            DialogNode node1 = new DialogNode("Все артефакты собраны.", 0);
            DialogNode node2 = new DialogNode("Пора победить зло.", 1);
            DialogNode node3 = new DialogNode("Нужно найти убежище Азрота Поглотителя.", 2);
            node1.makeLinear(1);
            node2.makeLinear(2);
            dialog.addNode(node1);
            dialog.addNode(node2);
            dialog.addNode(node3);
            dcontroller.startDialog(dialog);
            nextState = -1;
            canDraw = true;
        }
    }

    private void npcDialog() {
        DialogNode node1 = new DialogNode("Мое зелье настоящее чудо!", 0);
        DialogNode node2 = new DialogNode("И оно станет твоим всего за 42 гриба!", 1);
        DialogNode node3 = new DialogNode("Ну, что скажешь?", 2);
        DialogNode node4 = new DialogNode("Как!? Тогда ради тебя сделаю скидку.\n6 грибов и по рукам.\n", 3);
        DialogNode node5 = new DialogNode("Сначала принеси грибы!", 5);
        DialogNode node6 = new DialogNode("Ладно, приходи еще.", 6);
        DialogNode node8 = new DialogNode("Все равно загляни ко мне еще!", 8);

        /*if(controller.getInventory().getImgVisibility(1)){
            DialogNode node9 = new DialogNode("Ты вытащил Меч Силы?!", 9);
            DialogNode node10 = new DialogNode("Это, конечно круто, но знаешь что ещё круче?", 10);
            node9.makeLinear(node10.getId());
            node10.makeLinear(node1.getId());

            dialog.addNode(node9);
            dialog.addNode(node10);
        }*/

        if (controller.getInventory().getItem("Чудесный\nгриб") != null && controller.getInventory().getItem("Чудесный\nгриб").getCount() >= 6) {
            node5 = new DialogNode("Отлично! Забирай.", 5);
            node6 = new DialogNode("Оно тебе точно поможет в приключениях!", 6);
            DialogNode node7 = new DialogNode("Возьми хотя бы за 5!\nБольше скидок не будет.\n", 7);
            node6.makeLinear(7);
            node7.addChoice("Беру.", 5);
            dialog.addNode(node7);
        }
        node1.makeLinear(node2.getId());
        node2.makeLinear(node3.getId());
        node3.addChoice("Слишком дорого!", 3);
        node3.addChoice("Спасибо, откажусь.", 8);
        node4.addChoice("Беру.", 5);
        node4.addChoice("Снова откажусь.", 6);

        dialog.addNode(node1);
        dialog.addNode(node2);
        dialog.addNode(node3);
        dialog.addNode(node4);
        dialog.addNode(node5);
        dialog.addNode(node6);
        dialog.addNode(node8);
        dcontroller.startDialog(dialog);
    }

    @Override
    public void removeCollisionEntity(Body body) {

    }

    private void stop() {
        if (nextState != -1) {
            music.stop();
            gsm.setState(nextState);
            nextState = -1;
        }
        //entities.getEntity(entities.getCurEntity()).setVisible(false);
        canDraw = false;
    }

    public Player2 getPlayer() {
        return player;
    }

    public Controller getController() {
        return controller;
    }

    public JoyStick getJoyStick() {
        return joyStick;
    }

    public Preferences getPrefs() {
        return prefs;
    }
}
