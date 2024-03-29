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

public class MenuBtnBox extends Table {
    private Table uiTable;
    private TextButton.TextButtonStyle style;

    public enum MENU_STATE {
        MENU_TO_PLAY,
        EXIT,
        SAVE,
        SAVE_GAME,
        NON
    }

    private MENU_STATE state;

    public MenuBtnBox(Skin skin) {
        super(skin);
        uiTable = new Table();
        state = MENU_STATE.NON;

        this.add(uiTable).pad(10f);
    }

    public void addBtn(String btnText, final MENU_STATE newState) {
        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.downFontColor = Color.BLACK;

        TextButton btn = new TextButton(btnText, style);
        style.up = getSkin().getDrawable("menuBtn_up");
        style.down = getSkin().getDrawable("menuBtn_down");

        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                state = newState;
                //state = MENU_STATE.NON;
            }
        });

        uiTable.add(btn).expand().align(Align.center).space(8f).padBottom(0f);
        uiTable.row();
    }

    public MENU_STATE getState() {
        return state;
    }
}
