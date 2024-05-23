package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

public class BtnBox extends Table {
    private Table uiTable;
    private TextButton.TextButtonStyle style;

    //стейты кнопок для всех игровых стейтов (PaintState, Menu, ...), переделать?
    public enum STATES {
        MENU_TO_PLAY,
        EXIT,
        SAVE,
        ONLINE,
        STATISTICS,
        SAVE_GAME,
        NON,
        CLEAR,
        OK,
        WRONG,
        CHECK,
        DONE
    }

    private STATES state;
    private boolean clicked;

    public BtnBox(Skin skin) {
        super(skin);
        uiTable = new Table();
        state = STATES.NON;

        this.add(uiTable).pad(10f);
    }

    public void addBtn(String btnText, final STATES newState) {
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
                clicked = true;
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

    public boolean isClicked(){
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public STATES getState() {
        return state;
    }

    public void setState(STATES state) {
        this.state = state;
    }
}
