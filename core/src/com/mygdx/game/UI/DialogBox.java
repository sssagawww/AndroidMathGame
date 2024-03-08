package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

public class DialogBox extends Table {
    private String targetText = "";
    private float animTimer = 0f;
    private float animationTotalTime = 0f;
    private float TIME_PER_CHARACTER = 0.05f;
    private STATE state = STATE.IDLE;
    private Label textLabel;
    private Image nextBtn;
    private boolean isPressed;

    private enum STATE {
        ANIMATING,
        IDLE
    }

    public DialogBox(Skin skin) {
        super(skin);
        this.setBackground("GUI_img");
        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);
        textLabel = new Label("\n", lstyle);

        /*nextBtn = new Image(getSkin().getDrawable("next_btn"));
        nextBtn.setScale(3,3);*/
        textLabel.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isPressed = false;
            }
        });


        //textLabel.setFontScale(0.8f);
        this.add(textLabel).expand().align(Align.left).padTop(20f).padLeft(15f).padRight(15f).padBottom(-10f).row();
        //this.add(nextBtn).expand().align(Align.left).padTop(20f).padLeft(25f).padRight(25f).padBottom(-10f);
    }

    public void animateText(String text) {
        targetText = text;
        animationTotalTime = text.length()*TIME_PER_CHARACTER;
        state = STATE.ANIMATING;
        animTimer = 0f;
    }

    public boolean isFinished() {
        if (state == STATE.IDLE) {
            return true;
        } else {
            return false;
        }
    }

    private void setText(String text) {
        if (!text.contains("\n")) {
            text += "\n";
        }
        this.textLabel.setText(text);
    }

    //анимация вывода текста по буквам
    @Override
    public void act(float delta) {
        if (state == STATE.ANIMATING) {
            animTimer += delta;
            if (animTimer > animationTotalTime) {
                state = STATE.IDLE;
                animTimer = animationTotalTime;
            }
            String actuallyDisplayedText = "";
            int charactersToDisplay = (int)((animTimer/animationTotalTime)*targetText.length());
            for (int i = 0; i < charactersToDisplay; i++) {
                actuallyDisplayedText += targetText.charAt(i);
            }
            if (!actuallyDisplayedText.equals(textLabel.getText().toString())) {
                setText(actuallyDisplayedText);
            }
        }
    }

    @Override
    public float getPrefWidth() {
        return 200f;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean pressed) {
        isPressed = pressed;
    }
}
