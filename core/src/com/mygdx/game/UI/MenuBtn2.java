package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuBtn2 extends Table{
    private Table uiTable;

    public MenuBtn2(Skin skin) {
        super(skin);
        uiTable = new Table();
        this.add(uiTable).pad(10f);
    }

    public void create() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        TextButton.TextButtonStyle tstyle = new TextButton.TextButtonStyle();
        tstyle.font = font;
        tstyle.up = getSkin().getDrawable("GUI_img");
        tstyle.down = getSkin().getDrawable("optionbox");
        tstyle.checked = getSkin().getDrawable("GUI_2x");
        TextButton button = new TextButton("Button1", tstyle);
        uiTable.add(button);
        uiTable.row();
    }

}
