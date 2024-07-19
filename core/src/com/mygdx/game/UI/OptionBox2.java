package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class OptionBox2 extends Table {
    private Table uiTable;
    private TextButton.TextButtonStyle style;
    private ArrayList<TextButton> btns = new ArrayList<>();
    private int btnId;
    private boolean clicked;
    private int btnNum = 0;

    public OptionBox2(Skin skin) {
        super(skin);
        uiTable = new Table();

        this.add(uiTable).pad(10f);
    }

    public void addBtn(String btnText) {
        int num = btnNum;
        style = new TextButton.TextButtonStyle();
        style.font = getSkin().getFont("font");
        style.fontColor = Color.BLACK;
        style.downFontColor = Color.BLACK;

        TextButton btn = new TextButton(btnText, style);
        style.up = getSkin().getDrawable("menuBtn_up");
        style.down = getSkin().getDrawable("menuBtn_down");

        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnId = num;
                clicked = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                clicked = false;
            }
        });
        btnNum++;

        btns.add(btn);
        uiTable.add(btn).expand().align(Align.right).space(8f).padBottom(0f);
        uiTable.row();
    }

    public void clearChoices() {
        uiTable.clearChildren();
        btns.clear();
        btnId = 0;
        btnNum = 0;
    }

    public boolean isClicked(){
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public int getBtnId() {
        return btnId;
    }

    public void setBtnId(int btnId) {
        this.btnId = btnId;
    }
}
