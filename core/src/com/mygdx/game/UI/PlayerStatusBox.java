package com.mygdx.game.UI;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PlayerStatusBox extends StatusBox{
    private Label hpText;

    public PlayerStatusBox(Skin skin) {
        super(skin);
        /*hpText = new Label("10", skin, "lstyle");
        uiContainer.row();
        uiContainer.add(hpText).expand().right();*/
    }
    /*public void setHPText(int hpLeft, int hpTotal) {
        hpText.setText(hpLeft+"/"+hpTotal);
    }*/
}
