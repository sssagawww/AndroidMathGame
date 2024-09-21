package com.quenta.mobileGame.states;

import static com.quenta.mobileGame.MyGdxGame.*;
import static com.quenta.mobileGame.handlers.B2DVars.BIT_PLAYER;
import static com.quenta.mobileGame.handlers.B2DVars.BIT_TROPA;
import static com.quenta.mobileGame.handlers.B2DVars.PPM;
import static com.quenta.mobileGame.handlers.GameStateManager.MENU;
import static com.quenta.mobileGame.handlers.GameStateManager.MUSHROOMS;

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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quenta.mobileGame.MyGdxGame;
import com.quenta.mobileGame.UI.Controller;
import com.quenta.mobileGame.UI.JoyStick;
import com.quenta.mobileGame.UI.ScoreTable;
import com.quenta.mobileGame.entities.PlayEntities;
import com.quenta.mobileGame.entities.Player2;
import com.quenta.mobileGame.handlers.BoundedCamera;
import com.quenta.mobileGame.handlers.Controllable;
import com.quenta.mobileGame.handlers.GameStateManager;
import com.quenta.mobileGame.handlers.MyContactListener;
import com.quenta.mobileGame.multiplayer.MushroomsRequest;

import java.util.ArrayList;

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
    private Label mainLabel;
    private ScoreTable scoreTable;
    private Image readyBtn;
    private boolean readyBtnClicked;
    private boolean opponent;
    private float requestTime = 0;
    private float miniGameTime;
    private boolean gameOver;
    private int nextState;
    private Body removedBody;
    private Sound mushroomSound;
    private float spawnTime = 0;
    private boolean debug = false;
    private int playerScore = 0;
    private int count = 0;
    private MushroomsRequest request;
    private final int id = MyGdxGame.getPrefs().getInteger(PREF_ID);
    private int roomId;
    private boolean touchStarted = false;
    private Vector2 touchStartPos = new Vector2();

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

        initUI();
        request = gsm.game().getRequest();
        //request.leaveRoom(id, roomId);
        roomId = request.getRoomId();
        request.getOpponents().clear();
        scoreTable.addPlayerScore(MushroomsRequest.getName(), playerScore);
        scoreTable.setLabelId(roomId);

        initJoyStick();
        initController();
        createPlayer();
        createTiles();

        pickCam = new BoundedCamera();
        pickCam.setToOrtho(false, Gdx.graphics.getWidth() / (Gdx.graphics.getHeight() / 810f), 810);
        pickCam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
        b2dCam = new BoundedCamera();
        b2dCam.setToOrtho(false, Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
        b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0, (tileMapHeight * tileSize) / PPM);
    }

    @Override
    public void update(float dt) {
        if (!MyGdxGame.active) {
            request.leaveRoom(id, roomId);
        }

        if(request.isFailed()){
            request.setFailed(false);
            mainLabel.setVisible(true);
            mainLabel.setText("Ошибка! Перезайдите на сервер!");
        }

        handleInput();
        world.step(dt, 6, 2);
        player.update(dt);

        //окончание игры
        if (miniGameTime >= 30 && !request.isFailed()) {
            miniGameTime = 0;
            gameOver = true;
            request.getWinner(roomId);
        }

        if (gameOver) {
            readyBtnClicked = false;
            mainLabel.setVisible(true);
            mainLabel.setText(request.getWinnerName());
        }

        checkUsers();
        requestTime += dt;

        if (requestTime >= dt * 10 && miniGameTime < 30 && request.isDone() && !gameOver&& !request.isFailed()) {
            requestTime = 0;
            request.postInfo(id, playerScore, roomId);
            if (opponent)
                scoreTable.setPlayerScore(request.getOpponentNames(), request.getOpponentScores());
        }

        //если оба игрока готовы, то начинается игра и обновление рейтинга
        if (readyBtnClicked && request.isEveryoneReady(roomId) && !gameOver && !request.isFailed()) {
            mainLabel.setVisible(false);
            miniGameTime += dt;
            scoreTable.setPlayerScore(MushroomsRequest.getName(), playerScore);

            //появление грибов
            spawnTime += dt;
            if (spawnTime >= 1 && count <= 30) {
                count++;
                spawnTime = 0;
                spawnMushrooms();
            }
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
            controller.setMenuPressed(false);
        }

        if (Gdx.input.isTouched() && !controller.isInventoryVisible()) {
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

        uiStage.act(dt);
        controllerStage.act(dt);
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        pickCam.setPosition(player.getPosition().x * PPM, player.getPosition().y * PPM);
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
        float y = (float) Math.random() * (tileMapHeight * tileSize) / PPM * 4f;

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

        bdef.position.set(607f / PPM, 337f / PPM);

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
                Vector2[] v = new Vector2[4];
                v[0] = new Vector2(-tileSize / 6, -tileSize / 6);
                v[1] = new Vector2(-tileSize / 6, tileSize / 6);
                v[2] = new Vector2(tileSize / 6, tileSize / 6);
                v[3] = new Vector2(tileSize / 6, -tileSize / 6);
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
        joyCam.setToOrtho(false, (float) (Gdx.graphics.getWidth()), (float) (Gdx.graphics.getHeight()));

        joyStick = new JoyStick(250, 250, 250);
        joyStick = new JoyStick(250, 250, 250);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(joyCam.combined);
        mouse = new Vector3();
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void dispose() {
        game.getRequest().setJoined(false);
        game.getRequest().setCreated(false);
        player.stopSounds();
        isStopped = true;
        request.leaveRoom(id, roomId);
        scoreTable.clear();
        scoreTable.clear();
    }

    private void stop() {
        if (nextState != -1) {
            gsm.setState(nextState);
        }
        canDraw = false;
    }

    private void initUI() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);
        lstyle.font.getData().setScale(1.2f);
        lstyle.background = skin_this.getDrawable("GUI_img");

        mainLabel = new Label("\n", lstyle);
        mainLabel.setText("Ожидание игроков...");
        mainLabel.setAlignment(Align.center);

        readyBtn = new Image(skin_this.getDrawable("ok"));
        readyBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                readyBtnClicked = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                request.playerIsReady(id, roomId);
                readyBtn.setVisible(false);
            }
        });

        scoreTable = new ScoreTable(skin_this);

        Table rightTable = new Table();

        Table topTable = new Table();
        topTable.setFillParent(true);
        topTable.add(mainLabel).align(Align.top).height(Gdx.graphics.getHeight() / 10f).expand(true, false).row();
        rightTable.add(scoreTable).right().row();
        rightTable.add(readyBtn).width(Gdx.graphics.getWidth() / 15f).height(Gdx.graphics.getWidth() / 15f).right();
        topTable.add(rightTable).right().expand();

        uiStage.addActor(topTable);

        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void loadStage(String s, Body contactBody) {
        gsm.setLastState(MUSHROOMS);
        switch (s) {
            case "mushroom":
                playerScore++;
                mushroomSound.play(1f);
                break;
            default:
                break;
        }
    }

    private void checkUsers() {
        ArrayList<String> names = request.getOpponentNames();
        ArrayList<Float> scores = request.getOpponentScores();
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).isEmpty()) {
                opponent = false;
            } else if (!scoreTable.getPlayers().containsKey(names.get(i)) && !names.get(i).equals(" ")) {
                scoreTable.addPlayerScore(names.get(i), scores.get(i));
                opponent = true;
            }
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
}