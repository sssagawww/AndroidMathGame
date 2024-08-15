package com.quenta.mobileGame.UI;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class StatusBox extends Table {
    private final Label text;
    private final HP_Bar hpbar;
    protected Table uiContainer;

    public StatusBox(Skin skin) {
        super(skin);
        this.setBackground("status");
        uiContainer = new Table();
        this.add(uiContainer).grow();

        text = new Label("no entity", skin, "lstyle");
        uiContainer.add(text).align(Align.left).pad(10).row();

        hpbar = new HP_Bar(skin);
        uiContainer.add(hpbar).padBottom(10f).expand();
    }

    public void setText(String newText) {
        text.setText(newText);
    }

    public HP_Bar getHPBar() {
        return hpbar;
    }
}
