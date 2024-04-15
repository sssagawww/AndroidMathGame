package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.MyGdxGame;

import java.util.ArrayList;
import java.util.Random;

public class RhythmMenu extends Table {
    private Table strengthTable;
    private Image playerImage;
    private ArrayList<TextureRegionDrawable> drawables;
    private ProgressBar strengthBar;
    private ProgressBar progressBar;
    private Label strengthLabel;
    private Label percentsLabel;
    private Label timeLabel;
    private TextButton.TextButtonStyle style;
    private Sound sound;
    private boolean canProgress = true;
    private boolean sBtnClicked;
    private float time;
    private int randomNum = 0;
    private float curTime = 0;
    private float progress = 0;
    private int strength = 0;
    private boolean progressReverse;
    private float speed = 1;

    public RhythmMenu(Skin skin) {
        super(skin);
        //this.setDebug(true);
        strengthTable = new Table();
        strengthTable.setBackground(skin.getDrawable(("menuBtn_up")));
        this.add(strengthTable);
        drawables = new ArrayList<>();

        sound = Gdx.audio.newSound(Gdx.files.internal("music/sound3.wav"));

        //кнопки
        createBtnStyle();
        TextButton strengthBtn = new TextButton("Выбрать", style);
        strengthBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                sBtnClicked = true;
                strength = checkStrength();
                canProgress = false;
                return true;
            }
        });

        TextButton clickBtn = new TextButton("Тянуть", style);
        clickBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                float value = progressBar.getValue() + strength * 0.2f;
                if (curTime > 1) {
                    canProgress = true;
                    return true;
                }
                if (!sBtnClicked) {
                    return true;
                }
                setPlayerImage(1);
                strengthLabel.setText("Сила: 0");
                if (value >= 100) {
                    percentsLabel.setText("100%");
                    value = 100;
                }
                progressBar.setValue(value);
                percentsLabel.setText(String.format("%.1f", value) + "%");
                sound.setVolume(sound.play(), 0.5f);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setPlayerImage(0);
                timeLabel.setText("0,00");
                canProgress = true;
                sBtnClicked = false;
            }
        });

        createBars();

        //спрайт гг
        Texture tex = MyGdxGame.res.getTexture("playerSword");
        TextureRegion[] sprites = TextureRegion.split(tex, tex.getHeight(), tex.getHeight())[0];
        drawables.add(new TextureRegionDrawable(sprites[0]));
        drawables.add(new TextureRegionDrawable(sprites[1]));
        playerImage = new Image(sprites[0]);

        //стиль для label
        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);
        lstyle.background = getSkin().getDrawable("borders");

        //text label с величиной силы
        strengthLabel = new Label("\n", lstyle);
        strengthLabel.setText("Сила: " + strength);
        strengthLabel.setAlignment(Align.top);

        strengthTable.add(strengthLabel).padTop(25f).row();
        strengthTable.add(strengthBtn).padTop(25f).row();

        //text label с процентами прогресса
        percentsLabel = new Label("\n", lstyle);
        percentsLabel.setText("       0%");
        percentsLabel.setAlignment(Align.top);

        timeLabel = new Label("\n", lstyle);
        timeLabel.setText("0,00");
        timeLabel.setAlignment(Align.top);

        Table playerTable = new Table();
        playerTable.add(playerImage).align(Align.center).width(V_HEIGHT / 1.5f).height(V_HEIGHT / 1.5f).padLeft(146f).expand().row();
        playerTable.add(progressBar).width(500f).align(Align.center).padLeft(146f);
        playerTable.add(percentsLabel).align(Align.left).padLeft(25f);

        Table rightTable = new Table();
        rightTable.add(clickBtn).expand().align(Align.right).row();
        rightTable.add(timeLabel);

        this.add(playerTable).expand();
        this.add(rightTable);
    }

    private void createBtnStyle() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.downFontColor = Color.BLACK;

        style.up = getSkin().getDrawable("menuBtn_up");
        style.down = getSkin().getDrawable("menuBtn_down");
    }

    private void createBars() {
        ProgressBar.ProgressBarStyle strengthBarStyle = new ProgressBar.ProgressBarStyle(getSkin().getDrawable("GUI_img"), getSkin().getDrawable("green"));
        strengthBarStyle.knobBefore = getSkin().getDrawable("green");
        strengthBarStyle.knobBefore.setMinWidth(80f);
        strengthBar = new ProgressBar(0, 100, .1f, true, strengthBarStyle);
        strengthBar.setAnimateDuration(.15f);
        strengthTable.add(strengthBar).width(150f).height(200f).row();

        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle(getSkin().getDrawable("GUI_img"), getSkin().getDrawable("prLine"));
        progressBarStyle.background.setMinHeight(40f);
        progressBarStyle.knobBefore = getSkin().getDrawable("prLine");
        progressBarStyle.knobBefore.setMinHeight(20f);
        progressBar = new ProgressBar(0, 100, .1f, false, progressBarStyle);
        progressBar.setAnimateDuration(.15f);

        strengthBar.setValue(0f);
        progressBar.setValue(0f);
    }

    private void setPlayerImage(int num) {
        playerImage.setDrawable(drawables.get(num));
    }


    public void update(float dt) {
        time += dt;
        if (canProgress) {
            if (time > 1) {
                randomNum = (int) (Math.random() * 5) + 2;
                time = 0;
                speed = 3f - (float) Math.random();
            }
            if (progressReverse) progress -= speed;
            else progress += speed;
            if (progress >= 100) progressReverse = true;
            else if (progress <= 0) progressReverse = false;
        } else {
            curTime = randomNum - time;
            if (curTime <= 0) {
                canProgress = true;
                sBtnClicked = false;
                time = 0;
                curTime = 0;
            }
            timeLabel.setText(String.format("%.2f", curTime) + "");
        }
        strengthBar.setValue(progress);
    }

    private int checkStrength() {
        int i = Math.round(strengthBar.getValue());
        strengthLabel.setText("Сила: " + i);
        return i;
    }
}
