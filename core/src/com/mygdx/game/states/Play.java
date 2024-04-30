package com.mygdx.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Dialog.*;
import com.mygdx.game.Dialog.Dialog;
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
import com.mygdx.game.handlers.MyContactListener;
import com.mygdx.game.handlers.GameStateManager;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
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
    private int[] backgroundLayers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private int[] foregroundLayers = {11};
    private Stage uiStage;
    private Stage controllerStage;
    private Table dialogRoot;
    private DialogBox dialogueBox;
    private OptionBox2 optionBox;
    private Skin skin_this;
    private InputMultiplexer multiplexer;
    private Dialog dialog;
    private DialogController dcontroller;
    private Music music;
    private Preferences prefs;
    public boolean canDraw;
    //private Image image = new Image(new Texture("UI/blackScreen.png"));
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
        //music = Gdx.audio.newMusic(Gdx.files.internal("music/song.wav"));
        music = Gdx.audio.newMusic(Gdx.files.internal("music/birds.mp3"));
        prefs = Gdx.app.getPreferences(PREF_NAME);
        savePlay = game.save;
        skin_this = game.getSkin();

        initFight();
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

        //нужно обновление размера экрана, и тогда будет resize всех компонентов?

        //если этот state был выгружен, то при запуске все процессы должны возобновиться (удаляются ли они в multiplexer при выгрузке или просто останавливаются?)
        if (isStopped) {
            music.play();
            isStopped = false;
            for (Map.Entry<String, MovableNPC> entry : movableNPCs.entrySet()) {
                movableNPCs.get(entry.getKey()).setDirection(0, 0, 20);
            }
            cam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
            multiplexer.addProcessor(controllerStage);
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (controller.isMenuPressed()) {
            gsm.setState(MENU);
        }

        //обновление джойстика
        if (Gdx.input.isTouched() && !controller.isInventoryVisible() && !dialogueBox.isVisible()) {
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
            if (dialogueBox.isFinished() && time > 2f && dcontroller.isFinished()) {
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
        cam.setPosition(player.getPosition().x * PPM + V_WIDTH / 35, player.getPosition().y * PPM + V_HEIGHT / 35);
        //cam.position.set(player.getPosition().x * PPM / 2, player.getPosition().y * PPM / 2, 0);
        cam.update();

        //draw map
        tmr.setView(cam);
        tmr.render(backgroundLayers);

        //draw player and npc
        sb.setProjectionMatrix(cam.combined); //https://stackoverflow.com/questions/33703663/understanding-the-libgdx-projection-matrix - объяснение
        player.render(sb, 80f, 86.6f);
        entities.render(sb, 150f, 150f);

        for (Map.Entry<String, MovableNPC> entry : movableNPCs.entrySet()) {
            MovableNPC npc = movableNPCs.get(entry.getKey());
            npc.render(sb, npc.getWidth() * 1.5f, npc.getHeight() * 1.5f);
        }

        tmr.render(foregroundLayers);

        //draw collision
        if (debug) {
            b2dCam.position.set(player.getPosition().x, player.getPosition().y, 0);
            b2dCam.update();
            b2dr.render(world, b2dCam.combined);
        }

        joyStick.render(shapeRenderer);

        //draw initFight() if battle begin
        if (canDraw) {
            uiStage.draw();
            if (optionBox.getBtnId() == 0 && optionBox.isClicked()) {
                movableNPCs.get("hooded").setDirection(1, -0.5f, 20);
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
            game.save = false;
        } else {
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

        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("borders"); //слой с границами карты
        createLayer(layer, BIT_TROPA, BIT_PLAYER);
        TiledMapTileLayer layer2 = (TiledMapTileLayer) tiledMap.getLayers().get("col");
        createLayer(layer2, BIT_PLAYER, BIT_TROPA);
        TiledMapTileLayer layer3 = (TiledMapTileLayer) tiledMap.getLayers().get("playerInvCol");
        createLayer(layer3, BIT_TROPA, BIT_PLAYER);
        TiledMapTileLayer layer4 = (TiledMapTileLayer) tiledMap.getLayers().get("water");
        createLayer(layer4, BIT_TROPA, BIT_PLAYER);
    }

    //коллизия слоя на карте (слой создаётся в Tiled)
    private void createLayer(TiledMapTileLayer layer, short categoryBits, short maskBits) {
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
        multiplexer.addProcessor(uiStage); //не нагружает ли большое кол-во процессов программу?
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
        //controllerStage.addActor(image);
        controllerStage.addActor(controllerRoot);
        //.addAction(alpha(0));

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

        optionBox = new OptionBox2(skin_this);
        optionBox.setVisible(false);

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
        multiplexer.addProcessor(uiStage);
    }

    @Override
    public void dispose() {
        save();
        music.stop();
        player.stopSounds();
        isStopped = true;
    }

    public void save() {
        prefs.putFloat(PREF_X, player.getPosition().x).flush();
        prefs.putFloat(PREF_Y, player.getPosition().y).flush();
    }

    private boolean contact = false;

    public void loadStage(String s) {
        DialogNode node1;
        //initFight();
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
                entities.setCurEntity(1);
                canDraw = true;
                break;
            case "hooded":
                if (contact) break;
                else contact = true;
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
            case "next":
                nextState = FOREST;
                //entities.getEntity(entities.getCurEntity()).setVisible(true); //почему-то вылетает из-за этого
                stop();
                break;
            case "null":
                movableNPCs.get("hooded").setDirection(0, 0, 20);
                node1 = new DialogNode("Вот мы и пришли.", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);

                nextState = MAZE;
                canDraw = true;
                break;
            case "rabbit":
                movableNPCs.get("rabbit").setTime(0);
                movableNPCs.get("rabbit").setDirection(-movableNPCs.get("rabbit").getVelx(), -movableNPCs.get("rabbit").getVely(), 50);
                break;
            default:
                break;
        }
    }

    private void stop() {
        if (nextState != -1) {
            gsm.setState(nextState);
        }
        //entities.getEntity(entities.getCurEntity()).setVisible(false);
        music.dispose();
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
