package com.mygdx.game.UI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class HP_Bar extends Widget {
    private Skin skin;
    private float hpAmount = 1f;
    private Drawable green;
    private Drawable yellow;
    private Drawable red;
    private Drawable background_hpbar;
    private Drawable hp_left;
    private Drawable hp_bar;

    public HP_Bar(Skin skin){
        super();
        this.skin = skin;
        green = skin.getDrawable("green");
        yellow = skin.getDrawable("yellow");
        red = skin.getDrawable("red");
        background_hpbar = skin.getDrawable("background_hpbar");
        hp_left = skin.getDrawable("hpbar_side");
        hp_bar = skin.getDrawable("hpbar_bar");
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);
        int padLeft = 1;
        int padRight = 2;
        int padTop = 2;
        int padBottom = 2;

        float hpWidth = hpAmount * (hp_bar.getMinWidth()-padLeft-padRight);

        //проверяет какой цвет хп рисовать
        Drawable hpColor;
        if(hpAmount <= 0.2){
            hpColor = red;
        } else if (hpAmount <= 0.5) {
            hpColor = yellow;
        } else {
            hpColor = green;
        }

        //супер выравнивание объектов
        hp_left.draw(batch, this.getX(), this.getY(), hp_left.getMinWidth()*4, hp_left.getMinHeight()*4);
        background_hpbar.draw(batch, this.getX()+hp_left.getMinWidth()*4+padLeft, this.getY()+padBottom, hp_bar.getMinWidth()*4-padRight-padLeft*4, hp_bar.getMinHeight()*4-padTop-padBottom*4);
        hpColor.draw(batch, this.getX()+hp_left.getMinWidth()*4+padLeft, this.getY()+padBottom, hpWidth*4, (hp_bar.getMinHeight()-padTop-padBottom)*4);
        hp_bar.draw(batch, this.getX()+hp_left.getMinWidth()*4, this.getY(), hp_bar.getMinWidth()*4, hp_bar.getMinHeight()*4);

        /*hp_left.draw(batch, this.getX(), this.getY(), hp_left.getMinWidth(), hp_left.getMinHeight());

        background_hpbar.draw(batch, this.getX()+hp_left.getMinWidth()+padLeft, this.getY()+padBottom, hp_bar.getMinWidth()-padRight-padLeft, hp_bar.getMinHeight()-padTop-padBottom);

        hpColor.draw(batch, this.getX()+hp_left.getMinWidth()+padLeft, this.getY()+padBottom, hpWidth, (hp_bar.getMinHeight()-padTop-padBottom));

        hp_bar.draw(batch, this.getX()+hp_left.getMinWidth(), this.getY(), hp_bar.getMinWidth(), hp_bar.getMinHeight());*/
    }

    @Override
    public float getMinHeight() {
        return hp_left.getMinHeight()*4;
    }

    @Override
    public float getMinWidth() {
        return hp_left.getMinWidth()*4+hp_bar.getMinWidth()*4;
    }

    public void displayHPLeft(float hp) {
        this.hpAmount = hp;
        hpAmount = MathUtils.clamp(hpAmount, 0f, 1f); //clamp - значение не выйдет за пределы
    }
}
