package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class SelectionBox extends Table {
    private int selectedIndex = 0;

    private Label[] labels = new Label[4];
    private Image[] arrows = new Image[4];

    private Table uiTable;

    public SelectionBox(Skin skin){
        super(skin);
        this.setBackground("optionbox");
        this.uiTable = new Table();
        this.add(uiTable).pad(5f);

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);

        labels[0] = new Label("-", lstyle);
        labels[1] = new Label("-", lstyle);
        labels[2] = new Label("-", lstyle);
        labels[3] = new Label("-", lstyle);

        arrows[0] = new Image(skin, "arrow");
        arrows[0].setScaling(Scaling.none);
        arrows[1] = new Image(skin, "arrow");
        arrows[1].setScaling(Scaling.none);
        arrows[2] = new Image(skin, "arrow");
        arrows[2].setScaling(Scaling.none);
        arrows[3] = new Image(skin, "arrow");
        arrows[3].setScaling(Scaling.none);

        uiTable.add(arrows[0]).space(5f);
        uiTable.add(labels[0]).space(5f).align(Align.left);
        uiTable.add(arrows[1]).space(5f);
        uiTable.add(labels[1]).space(5f).align(Align.left).row();
        uiTable.add(arrows[2]).space(5f);
        uiTable.add(labels[2]).space(5f).align(Align.left);
        uiTable.add(arrows[3]).space(5f);
        uiTable.add(labels[3]).space(5f).align(Align.left);

        setSelection(0);
    }

    public void moveUp() {
        if (selectedIndex == 0) {
            return;
        }
        if (selectedIndex == 1) {
            return;
        }
        if (selectedIndex == 2) {
            setSelection(0);
            return;
        }
        if (selectedIndex == 3) {
            setSelection(1);
            return;
        }
    }

    public void moveDown() {
        if (selectedIndex == 0) {
            setSelection(2);
            return;
        }
        if (selectedIndex == 1) {
            setSelection(3);
            return;
        }
        if (selectedIndex == 2) {
            return;
        }
        if (selectedIndex == 3) {
            return;
        }
    }

    public void moveLeft() {
        if (selectedIndex == 0) {
            return;
        }
        if (selectedIndex == 1) {
            setSelection(0);
            return;
        }
        if (selectedIndex == 2) {
            return;
        }
        if (selectedIndex == 3) {
            setSelection(2);
            return;
        }
    }

    public void moveRight() {
        if (selectedIndex == 0) {
            setSelection(1);
            return;
        }
        if (selectedIndex == 1) {
            return;
        }
        if (selectedIndex == 2) {
            setSelection(3);
            return;
        }
        if (selectedIndex == 3) {
            return;
        }
    }

    private void setSelection(int index) {
        selectedIndex = index;
        for (int i = 0; i < labels.length; i++) {
            if (i == index) {
                arrows[i].setVisible(true);
            } else {
                arrows[i].setVisible(false);
            }
        }
    }

    public void setLabel(int index, String text) {
        labels[index].setText(text);
    }

    public int getSelection() {
        return selectedIndex;
    }
}
