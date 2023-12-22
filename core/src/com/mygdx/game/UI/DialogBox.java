package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class DialogBox extends Table {
    private String targetText = "";
    private float animTimer = 0f;
    private float animationTotalTime = 0f;
    private float TIME_PER_CHARACTER = 0.05f;
    private STATE state = STATE.IDLE;
    private Label textLabel;
    private Skin skinDialog;

    private enum STATE {
        ANIMATING,
        IDLE,
        ;
    }

    public DialogBox(Skin skin) {
        super(skin);
        this.setBackground("GUI_img");
        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);
        textLabel = new Label("\n", lstyle);
        //textLabel.setFontScale(0.8f);
        this.add(textLabel).expand().align(Align.left).padTop(20f).padLeft(15f).padRight(15f).padBottom(-10f);
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
}
