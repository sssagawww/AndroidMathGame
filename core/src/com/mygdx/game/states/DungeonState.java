package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.PREF_DUNGEON;
import static com.mygdx.game.MyGdxGame.PREF_FOREST;
import static com.mygdx.game.MyGdxGame.PREF_MAZE;
import static com.mygdx.game.MyGdxGame.PREF_MAZE_HOODED;
import static com.mygdx.game.MyGdxGame.PREF_STATE;
import static com.mygdx.game.paint.Figures.FiguresDatabase.FIGURES_TYPES;
import static com.mygdx.game.handlers.B2DVars.BIT_PLAYER;
import static com.mygdx.game.handlers.B2DVars.BIT_TROPA;
import static com.mygdx.game.handlers.B2DVars.PPM;
import static com.mygdx.game.handlers.GameStateManager.*;
import static com.mygdx.game.states.Play.PREF_X;
import static com.mygdx.game.states.Play.PREF_Y;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.mygdx.game.UI.OptionBox2;
import com.mygdx.game.entities.B2DSprite;
import com.mygdx.game.entities.PlayEntities;
import com.mygdx.game.entities.Player2;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.Controllable;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.handlers.MyContactListener;

import java.util.ArrayList;
import java.util.Arrays;

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
    private BoundedCamera dunCam;
    private BoundedCamera joyCam;
    private ShapeRenderer shapeRenderer;
    private Vector3 mouse;
    private boolean isStopped = false;
    private boolean canDraw = false;
    private Stage uiStage;
    private DialogController dcontroller;
    private float time = 0;
    private DialogBox dialogBox;
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
    private Sound openingDoorSound;
    private long soundId;
    public static boolean progress;
    private boolean touchStarted = false;
    private Vector2 touchStartPos = new Vector2();

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

        initFight();
        initJoyStick();
        initController();
        createPlayer();
        createTiles();
        createNPC();

        if(controller.getInventory().getItems().containsKey("Ключ")){
            earnedKey = true;
        }
        if(controller.getInventory().getImgVisibility(2)){
            earnedAmulet = true;
        }

        openingDoorSound = Gdx.audio.newSound(Gdx.files.internal("music/door_opening.mp3"));

        dunCam = new BoundedCamera();
        dunCam.setToOrtho(false, (float) (Gdx.graphics.getWidth()), (float) (Gdx.graphics.getHeight()));
        dunCam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
        b2dCam = new BoundedCamera(); //рисует дебаг коллизию?
        b2dCam.setToOrtho(false, Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM); // /2?
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
            if (opening || PaintState.isDone()) {
                PaintState.setDone(false);
                openDoor(doorForOpen);
                opening = false;
            }
            multiplexer.addProcessor(controllerStage);
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (controller.isMenuPressed()) {
            gsm.setState(MENU);
            controller.setMenuPressed(false);
        }

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
        dunCam.setPosition(player.getPosition().x * PPM, player.getPosition().y * PPM);
        dunCam.update();

        tmr.setView(dunCam);
        tmr.render();

        sb.setProjectionMatrix(dunCam.combined);
        player.render(sb, 80f, 86.6f);
        if (!reloading) {
            entities.render(sb, 4.5f, 4.5f);
        }

        if (debug) {
            b2dCam.position.set(player.getPosition().x, player.getPosition().y, 0);
            b2dCam.update();
            b2dr.render(world, b2dCam.combined);
        }

        joyStick.render(shapeRenderer);

        if (canDraw) {
            uiStage.draw();
        }

        controllerStage.draw();
    }

    private void stop() {
        if (nextState != -1) {
            gsm.setState(nextState);
        }
        music.dispose();
        isStopped = true;
        canDraw = false;
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

        if(game.getPrefs().getInteger(PREF_STATE, NEW_GAME) == DUNGEON){
            bdef.position.x = game.getPrefs().getFloat(PREF_X, 607f / PPM);
            bdef.position.y = game.getPrefs().getFloat(PREF_Y, 337f / PPM);
        } else {
            bdef.position.set(607f / PPM, 337f / PPM);
        }

        MazeState.progress = game.getPrefs().getBoolean(PREF_MAZE, false);
        MazeState.hoodedRun = game.getPrefs().getBoolean(PREF_MAZE_HOODED, false);
        Forest.progress = game.getPrefs().getBoolean(PREF_FOREST, false);
        DungeonState.progress = game.getPrefs().getBoolean(PREF_DUNGEON, false);

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
        multiplexer.addProcessor(controllerStage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initJoyStick() {
        joyCam = new BoundedCamera();
        joyCam.setBounds(0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());
        joyCam.setToOrtho(false, (float) (Gdx.graphics.getWidth()), (float) (Gdx.graphics.getHeight()));

        joyStick = new JoyStick(200, 200, 200);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(joyCam.combined);
        mouse = new Vector3();
    }

    private void initFight() {
        skin_this = game.getSkin();
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        dialogRoot = new Table();
        dialogRoot.setFillParent(true);
        uiStage.addActor(dialogRoot);

        dialogBox = new DialogBox(skin_this);
        dialogBox.addBtn();
        dialogBox.setVisible(false);

        optionBox = new OptionBox2(skin_this);
        optionBox.setVisible(false);

        Table dialogTable = new Table();
        dialogTable.add(dialogBox)
                .expand().align(Align.bottom)
                .space(8f)
                .row();

        dialogRoot.add(dialogTable).expand().align(Align.bottom).pad(15f);

        dcontroller = new DialogController(dialogBox, optionBox);
        multiplexer.addProcessor(uiStage); //не нагружает ли большое кол-во процессов программу?
        //multiplexer.addProcessor(dcontroller);
        Gdx.input.setInputProcessor(multiplexer);

        dialog = new Dialog();
    }

    @Override
    public void loadStage(String s, Body contactBody) {
        DialogNode node1;

        gsm.setLastState(DUNGEON);

        switch (s) {
            case "amuletChest":
                if (!earnedAmulet) {
                    node1 = new DialogNode("Поздравляем! Вы получили Амулет Времени!", 0);
                    dialog.addNode(node1);
                    dcontroller.startDialog(dialog);
                    controller.getInventory().setImgVisibility(2, true);
                    earnedAmulet = true;
                    canDraw = true;
                    progress = true;
                    nextState = -1;
                }
                break;
            case "keyChest":
                if (!earnedKey) {
                    node1 = new DialogNode("Вы получили ключ. Но от чего же он?", 0);
                    controller.getInventory().setAchievementVisibility(3);
                    dialog.addNode(node1);
                    dcontroller.startDialog(dialog);
                    canDraw = true;
                    controller.getInventory().addItem("Ключ");
                    earnedKey = true;
                    nextState = -1;
                }
                break;
            case "door1":
                node1 = new DialogNode("Дверь заперта. Нужно разгадать руны!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = PAINT;
                gsm.setPaintArgs(new ArrayList<>(Arrays.asList(FIGURES_TYPES.SQUARE, FIGURES_TYPES.RHOMBUS, FIGURES_TYPES.STAR)));
                canDraw = true;

                //opening = true;
                doorForOpen = s;
                break;
            case "door2":
                node1 = new DialogNode("Эта тоже... Снова руны!", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = PAINT;
                gsm.setPaintArgs(new ArrayList<>(Arrays.asList(FIGURES_TYPES.CIRCLE, FIGURES_TYPES.TRIANGLE)));
                canDraw = true;

                //opening = true;
                doorForOpen = s;
                break;
            case "door3":
                node1 = new DialogNode("Дверь заперта...", 0);
                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                nextState = PAINT;
                gsm.setPaintArgs(new ArrayList<>(Arrays.asList(FIGURES_TYPES.STAR, FIGURES_TYPES.TRIANGLE, FIGURES_TYPES.CIRCLE, FIGURES_TYPES.SQUARE)));
                canDraw = true;

                //opening = true;
                doorForOpen = s;
                break;
            case "keyDoor":
                if (earnedKey) {
                    opening = false;
                    doorForOpen = s;
                    openDoor(doorForOpen);
                    controller.getInventory().removeItem("Ключ");
                } else {
                    node1 = new DialogNode("Дверь заперта... Возможно, ключ от нее где-то рядом.", 0);
                    dialog.addNode(node1);
                    dcontroller.startDialog(dialog);
                    nextState = -1;
                    canDraw = true;
                }
                break;
            case "ladder":
                if (earnedAmulet) {
                    nextState = PLAY;
                    stop();
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
        soundId = openingDoorSound.play(1.0f);
        openingDoorSound.setVolume(soundId, 0.2f);
        openingDoorSound.setLooping(soundId, false);

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
    public Controller getController() {
        return controller;
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void dispose() {
        player.stopSounds();
        isStopped = true;
        gsm.setLastState(DUNGEON);
        MyGdxGame.getPrefs().putFloat(PREF_X, player.getPosition().x).flush();
        MyGdxGame.getPrefs().putFloat(PREF_Y, player.getPosition().y).flush();
        game.saveProgress();
    }

    @Override
    public JoyStick getJoyStick() {
        return joyStick;
    }
}

