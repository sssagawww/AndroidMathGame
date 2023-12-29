package com.mygdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.game.battle.examples.ExampleDatabase;
import com.mygdx.game.battle.steps.StepDatabase;
import com.mygdx.game.handlers.*;

public class MyGdxGame implements ApplicationListener {
    private static int width;
    private static int height;
    public static int V_WIDTH = 1216;
    public static int V_HEIGHT = 672;
    //public static final int SCALE = 2;
    private SpriteBatch sb;
    private AssetManager assetManager;
    private BoundedCamera cam;
    private GameStateManager gsm;
    //public static final float STEP = 1 / 60f;
    //private float accum;
    public static Content res;
    private Skin skin;
    private StepDatabase stepDatabase;
    private ExampleDatabase exampleDatabase;
    public boolean save = false;

    public void create() {
        //Gdx.input.setInputProcessor(new MyInputProcessor());
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            V_WIDTH = width;
            V_HEIGHT = height;
        }

        res = new Content();
        res.loadTexture("gnomikS.png", "gnomik");
        res.loadTexture("gnomik.png", "gnomikFull");
        res.loadTexture("gnom1rowP1.png", "gnomikrow");
        res.loadTexture("allBtn.png", "btn");
        res.loadTexture("enemySprite2.2.png", "enemy");

        assetManager = new AssetManager();
        assetManager.load("testAtlas.atlas", TextureAtlas.class);
        assetManager.load("testAtlas2.atlas", TextureAtlas.class);
        assetManager.load("uipack.atlas", TextureAtlas.class);
        assetManager.load("mcRus.fnt", BitmapFont.class);
        assetManager.finishLoading();
        skin = SkinManager.generateSkin(assetManager);

        sb = new SpriteBatch();
        cam = new BoundedCamera();
        cam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT));
        gsm = new GameStateManager(this);
        stepDatabase = new StepDatabase();
        exampleDatabase = new ExampleDatabase();
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
}