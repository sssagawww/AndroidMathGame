package com.quenta.mobileGame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quenta.mobileGame.UI.Controller;
import com.quenta.mobileGame.UI.Inventory;
import com.quenta.mobileGame.battle.examples.ExampleDatabase;
import com.quenta.mobileGame.battle.steps.StepDatabase;
import com.quenta.mobileGame.db.DbWrapper;
import com.quenta.mobileGame.db.Progress;
import com.quenta.mobileGame.handlers.*;
import com.quenta.mobileGame.multiplayer.MushroomsRequest;
import com.quenta.mobileGame.paint.Figures.FiguresDatabase;
import com.quenta.mobileGame.states.DungeonState;
import com.quenta.mobileGame.states.Forest;
import com.quenta.mobileGame.states.MazeState;

public class MyGdxGame implements ApplicationListener {
    private SpriteBatch sb;
    private AssetManager assetManager;
    private BoundedCamera cam;
    private Controller controller;
    private Stage controllerStage;
    private GameStateManager gsm;
    public static Content res;
    private Skin skin;
    private StepDatabase stepDatabase;
    private ExampleDatabase exampleDatabase;
    private FiguresDatabase figuresDatabase;
    private DbWrapper dbWrapper;
    private MushroomsRequest request;
    public boolean save = false;
    public static float gameTime = 0;
    public static boolean active = true;
    private static Preferences prefs;
    public static final String MUSHROOMS_GAME = "mushroomsMiniGame";
    public static final String PAINT_GAME = "paintMiniGame";
    private static final String PREF_NAME = "position";
    public static final String PREF_ID = "userID";
    public static final String PREF_IP = "serverIP";
    public static final String PREF_USERNAME = "userNAME";
    public static final String PREF_MAZE = "mazeProgress";
    public static final String PREF_MAZE_HOODED = "mazeHoodedRun";
    public static final String PREF_FOREST = "forestProgress";
    public static final String PREF_DUNGEON = "dungeonProgress";
    public static final String PREF_STATE = "lastState";

    public MyGdxGame() {
    }

    public MyGdxGame(DbWrapper dbWrapper) {
        this.dbWrapper = dbWrapper;
    }

    public void create() {
        //Gdx.input.setInputProcessor(new MyInputProcessor());
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            if(dbWrapper.getProgress().size() != 0) gameTime = dbWrapper.getProgress().get(dbWrapper.getProgress().size()-1).getTime();
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        }

        res = new Content();
        res.loadTexture("entitySprites/gnomikStep.png", "gnomik");
        res.loadTexture("entitySprites/gnomik.png", "gnomikFull");
        res.loadTexture("entitySprites/idleGnomik.png", "gnomikrow");
        res.loadTexture("entitySprites/slime.png", "slimeBoss");
        res.loadTexture("allBtn.png", "btn");
        res.loadTexture("entitySprites/enemySprite.png", "enemyBattle");
        res.loadTexture("entitySprites/enemy2.png", "enemy");
        res.loadTexture("entitySprites/enemySlime.png", "enemy2");
        res.loadTexture("entitySprites/bombGuy.png", "npc");
        res.loadTexture("entitySprites/hooded2.png", "hooded");
        res.loadTexture("entitySprites/player.png", "npcForest");
        res.loadTexture("entitySprites/sword3.png", "playerSword");
        res.loadTexture("entitySprites/swordBoss2.png", "playerBossSword");
        res.loadTexture("entitySprites/finalSword.png", "finalSword");
        res.loadTexture("entitySprites/swordAnim.png", "sword");
        res.loadTexture("entitySprites/next.png", "next");
        res.loadTexture("entitySprites/MiniBunny.png", "rabbit");
        res.loadTexture("dungeon/amuletChest.png", "chest");
        res.loadTexture("entitySprites/mushroom.png", "mushroom");

        res.loadTexture("dungeon/amuletChest.png", "amuletChest");
        res.loadTexture("dungeon/door1.png", "door1");
        res.loadTexture("dungeon/door1.png", "door3");
        res.loadTexture("dungeon/keyDoor.png", "keyDoor");
        res.loadTexture("dungeon/keyDoor.png", "door2");
        res.loadTexture("dungeon/amuletChest.png", "keyChest");
        res.loadTexture("dungeon/ladder.png", "ladder");

        res.loadTexture("dungeon/door1_opened.png", "door1_opened");
        res.loadTexture("dungeon/keyDoor_opened.png", "door2_opened");
        res.loadTexture("dungeon/door1_opened.png", "door3_opened");
        res.loadTexture("dungeon/keyDoor_opened.png", "keyDoor_opened");

        assetManager = new AssetManager();
        assetManager.load("UI/testAtlas.atlas", TextureAtlas.class);
        assetManager.load("UI/testAtlas2.atlas", TextureAtlas.class);
        assetManager.load("UI/uipack.atlas", TextureAtlas.class);
        assetManager.load("mcRus.fnt", BitmapFont.class);
        assetManager.finishLoading();
        skin = SkinManager.generateSkin(assetManager);

        controllerStage = new Stage(new ScreenViewport());
        controllerStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        controller = new Controller(skin);
        controller.setVisible(true);

        Table controllerRoot = new Table();
        controllerRoot.setFillParent(true);
        controllerRoot.add(controller).expand().align(Align.bottomLeft);
        controllerStage.addActor(controllerRoot);

        sb = new SpriteBatch();
        cam = new BoundedCamera();
        cam.setToOrtho(false, (float) (Gdx.graphics.getWidth()), (float) (Gdx.graphics.getHeight()));
        stepDatabase = new StepDatabase();
        exampleDatabase = new ExampleDatabase();
        figuresDatabase = new FiguresDatabase(this);
        request = new MushroomsRequest();
        prefs = Gdx.app.getPreferences(PREF_NAME);
        if(prefs.getInteger(PREF_ID)==0){
            prefs.putInteger(PREF_ID, (int) (Math.random() * 10000)).flush();
            prefs.putString(PREF_IP, "000").flush();
            prefs.putString(PREF_USERNAME, "name").flush();
        }
        gsm = new GameStateManager(this);
    }

    @Override
    public void render() {
        //update();
		/*accum += Gdx.graphics.getDeltaTime();
		while(accum >= STEP){
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
			GameKeys.update();
		}

        System.out.println("FPS: " + Gdx.graphics.getFramesPerSecond());
        MathUtils.clamp(Gdx.graphics.getDeltaTime(),-1f,0.0168f);???
        System.out.println(Gdx.graphics.getDeltaTime()-0.01f);*/

        gameTime += Gdx.graphics.getDeltaTime();
        gsm.update(Gdx.graphics.getDeltaTime()); // ???
        gsm.render();
    }

    public void update() {
    }

    @Override
    public void dispose() {
        sb.dispose();
        gsm.dispose();
    }

    @Override
    public void resize(int w, int h) {
        //сюда все resize, которых нигде нет?
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    public void saveProgress() {
        Inventory inventory = controller.getInventory();
        Progress progress = new Progress(inventory.getImgVisibility(0), inventory.getImgVisibility(1), inventory.getImgVisibility(2), inventory.getArtefacts(),
                inventory.getAchievementsVisibility(), inventory.getItems(), gameTime);
        getDbWrapper().saveProgress(progress);

        prefs.putBoolean(PREF_MAZE, MazeState.progress).flush();
        prefs.putBoolean(PREF_MAZE_HOODED, MazeState.hoodedRun).flush();
        prefs.putBoolean(PREF_FOREST, Forest.progress).flush();
        prefs.putBoolean(PREF_DUNGEON, DungeonState.progress).flush();
        prefs.putInteger(PREF_STATE, gsm.getLastState()).flush();
    }

    public Skin getSkin() {
        return skin;
    }

    public SpriteBatch getSb() {
        return sb;
    }

    public BoundedCamera getCam() {
        return cam;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public StepDatabase getStepDatabase() {
        return stepDatabase;
    }

    public ExampleDatabase getExampleDatabase() {
        return exampleDatabase;
    }

    public FiguresDatabase getFiguresDatabase() {
        return figuresDatabase;
    }

    public DbWrapper getDbWrapper() {
        return dbWrapper;
    }

    public Controller getController() {
        return controller;
    }

    public Stage getControllerStage() {
        return controllerStage;
    }

    public MushroomsRequest getRequest() {
        return request;
    }

    public static Preferences getPrefs() {
        return prefs;
    }
}