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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.MyGdxGame;

import java.util.ArrayList;

public class RhythmMenu extends Table {
    private Table strengthTable;
    private Image playerImage;
    private Drawable finalSword;
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
    private boolean percent100;
    private boolean strength100;
    private float speed = 1;
    private float size = V_HEIGHT / 1.5f * 1.34f;
    private float playerImageX;
    private float playerImageSize;
    private boolean bossFight;

    public RhythmMenu(Skin skin, boolean bossFight) {
        super(skin);
        this.bossFight = bossFight;
        strengthTable = new Table();
        strengthTable.setBackground(skin.getDrawable(("menuBtn_up")));
        drawables = new ArrayList<>();

        sound = Gdx.audio.newSound(Gdx.files.internal("music/sound3.wav"));
        if(bossFight){
            sound = Gdx.audio.newSound(Gdx.files.internal("music/bonk.mp3"));
        }

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
        if(bossFight) clickBtn = new TextButton("Ударить", style);
        clickBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setPlayerImage(1);
                float value = progressBar.getValue() + strength * 0.2f;
                if (curTime > 1) {
                    canProgress = true;
                    return true;
                }
                if (!sBtnClicked) {
                    return true;
                }
                strengthLabel.setText("Сила: 0");
                if (value >= 100) {
                    playerImage.setDrawable(finalSword);
                    percent100 = true;
                    percentsLabel.setText("100%");
                    value = 100;
                }
                progressBar.setValue(value);
                percentsLabel.setText(String.format("%.1f", value) + "%");
                sound.setVolume(sound.play(), 1f);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (progressBar.getValue() < 100) {
                    setPlayerImage(0);
                }
                timeLabel.setText("0,00");
                canProgress = true;
                sBtnClicked = false;
            }
        });

        //двигающаяся шкала и прогресс мини-игры
        createBars();

        Texture finalTex = MyGdxGame.res.getTexture("finalSword");
        TextureRegion finalSprite = new TextureRegion(finalTex, finalTex.getHeight(), finalTex.getHeight());
        finalSword = new TextureRegionDrawable(finalSprite);

        //спрайт гг
        Texture tex = MyGdxGame.res.getTexture("playerSword");
        if(bossFight){
            tex = MyGdxGame.res.getTexture("playerBossSword");
        }
        TextureRegion[] sprites = TextureRegion.split(tex, tex.getHeight(), tex.getHeight())[0];
        drawables.add(new TextureRegionDrawable(sprites[0]));
        drawables.add(new TextureRegionDrawable(sprites[1]));
        playerImage = new Image(sprites[0]);

        playerImageSize = size / 1.65f;

        //стиль для label
        BitmapFont font = getSkin().getFont("font");
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

        Label.LabelStyle tStyle = new Label.LabelStyle(font, Color.BLACK);
        tStyle.background = getSkin().getDrawable("menuBtn_down");

        Label trainingLabel = new Label("Выбери вовремя большую силу, а потом нажми\nна кнопку \"Тянуть\", когда на счетчике будет 0-1 секунд.", tStyle);
        trainingLabel.setFontScale(0.9f);
        trainingLabel.setAlignment(Align.center);

        Table playerTable = new Table();
        Table bottomTable = new Table(skin);
        bottomTable.setBackground("menuBtn_down");
        bottomTable.add(progressBar).width(500f).align(Align.center);
        bottomTable.add(percentsLabel).align(Align.left).padLeft(25f);

        playerTable.add(playerImage).align(Align.center).width(V_HEIGHT / 1.5f).height(V_HEIGHT / 1.5f).expand().row();
        playerTable.add(bottomTable).bottom().row();
        playerTable.add(trainingLabel).height(playerTable.getPrefHeight()/8f).padTop(20f);

        Table rightTable = new Table(skin);
        rightTable.setBackground("menuBtn_down");
        rightTable.add(clickBtn).expand().align(Align.center).row();
        rightTable.add(timeLabel);

        this.add(strengthTable);
        this.add(playerTable).expand();
        this.add(rightTable);
    }

    private void createBtnStyle() {
        BitmapFont font = getSkin().getFont("font");
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
        if (progressBar.getValue() >= 100 && size >= playerImageSize) {
            size -= 1;
            playerImage.setSize(size, size);
            playerImageX += 0.003f;
            playerImage.setPosition(playerImageX + playerImage.getX(), playerImage.getY());
        }
        time += dt;
        if (canProgress) {
            if (time > 1) {
                randomNum = (int) (Math.random() * 3) + 2;
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
        if (i >= 100) {
            strength100 = true;
        }
        return i;
    }

    public boolean isPercent100() {
        return percent100;
    }

    public void setPercent100(boolean percent100) {
        this.percent100 = percent100;
    }

    public boolean isStrength100() {
        return strength100;
    }
}
