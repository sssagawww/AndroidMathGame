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
import com.mygdx.game.UI.OptionBox;
import com.mygdx.game.entities.PlayEntities;
import com.mygdx.game.entities.Player2;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.Controllable;
import com.mygdx.game.handlers.MyContactListener;
import com.mygdx.game.handlers.GameStateManager;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.B2DVars.*;
import static com.mygdx.game.handlers.GameStateManager.BATTLE;
import static com.mygdx.game.handlers.GameStateManager.FOREST;
import static com.mygdx.game.handlers.GameStateManager.MAZE;
import static com.mygdx.game.handlers.GameStateManager.MENU;
import static com.mygdx.game.handlers.GameStateManager.PAINT;
import static com.mygdx.game.handlers.GameStateManager.RHYTHM;

public class Play extends GameState implements Controllable {
    private MyGdxGame game;
    private boolean debug = false;
    private World world;
    private Box2DDebugRenderer b2dr;
    private BoundedCamera b2dCam;
    private MyContactListener cl;
    private Player2 player;
    private PlayEntities entities;
    //private Boss boss;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tmr;
    private float tileSize;
    private int tileMapWidth;
    private int tileMapHeight;
    private Stage uiStage;
    private Stage controllerStage;
    private Stage inventoryStage;
    private Inventory inventory;
    private Table dialogRoot;
    private DialogBox dialogueBox;
    private OptionBox optionBox;
    private Skin skin_this;
    private InputMultiplexer multiplexer;
    private Dialog dialog;
    private DialogController dcontroller;
    private Music music;
    private Preferences prefs;
    public boolean canDraw;
    public boolean savePlay;
    private float time = 0;
    public BodyDef bdef;
    private Controller controller;
    // -------- JoyStick ----------
    private JoyStick joyStick;
    private ShapeRenderer shapeRenderer;
    private Vector3 mouse;
    private BoundedCamera joyCam;
    // --------- END JoyStick ---------
    private boolean isStopped;
    private int nextState;
    private static final String PREF_NAME = "position";
    private static final String PREF_X = "x";
    private static final String PREF_Y = "y";

    public Play(GameStateManager gsm) {
        super(gsm);
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer(); //отрисовщик дебаг коллизии
        game = gsm.game();
        multiplexer = new InputMultiplexer();
        cl = new MyContactListener(this); //детектит коллизию
        world.setContactListener(cl);
        music = Gdx.audio.newMusic(Gdx.files.internal("music/song.wav"));
        prefs = Gdx.app.getPreferences(PREF_NAME);
        savePlay = game.save;
        skin_this = game.getSkin();

        //initUI();
        initJoyStick();
        initController();
        initInventory();
        createPlayer();
        createTiles();
        createNPC();
        //createMusic(); //отключено, чтобы не мешало при дебаггинге

        //initFight();

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
        controllerStage.act(dt);
        if (inventory.isVisible()) inventoryStage.act(dt);
        player.update(dt);
        //boss.update(dt);
        entities.update(dt);
        player.updatePL();

        //нужно обновление размера экрана, и тогда будет resize всех компонентов?

        //если этот state был выгружен, то при запуске все процессы должны возобновиться (удаляются ли они в multiplexer при выгрузке или просто останавливаются?)
        if (isStopped) {
            isStopped = false;
            multiplexer.addProcessor(controllerStage);
            multiplexer.addProcessor(inventoryStage);
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (controller.isMenuPressed()) {
            gsm.setState(MENU);
        } else if (controller.isInventoryPressed()) {
            inventory.setVisible(true);
            controller.setVisible(false);
        }

        //можно начать бой
        if (canDraw) {
            uiStage.act(dt);
            dcontroller.update(dt);
            time += dt;
            if (dialogueBox.isFinished() && time > 2f) {
                time = 0;
                stop();
            }
        }

        //обновление джойстика
        if (Gdx.input.isTouched() && !inventory.isVisible()) {
            mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            joyCam.unproject(mouse);
            joyStick.update(mouse.x, mouse.y);
        } else {
            joyStick.setDefaultPos();
        }

    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.setPosition(player.getPosition().x * PPM + V_WIDTH / 35, player.getPosition().y * PPM + V_HEIGHT / 35);
        //cam.position.set(player.getPosition().x * PPM / 2, player.getPosition().y * PPM / 2, 0);
        cam.update();

        //draw map
        tmr.setView(cam);
        tmr.render();

        //draw player and npc
        sb.setProjectionMatrix(cam.combined); //https://stackoverflow.com/questions/33703663/understanding-the-libgdx-projection-matrix - объяснение
        player.render(sb, 80f, 86.6f);
        //boss.render(sb, 200f, 200f);
        entities.render(sb, 150f, 150f);

        //draw collision
        if (debug) {
            b2dCam.position.set(player.getPosition().x, player.getPosition().y, 0);
            b2dCam.update();
            b2dr.render(world, b2dCam.combined);
        }

        //draw initFight() if battle begin
        if (canDraw) {
            uiStage.draw();
        }

        controllerStage.draw();
        joyStick.render(shapeRenderer);
        if (inventory.isVisible()) inventoryStage.draw();
    }

    private void createPlayer() {
        bdef = new BodyDef();
        PolygonShape ps = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        if (savePlay) {
            bdef.position.x = prefs.getFloat(PREF_X, 607f / PPM);
            bdef.position.y = prefs.getFloat(PREF_Y, 337f / PPM);
            game.save = false;
        } else {
            bdef.position.set(607f / PPM, 337f / PPM);
        }

        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);

        ps.setAsBox(40f / PPM, 50f / PPM, new Vector2(-5.4f, -3.6f), 0);
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

        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("borders"); //слой с границами карты
        createLayer(layer, BIT_TROPA);
        //layer = (TiledMapTileLayer) tiledMap.getLayers().get("grass");
    }

    //коллизия слоя на карте (слой создаётся в Tiled)
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
                world.createBody(bdef).createFixture(fdef);
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
            float x = (float) mo.getProperties().get("x") / PPM * 4;
            float y = (float) mo.getProperties().get("y") / PPM * 4;
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

            /*boss = new Boss(body);
            body.setUserData(boss);
            System.out.println(body.getUserData());*/
        }
    }

    private void createMusic() {
        music.setVolume(0.9f);
        music.setLooping(true);
        music.play();
        //music.dispose();
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

        optionBox = new OptionBox(skin_this);
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
        /*DialogNode node1 = new DialogNode("Враг атакует!", 0);

        dialog.addNode(node1);
        dcontroller.startDialog(dialog);*/
    }

    private void initInventory() {
        inventoryStage = new Stage(new ScreenViewport());
        inventoryStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        inventory = new Inventory(game.getSkin(), this);
        inventory.setVisible(false);

        Table controllerRoot = new Table();
        controllerRoot.setFillParent(true);
        controllerRoot.add(inventory).expand().align(Align.center);
        inventoryStage.addActor(controllerRoot);

        multiplexer.addProcessor(inventoryStage);
        Gdx.input.setInputProcessor(multiplexer);
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
        joyCam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT)); //не хватало этой строчки

        joyStick = new JoyStick(200, 200, 200);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(joyCam.combined);
        mouse = new Vector3();
    }

    //был тестовый метод, чтобы понять работает ли диалог, можно использовать в других местах
    private void initUI() {
        skin_this = game.getSkin();
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        dialogRoot = new Table();
        dialogRoot.setFillParent(true);
        uiStage.addActor(dialogRoot);

        dialogueBox = new DialogBox(skin_this);
        dialogueBox.setVisible(false);
        /*dialogueBox.animateText("RU font doesn't support!");
        dialogueBox.isFinished();*/

        optionBox = new OptionBox(skin_this);
        optionBox.setVisible(false);
        /*optionBox.addOption("option 1");
        optionBox.addOption("option 2");
        optionBox.addOption("option 3");*/

        Table dialogTable = new Table();
        dialogTable.add(optionBox)
                .expand().align(Align.right)
                .space(8f)
                .row();
        dialogTable.add(dialogueBox)
                .expand().align(Align.bottom)
                .space(8f)
                .row();

        dialogRoot.add(dialogTable).expand().align(Align.bottom).pad(15f);

        dcontroller = new DialogController(dialogueBox, optionBox);
        multiplexer.addProcessor(dcontroller);
        Gdx.input.setInputProcessor(multiplexer);

        dialog = new Dialog();
        DialogNode node1 = new DialogNode("Привет! Это первая фраза", 0);
        DialogNode node2 = new DialogNode("И это вторая?", 1);
        DialogNode node3 = new DialogNode("Да, ты прав", 2);
        DialogNode node4 = new DialogNode("Неа, не угадал :(", 4);

        node1.makeLinear(node2.getId());
        node2.addChoice("Да", 2);
        node2.addChoice("Нет", 4);

        dialog.addNode(node1);
        dialog.addNode(node2);
        dialog.addNode(node3);
        dialog.addNode(node4);
        dcontroller.startDialog(dialog);
    }

    @Override
    public void dispose() {
        save();
    }

    public void save() {
        prefs.putFloat(PREF_X, player.getPosition().x).flush();
        prefs.putFloat(PREF_Y, player.getPosition().y).flush();
    }

    public void loadStage(String s) {
        DialogNode node1;
        initFight();
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
            case "hooded":
                node1 = new DialogNode("Идем в руины.", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = MAZE;
                canDraw = true;
                break;
            case "next":
                nextState = FOREST;
                stop();
                break;
            default:
                break;
        }
    }

    private void stop() {
        gsm.setState(nextState);
        music.dispose();
        isStopped = true;
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
}
