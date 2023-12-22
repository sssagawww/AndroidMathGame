package com.mygdx.game.UI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

public class MenuOptionBox extends Table {
    private int selectedIndex;

    private List<Image> arrows = new ArrayList<Image>();
    private List<Label> options = new ArrayList<Label>();

    private Table uiTable;

    public MenuOptionBox(Skin skin){
        super(skin);
        //this.setBackground("optionbox");
        uiTable = new Table();
        this.add(uiTable).pad(10f);
    }

    public void addOption(String option){
        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);
        Label optionLabel = new Label(option, lstyle);
        lstyle.background = getSkin().getDrawable("optionbox");
        options.add(optionLabel);

        Image arrow = new Image(this.getSkin(), "arrow");
        arrow.setVisible(false);
        arrows.add(arrow);

        uiTable.add(arrow).expand().align(Align.left)/*.padBottom(25f)*/;
        uiTable.add(optionLabel).expand().align(Align.center).space(8f).padBottom(0f); //25f
        uiTable.row();

        arrowsVisibility();
    }

    private void arrowsVisibility() {
        for(int i = 0; i < arrows.size(); i++){
            if(i == selectedIndex){
                arrows.get(i).setVisible(true);
            } else {
                arrows.get(i).setVisible(false);
            }
        }
    }

    public void moveDown(){
        System.out.println(selectedIndex);
        selectedIndex++;
        if (selectedIndex >= options.size()){
            selectedIndex = options.size()-1;
        }
        arrowsVisibility();
    }

    public void moveUp(){
        System.out.println(selectedIndex);
        selectedIndex--;
        if (selectedIndex < 0){
            selectedIndex = 0;
        }
        arrowsVisibility();
    }

    public int getID(){
        return selectedIndex;
    }

    public void clearChoices(){
        uiTable.clearChildren();
        arrows.clear();
        options.clear();
        selectedIndex = 0;
    }

    public int getIndex() {
        return selectedIndex;
    }
}
