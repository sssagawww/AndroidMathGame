package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.B2DVars.BIT_PLAYER;
import static com.mygdx.game.handlers.B2DVars.BIT_TROPA;
import static com.mygdx.game.handlers.B2DVars.PPM;
import static com.mygdx.game.handlers.GameStateManager.FOREST;
import static com.mygdx.game.handlers.GameStateManager.MENU;
import static com.mygdx.game.handlers.GameStateManager.MUSHROOMS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.UI.Controller;
import com.mygdx.game.UI.JoyStick;
import com.mygdx.game.entities.PlayEntities;
import com.mygdx.game.entities.Player2;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.Controllable;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.handlers.MyContactListener;
import com.mygdx.game.multiplayer.MushroomsRequest;

public class MushroomsState extends GameState implements Controllable {
    private Box2DDebugRenderer b2dr;
    private InputMultiplexer multiplexer;
    private MyContactListener cl;
    private Skin skin_this;
    private BoundedCamera b2dCam;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tmr;
    private int[] backgroundLayers = {0, 1, 2};
    private int[] foregroundLayers = {3};
    private int tileSize;
    private int tileMapWidth;
    private int tileMapHeight;
    private World world;
    private BodyDef bdef;
    private Player2 player;
    private JoyStick joyStick;
    private BoundedCamera joyCam;
    private BoundedCamera pickCam;
    private ShapeRenderer shapeRenderer;
    private Vector3 mouse;
    private PlayEntities entities;
    private boolean isStopped;
    private boolean canDraw;
    private Stage uiStage;
    private Label score;
    private float time;
    private int nextState;
    private Body removedBody;
    private Sound mushroomSound;
    private float spawnTime = 0;
    private boolean debug = false;
    private int playerScore;
    private int count = 0;
    private MushroomsRequest request;

    public MushroomsState(GameStateManager gsm) {
        super(gsm);
        mushroomSound = Gdx.audio.newSound(Gdx.files.internal("music/pick_up_mushroom2.mp3"));
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        game = gsm.game();
        multiplexer = new InputMultiplexer();
        cl = new MyContactListener(this);
        world.setContactListener(cl);
        skin_this = game.getSkin();
        entities = new PlayEntities();

        request = new MushroomsRequest();

        initJoyStick();
        initController();
        createPlayer();
        createTiles();
        initFight();

        pickCam = new BoundedCamera();
        pickCam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT));
        pickCam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
        b2dCam = new BoundedCamera();
        b2dCam.setToOrtho(false, V_WIDTH / PPM, V_HEIGHT / PPM);
        b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0, (tileMapHeight * tileSize) / PPM);
    }

    @Override
    public void update(float dt) {
        handleInput();
        world.step(dt, 6, 2);
        player.update(dt);

        spawnTime += dt;
        if (spawnTime >= 1 && count <= 30) {
            count++;
            spawnTime = 0;
            spawnMushrooms();
        }
        entities.update(dt);
        player.updatePL();

        if (isStopped) {
            isStopped = false;
            multiplexer.addProcessor(controllerStage);
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (controller.isMenuPressed()) {
            gsm.setState(MENU);
        }

        if (Gdx.input.isTouched() && !controller.isInventoryVisible()/* && !dialogueBox.isVisible()*/) {
            mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            joyCam.unproject(mouse);
            joyStick.update(mouse.x, mouse.y);
        } else {
            joyStick.setDefaultPos();
        }

        uiStage.act(dt);
        controllerStage.act(dt);
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        pickCam.setPosition(player.getPosition().x * PPM + V_WIDTH / 35, player.getPosition().y * PPM + V_HEIGHT / 35);
        pickCam.update();

        tmr.setView(pickCam);
        tmr.render(backgroundLayers);

        sb.setProjectionMatrix(pickCam.combined);
        player.render(sb, 80f, 86.6f);
        entities.render(sb, 1.5f, 1.5f);

        tmr.render(foregroundLayers);

        if (debug) {
            b2dCam.position.set(player.getPosition().x, player.getPosition().y, 0);
            b2dCam.update();
            b2dr.render(world, b2dCam.combined);
        }

        joyStick.render(shapeRenderer);

        uiStage.draw();

        controllerStage.draw();
    }

    private void spawnMushrooms() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;

        float x = (float) Math.random() * (tileMapWidth * tileSize) / PPM * 4f;
        float y = (float) Math.random() * (tileMapWidth * tileSize) / PPM * 4f;

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

        body.createFixture(cdef).setUserData("mushroom");
        entities.addEntity(body, "mushroom");
    }

    private void createPlayer() {
        bdef = new BodyDef();
        PolygonShape ps = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        if (gsm.getLastState() == FOREST) {
            bdef.position.set(107f / PPM, 637f / PPM);
        } else {
            bdef.position.set(607f / PPM, 337f / PPM);
        }

        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);

        ps.setAsBox(40f / PPM, 40f / PPM, new Vector2(-5.4f, -4.0f), 0);
        fdef.shape = ps;
        fdef.filter.categoryBits = BIT_PLAYER;
        fdef.filter.maskBits = BIT_TROPA;
        body.createFixture(fdef).setUserData("player");
        ps.dispose();

        player = new Player2(body);
        player.setState(this);
        player.setSpeed(50f);
        body.setUserData(player);
    }

    private void createTiles() {
        tiledMap = new TmxMapLoader().load("sprites/mystic_woods_free_2.1/mushrooms.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap, 4); // !!! размер карты
        tileSize = (int) tiledMap.getProperties().get("tilewidth");

        tileMapWidth = (int) tiledMap.getProperties().get("width");
        tileMapHeight = (int) tiledMap.getProperties().get("height");

        TiledMapTileLayer borders = (TiledMapTileLayer) tiledMap.getLayers().get("col");
        createLayer(borders, BIT_TROPA);
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

    private void initController() {
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
    public void handleInput() {
    }

    @Override
    public void dispose() {
        player.stopSounds();
        isStopped = true;
    }

    private void stop() {
        if (nextState != -1) {
            gsm.setState(nextState);
        }
        canDraw = false;
    }

    @Override
    public void loadStage(String s, Body contactBody) {
        gsm.setLastState(MUSHROOMS);
        switch (s) {
            case "mushroom":
                playerScore++;
                mushroomSound.play(1f);
                score.setText("Счет: " + playerScore + " || " + request.getOpponentScore());
                request.postInfo(123, playerScore);
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

    @Override
    public JoyStick getJoyStick() {
        return joyStick;
    }

    private void initFight() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);
        lstyle.background = skin_this.getDrawable("GUI_img");

        score = new Label("\n", lstyle);
        score.setText("Счет: " + playerScore + " || " + request.getOpponentScore());
        score.setAlignment(Align.center);

        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.add(score).align(Align.top).expand();

        uiStage.addActor(topTable);

        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);
    }
}
