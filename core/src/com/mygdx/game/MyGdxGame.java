package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.UI.Controller;
import com.mygdx.game.battle.examples.ExampleDatabase;
import com.mygdx.game.battle.steps.StepDatabase;
import com.mygdx.game.db.DbWrapper;
import com.mygdx.game.handlers.*;
import com.mygdx.game.paint.Figures.Figure;
import com.mygdx.game.paint.Figures.FiguresDatabase;
import com.mygdx.game.paint.PixelPoint;

import java.util.ArrayList;
import java.util.Arrays;

public class MyGdxGame implements ApplicationListener {
    private static int width;
    private static int height;
    public static int V_WIDTH = 1216; //1216
    public static int V_HEIGHT = 672; //672
    //public static final int SCALE = 2;
    private SpriteBatch sb;
    private AssetManager assetManager;
    private BoundedCamera cam;
    private Controller controller;
    private Stage controllerStage;
    private GameStateManager gsm;

    //public static final float STEP = 1 / 60f;
    //private float accum;
    public static Content res;
    private Skin skin;
    private StepDatabase stepDatabase;
    private ExampleDatabase exampleDatabase;
    private FiguresDatabase figuresDatabase;
    private DbWrapper dbWrapper;
    public boolean save = false;
    public static float gameTime = 0;

    public MyGdxGame() {
    }

    public MyGdxGame(DbWrapper dbWrapper) {
        this.dbWrapper = dbWrapper;
    }

    public void create() {
        //Gdx.input.setInputProcessor(new MyInputProcessor());
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            V_WIDTH = width; //Gdx.graphics.getWidth();
            V_HEIGHT = height; //Gdx.graphics.getHeight();
            if(dbWrapper.getProgress().size() != 0) gameTime = dbWrapper.getProgress().get(dbWrapper.getProgress().size()-1).getTime();
        }

        //V_WIDTH = (int) ((Gdx.graphics.getWidth()/1216f)*1216);
        //V_HEIGHT = (int) ((Gdx.graphics.getHeight()/672f)*672);

        res = new Content();
        res.loadTexture("entitySprites/gnomikStep.png", "gnomik");
        res.loadTexture("entitySprites/gnomik.png", "gnomikFull");
        res.loadTexture("entitySprites/idleGnomik.png", "gnomikrow");
        res.loadTexture("entitySprites/slime.png", "slimeBoss");
        res.loadTexture("allBtn.png", "btn");
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
        controllerStage.getViewport().update(V_WIDTH, V_HEIGHT, true);
        controller = new Controller(skin);
        controller.setVisible(true);

        Table controllerRoot = new Table();
        controllerRoot.setFillParent(true);
        controllerRoot.add(controller).expand().align(Align.bottomLeft);
        controllerStage.addActor(controllerRoot);

        sb = new SpriteBatch();
        cam = new BoundedCamera();
        cam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT));
        stepDatabase = new StepDatabase();
        exampleDatabase = new ExampleDatabase();
        figuresDatabase = new FiguresDatabase(this);
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

    public static void setWidth(int width) {
        MyGdxGame.width = width;
    }

    public static void setHeight(int height) {
        MyGdxGame.height = height;
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
}