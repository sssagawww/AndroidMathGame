package com.quenta.mobileGame.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.quenta.mobileGame.UI.DialogBox;
import com.quenta.mobileGame.UI.RhythmMenu;
import com.quenta.mobileGame.handlers.BoundedCamera;
import com.quenta.mobileGame.handlers.GameStateManager;

public class RhythmState extends GameState {
    private final InputMultiplexer multiplexer;
    private Stage uiStage;
    private DialogBox dialogBox;
    private RhythmMenu rhythmMenu;
    private static boolean done;
    private static boolean strength100;
    private static boolean bossFight;
    private final Sound doneSound;
    private Sprite bg;
    private final BoundedCamera rhCam;

    public RhythmState(GameStateManager gsm) {
        super(gsm);
        game = gsm.game();
        multiplexer = new InputMultiplexer();
        doneSound = Gdx.audio.newSound(Gdx.files.internal("music/swordShort.mp3"));

        bg = new Sprite(new Texture("UI/forestBg.png"));
        if (bossFight) {
            bg = new Sprite(new Texture("UI/bossBg.png"));
        }
        initUI();

        rhCam = new BoundedCamera();
        rhCam.setToOrtho(false, (float) (Gdx.graphics.getWidth()), (float) (Gdx.graphics.getHeight()));
        rhCam.setBounds(0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());

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
        rhCam.setPosition(0, 0);
        rhCam.update();
        sb.setProjectionMatrix(rhCam.combined);

        sb.begin();
        sb.draw(bg, Gdx.graphics.getWidth() / 2f - (Gdx.graphics.getWidth() * 1.2f) / 2f, 0, Gdx.graphics.getWidth() * 1.2f, Gdx.graphics.getHeight() * 1.2f);
        sb.end();

        uiStage.draw();
    }

    @Override
    public void dispose() {

    }

    public void initUI() {
        uiStage = new Stage(new ScreenViewport());
        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Table root = new Table();
        root.setFillParent(true);
        uiStage.addActor(root);

        //временная кнопка выхода обратно
        Image menuImg = new Image(new Texture("controller/menuBtn.png"));
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
        root.add(menuImg).align(Align.topLeft).width(Gdx.graphics.getHeight() / 9f).height(Gdx.graphics.getHeight() / 9f).expand();
        root.add(rhythmMenu).align(Align.center).width(Gdx.graphics.getHeight() / 1.5f).height(Gdx.graphics.getHeight() / 1.5f);
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

    public static void setBossFight(boolean bossFight) {
        RhythmState.bossFight = bossFight;
    }
}
