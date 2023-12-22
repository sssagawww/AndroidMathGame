package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class StatusBox extends Table {
    private Label text;
    private HP_Bar hpbar;
    protected Table uiContainer;

    public StatusBox(Skin skin) {
        super(skin);
        this.setBackground("status");
        uiContainer = new Table();
        this.add(uiContainer).grow().pad(0f);

        text = new Label("no entity", skin, "lstyle");
        uiContainer.add(text).align(Align.left).padTop(5f).padLeft(5f).row();

        hpbar = new HP_Bar(skin);
        uiContainer.add(hpbar).spaceTop(0f).expand();
    }

    public void setText(String newText) {
        text.setText(newText);
    }

    public HP_Bar getHPBar() {
        return hpbar;
    }
}
