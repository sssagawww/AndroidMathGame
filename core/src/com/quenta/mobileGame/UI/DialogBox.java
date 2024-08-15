package com.quenta.mobileGame.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
    private final Label textLabel;
    private final Image skipBtn;
    private final Image labelBtn;
    private boolean isPressed;
    private boolean isSkipped;
    private final Table textTable;

    private enum STATE {
        ANIMATING,
        IDLE
    }

    public DialogBox(Skin skin) {
        super(skin);
        Table uiTable = new Table(skin);
        textTable = new Table(skin);
        textTable.setBackground("GUI_img");

        //основной текст
        Label.LabelStyle lstyle = new Label.LabelStyle(getSkin().getFont("font"), Color.BLACK);
        textLabel = new Label("\n", lstyle);
        textLabel.addListener(new InputListener() {
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

        //значок, что нужно нажимать на диалог для продолжения
        labelBtn = new Image(new Texture("controller/labelBtn.png"));
        labelBtn.addListener(new InputListener() {
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

        //значок пропуска
        skipBtn = new Image(new Texture("controller/skipBtn.png"));
        skipBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isSkipped = true;
                isPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isSkipped = false;
                isPressed = false;
            }
        });

        textTable.add(textLabel).expand().align(Align.left).padTop(20f).padLeft(15f).padRight(15f).padBottom(-20f);
        uiTable.add(textTable).center();
        this.add(uiTable).width(Gdx.graphics.getWidth() / 1.05f);
    }

    public void animateText(String text) {
        targetText = text;
        animationTotalTime = text.length() * TIME_PER_CHARACTER;
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
        //if (!text.contains("\n")) {
        text += "\n";
        // }
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
            int charactersToDisplay = (int) ((animTimer / animationTotalTime) * targetText.length());
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

    public boolean isSkipped() {
        return isSkipped;
    }

    public void addBtn() {
        textTable.add(labelBtn).width(Gdx.graphics.getHeight() / 25f).height(Gdx.graphics.getHeight() / 25f);
        this.add(skipBtn).align(Align.center).width(Gdx.graphics.getHeight() / 12.8f).height(Gdx.graphics.getHeight() / 12.8f).padRight(5f).right().expand();
    }
}
