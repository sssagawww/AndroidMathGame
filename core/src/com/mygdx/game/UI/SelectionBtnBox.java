package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class SelectionBtnBox extends Table {
    private Table uiTable;
    private TextButton.TextButtonStyle style;
    private TextButton[] buttons;
    private int selectedIndex = 0;
    private boolean isPressed;

    public SelectionBtnBox(Skin skin) {
        super(skin);
        uiTable = new Table();
        this.add(uiTable).pad(10f);

        /*for (int i = 0; i < 4; i++) {
            addBtn("---", i);
        }*/

        buttons = new TextButton[4];
        buttons[0] = addBtn(0);
        buttons[1] = addBtn(1);
        buttons[2] = addBtn(2);
        buttons[3] = addBtn(3);

        uiTable.add(buttons[0]).space(25f).align(Align.left).width(300).height(80);
        uiTable.add(buttons[1]).space(25f).align(Align.left).width(300).height(80).row();
        uiTable.add(buttons[2]).space(25f).align(Align.left).width(300).height(80);
        uiTable.add(buttons[3]).space(25f).align(Align.left).width(300).height(80);
    }

    private TextButton addBtn(final int index) {
        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.downFontColor = Color.BLACK;

        TextButton btn = new TextButton("-----", style);
        style.up = getSkin().getDrawable("menuBtn_up");
        style.down = getSkin().getDrawable("menuBtn_down");

        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                selectedIndex = index;
                isPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isPressed = false;
            }
        });

        return btn;
    }

    public void setLabel(int index, String text) {
        buttons[index].setText(text);
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public boolean isPressed() {
        return isPressed;
    }
}