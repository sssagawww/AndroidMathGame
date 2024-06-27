package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.B2DVars.BIT_PLAYER;
import static com.mygdx.game.handlers.B2DVars.BIT_TROPA;
import static com.mygdx.game.handlers.B2DVars.PPM;
import static com.mygdx.game.handlers.GameStateManager.BATTLE;
import static com.mygdx.game.handlers.GameStateManager.BLACK_SCREEN;
import static com.mygdx.game.handlers.GameStateManager.BOSSFIGHT;
import static com.mygdx.game.handlers.GameStateManager.MENU;
import static com.mygdx.game.handlers.GameStateManager.PAINT;
import static com.mygdx.game.handlers.GameStateManager.RHYTHM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.github.tommyettinger.textra.Font;
import com.github.tommyettinger.textra.TypingLabel;
import com.mygdx.game.Dialog.Dialog;
import com.mygdx.game.Dialog.DialogController;
import com.mygdx.game.Dialog.DialogNode;
import com.mygdx.game.UI.BossLabel;
import com.mygdx.game.UI.Controller;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.Global;
import com.mygdx.game.UI.JoyStick;
import com.mygdx.game.UI.OptionBox2;
import com.mygdx.game.entities.PlayEntities;
import com.mygdx.game.entities.Player2;
import com.mygdx.game.entities.SlimeBoss;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.Controllable;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.handlers.MyContactListener;

public class BossFightState extends GameState implements Controllable {

    private BoundedCamera b2dCam;
    private float tileMapHeight;
    private float tileSize;
    private float tileMapWidth;
    private BoundedCamera bossCam;
    private Skin skin_this;
    private MyContactListener cl;
    private InputMultiplexer multiplexer;
    private Box2DDebugRenderer b2dr;
    private World world;
    private BoundedCamera joyCam;
    private JoyStick joyStick;
    private ShapeRenderer shapeRenderer;
    private Vector3 mouse;
    private Player2 player;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tmr;
    private PlayEntities entities;
    private SlimeBoss slimeBoss;
    private Stage uiStage;
    private Stage bossUiStage;
    private Table dialogRoot;
    private DialogBox dialogueBox;
    private OptionBox2 optionBox;
    private DialogController dcontroller;
    private Dialog dialog;
    private BossLabel bossLabel;
    private boolean debug = false;
    private boolean canDraw = false;
    private boolean fight = false;
    private boolean isStopped = false;
    private float time = 0;
    private boolean done;
    private boolean end;
    private Music music;
    private float bossPositionY;

    public BossFightState(GameStateManager gsm) {
        super(gsm);
        music = Gdx.audio.newMusic(Gdx.files.internal("music/dubstep_bossfight_bg.mp3"));
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        game = gsm.game();
        multiplexer = new InputMultiplexer();
        cl = new MyContactListener(this);
        world.setContactListener(cl);
        skin_this = game.getSkin();

        initJoyStick();
        initController();
        createTiles();
        createPlayer();
        createSlime();
        //createNPC();

        initFight();

        bossCam = new BoundedCamera();
        bossCam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT));
        bossCam.setBounds(0, tileMapWidth * tileSize * 4, 0, tileMapHeight * tileSize * 4);
        b2dCam = new BoundedCamera();
        b2dCam.setToOrtho(false, V_WIDTH / PPM, V_HEIGHT / PPM);
        b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0, (tileMapHeight * tileSize) / PPM);
    }

    @Override
    public void update(float dt) {
        handleInput();
        world.step(dt, 6, 2);
        player.update(dt);
//        entities.update(dt);
        player.updatePL();
        slimeBoss.update(dt);

        if (isStopped) {
            if (PaintState.isDone()) {
                PaintState.setDone(false);
                slimeBoss.setNewAnimation(3, 32, 32);
                bossLabel.getHpBar().setValue(bossLabel.getHpBar().getValue() - 33f);
                bossLabel.getPaintBtn().setVisible(false);
            } else if (RhythmState.isDone()) {
                RhythmState.setDone(false);
                slimeBoss.setNewAnimation(3, 32, 32);
                bossLabel.getHpBar().setValue(bossLabel.getHpBar().getValue() - 33f);
                bossLabel.getRhythmBtn().setVisible(false);
            } else if (BattleState2.isDone()) {
                BattleState2.setDone(false);
                slimeBoss.setNewAnimation(3, 32, 32);
                bossLabel.getHpBar().setValue(bossLabel.getHpBar().getValue() - 33f);
                bossLabel.getMathBtn().setVisible(false);
            }

            if (bossLabel.getHpBar().getValue() <= 1) {
                slimeBoss.setNewAnimation(0, 32, 32);
                storyNext();
            }

            isStopped = false;
            multiplexer.addProcessor(uiStage);
            multiplexer.addProcessor(bossUiStage);
            multiplexer.addProcessor(controllerStage);
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (controller.isMenuPressed()) {
            gsm.setState(MENU);
            controller.setMenuPressed(false);
        }

        if (done && slimeBoss.getPosition().y < bossPositionY) {
            done = false;
            slimeBoss.setDirection(0, 0, 30, 32, 32);
            finalDialog();
        }

        if (canDraw) {
            uiStage.act(dt);
            dcontroller.update(dt);
            time += dt;
            if (dialogueBox.isFinished() && time > 2f && dcontroller.isFinished()) {
                time = 0;
                if (done) {
                    slimeBoss.setDirection(1, 0.8f, 30, 32, 32);
                    bossLabel.changeCell();
                    bossLabel.getTimeTable().setVisible(true);
                } else if (!fight) {
                    music.setLooping(true);
                    music.play();
                    fight = true;
                } else {
                    slimeBoss.setNewAnimation(4, 32, 32);
                    end = true;
                }
                stop();
            }
        }

        if (end) {
            time += dt;
            if (time >= 1) {
                BlackScreen.setFinalTitles(true);
                gsm.setState(BLACK_SCREEN);
            }
        }

        if (Gdx.input.isTouched() && !controller.isInventoryVisible() && !dialogueBox.isVisible() && !fight) {
            mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            joyCam.unproject(mouse);
            joyStick.update(mouse.x, mouse.y);
        } else {
            joyStick.setDefaultPos();
        }

        if (fight) {
            if (controller.getSoundSettings().getSliderBg().isDragging())
                music.setVolume(controller.getSoundSettings().getSliderBg().getPercent());
            if (!done) slimeBoss.randomAnimation(dt);
            bossUiStage.act(dt);
        }
        controllerStage.act(dt);
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        bossCam.setPosition(player.getPosition().x * PPM + V_WIDTH / 35, player.getPosition().y * PPM + V_HEIGHT / 35);
        bossCam.update();

        tmr.setView(bossCam);
        tmr.render();

        sb.setProjectionMatrix(bossCam.combined);
        player.render(sb, 80f, 86.6f);
        slimeBoss.render(sb, 260f, 260f);
//        entities.render(sb, 1.5f, 1.5f);

        if (debug) {
            b2dCam.position.set(player.getPosition().x, player.getPosition().y, 0);
            b2dCam.update();
            b2dr.render(world, b2dCam.combined);
        }

        if (!fight) joyStick.render(shapeRenderer);

        if (canDraw) {
            uiStage.draw();
        }

        if (fight) {
            bossUiStage.draw();
            checkBtns();
        }
        controllerStage.draw();
    }

    private void createTiles() {
        tiledMap = new TmxMapLoader().load("sprites/mystic_woods_free_2.1/bosslocation2.tmx");
        tmr = new OrthogonalTiledMapRenderer(tiledMap, 4); // !!! размер карты
        tileSize = (int) tiledMap.getProperties().get("tilewidth");

        tileMapWidth = (int) tiledMap.getProperties().get("width");
        tileMapHeight = (int) tiledMap.getProperties().get("height");

        TiledMapTileLayer walls = (TiledMapTileLayer) tiledMap.getLayers().get("walls");
        createLayer(walls, BIT_TROPA, BIT_PLAYER, false);
        TiledMapTileLayer collision = (TiledMapTileLayer) tiledMap.getLayers().get("collision");
        createLayer(collision, BIT_TROPA, BIT_PLAYER, true);
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
                    fdef.isSensor = true;
                    world.createBody(bdef).createFixture(fdef).setUserData(layer.getName());
                } else {
                    world.createBody(bdef).createFixture(fdef);
                }
                world.createBody(bdef).createFixture(fdef).setUserData(layer.getName());
                cs.dispose();
            }
        }
    }

    private void createPlayer() {
        BodyDef bdef = new BodyDef();
        PolygonShape ps = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(1000f / PPM, 150f / PPM);

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
        MapLayer mlayer = tiledMap.getLayers().get("exit");
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

    private void createSlime() {
        BodyDef bdef = new BodyDef();

        bdef.position.set(tileMapWidth * tileSize * 3.5f / 2f / PPM, tileMapHeight * tileSize * 3.5f / 2f / PPM);

        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);

        FixtureDef cdef = new FixtureDef();
        CircleShape cshape = new CircleShape();
        cshape.setRadius(50f / PPM);
        cdef.shape = cshape;
        cdef.isSensor = true;
        cdef.filter.categoryBits = BIT_TROPA;
        cdef.filter.maskBits = BIT_PLAYER;
        cshape.setPosition(new Vector2(7.5f, 7.5f));
        cshape.dispose();

        body.createFixture(cdef).setUserData("boss");

        slimeBoss = new SlimeBoss(body);
        body.setUserData(slimeBoss);

        bossPositionY = slimeBoss.getPosition().y;
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

        bossUiStage = new Stage(new ScreenViewport());
        bossUiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        Table root = new Table();
        root.setFillParent(true);
        bossLabel = new BossLabel(skin_this);
        bossLabel.getTimeTable().addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                slimeBoss.setDirection(-1, -0.8f, 45, 32, 32);
                bossLabel.getTimeTable().setVisible(false);
                DialogNode node1 = new DialogNode("Время изменило свой ход...", 0);

                dialog.addNode(node1);
                dcontroller.startDialog(dialog);
                canDraw = true;
                return true;
            }
        });

        root.add(bossLabel);
        bossUiStage.addActor(root);

        dcontroller = new DialogController(dialogueBox, optionBox);
        multiplexer.addProcessor(bossUiStage);
        multiplexer.addProcessor(uiStage);
        //multiplexer.addProcessor(dcontroller);
        Gdx.input.setInputProcessor(multiplexer);

        dialog = new Dialog();
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

    private void checkBtns() {
        switch (bossLabel.getState()) {
            case PAINT_ATTACK:
                gsm.setLastState(BOSSFIGHT);
                gsm.setPaintArgs(null);
                gsm.setState(PAINT);
                break;
            case RHYTHM_ATTACK:
                gsm.setLastState(BOSSFIGHT);
                RhythmState.setBossFight(true);
                gsm.setState(RHYTHM);
                break;
            case MATH_ATTACK:
                gsm.setLastState(BOSSFIGHT);
                BattleState2.setBossFight(true);
                gsm.setState(BATTLE);
                break;
        }
        bossLabel.setState(BossLabel.ATTACK_STATES.NON_ATTACK);
    }

    @Override
    public JoyStick getJoyStick() {
        return joyStick;
    }

    @Override
    public void loadStage(String s, Body contactBody) {
        DialogNode node1;
        gsm.setLastState(BOSSFIGHT);
        switch (s) {
            case "collision":
                if (fight) break;
                contactBody.getFixtureList().get(0).setUserData("collided");
                node1 = new DialogNode("Наконец-то ты добрался сюда.", 0);
                DialogNode node2 = new DialogNode("Ты прошел через многие испытания на пути...", 1);
                DialogNode node3 = new DialogNode("Но поглощение этого мира неизбежно.", 2);
                DialogNode node4 = new DialogNode("Теперь же ты тоже станешь частью моей тьмы!", 3);

                node1.makeLinear(node2.getId());
                node2.makeLinear(node3.getId());
                node3.makeLinear(node4.getId());

                dialog.addNode(node1);
                dialog.addNode(node2);
                dialog.addNode(node3);
                dialog.addNode(node4);
                dcontroller.startDialog(dialog);
                canDraw = true;
                break;
        }
    }

    private void stop() {
        canDraw = false;
    }

    private void storyNext() {
        music.stop();
        DialogNode node1 = new DialogNode("Ты... сильнее, чем я думал...", 0);
        DialogNode node2 = new DialogNode("Но я добьюсь своей цели.", 1);

        node1.makeLinear(node2.getId());

        dialog.addNode(node1);
        dialog.addNode(node2);
        dcontroller.startDialog(dialog);
        canDraw = true;
        done = true;
    }

    private void finalDialog() {
        DialogNode node1 = new DialogNode("Нет...", 0);
        DialogNode node2 = new DialogNode("Я... ещe вернусь...", 1);

        node1.makeLinear(node2.getId());

        dialog.addNode(node1);
        dialog.addNode(node2);
        dcontroller.startDialog(dialog);
        canDraw = true;

        bossLabel.setVisible(false);
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
    }
}
