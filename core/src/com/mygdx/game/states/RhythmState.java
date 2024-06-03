package com.mygdx.game.states;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;
import static com.mygdx.game.handlers.GameStateManager.FOREST;
import static com.mygdx.game.handlers.GameStateManager.PLAY;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Dialog.Dialog;
import com.mygdx.game.Dialog.DialogController;
import com.mygdx.game.Dialog.DialogNode;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.OptionBox;
import com.mygdx.game.UI.PaintMenu;
import com.mygdx.game.UI.RhythmMenu;
import com.mygdx.game.handlers.BoundedCamera;
import com.mygdx.game.handlers.GameStateManager;

import java.util.ArrayList;

public class RhythmState extends GameState {
    private InputMultiplexer multiplexer;
    private Stage uiStage;
    private DialogBox dialogBox;
    private RhythmMenu rhythmMenu;
    private static boolean done;
    private static boolean strength100;
    private static boolean bossFight;
    private Sound doneSound;
    private Sprite bg;
    private BoundedCamera rhCam;

    public RhythmState(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        multiplexer = new InputMultiplexer();
        doneSound = Gdx.audio.newSound(Gdx.files.internal("music/swordShort.mp3"));

        bg = new Sprite(new Texture("UI/forestBg.png"));
        if(bossFight){
            bg = new Sprite(new Texture("UI/bossBg.png"));
        }
        initUI();

       rhCam = new BoundedCamera();
       rhCam.setToOrtho(false, (float) (V_WIDTH), (float) (V_HEIGHT));
        rhCam.setBounds(0, V_WIDTH, 0, V_HEIGHT);

        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        uiStage.act(dt);
        rhythmMenu.update(dt);
        if (rhythmMenu.isPercent100()) {
            rhythmMenu.setPercent100(false);
            dialogBox.animateText("Готово!");
            done = true;
            dialogBox.setVisible(true);
            strength100 = rhythmMenu.isStrength100();
            doneSound.play(0.7f);
        }
        if (dialogBox.isPressed()) {
            gsm.setState(gsm.getLastState());
        }
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 0);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        rhCam.setPosition(0,0);
        rhCam.update();
        sb.setProjectionMatrix(rhCam.combined);

        sb.begin();
        sb.draw(bg, -V_WIDTH/4f, 0);
        sb.end();

        uiStage.draw();
    }

    @Override
    public void dispose() {

    }

    public void initUI() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(V_WIDTH, V_HEIGHT, true);

        Table root = new Table();
        //root.setBackground(game.getSkin().getDrawable("bg"));
        root.setFillParent(true);
        uiStage.addActor(root);

        //временная кнопка выхода обратно
        Image menuImg = new Image(new Texture("controller/menuBtn.png"));
        menuImg.setScale(5, 5);
        menuImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gsm.setState(gsm.getLastState());
            }
        });

        rhythmMenu = new RhythmMenu(game.getSkin(), bossFight);

        Table table = new Table();
        root.add(menuImg).expand().align(Align.topLeft).padTop(65f);
        root.add(rhythmMenu).align(Align.center).width(V_HEIGHT / 1.5f).height(V_HEIGHT / 1.5f);
        root.add(table).expand().align(Align.center);

        Table dialogRoot = new Table();
        dialogRoot.setFillParent(true);
        uiStage.addActor(dialogRoot);

        dialogBox = new DialogBox(game.getSkin());
        dialogBox.setVisible(false);

        dialogRoot.add(dialogBox).expand().align(Align.top).padTop(50f);

        multiplexer.addProcessor(uiStage);
    }

    public static boolean isDone() {
        return done;
    }

    public static void setDone(boolean done) {
        RhythmState.done = done;
    }

    public static boolean isStrength100() {
        return strength100;
    }

    public static boolean isBossFight() {
        return bossFight;
    }

    public static void setBossFight(boolean bossFight) {
        RhythmState.bossFight = bossFight;
    }
}
