package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class SoundSettings extends Table {
    private Image exitImage;
    private Table uiTable;
    private Skin skin;
    private Label.LabelStyle lstyle;
    private Label.LabelStyle titleStyle;
    private Label titleLable, bgSoundsLbl, soundEffLbl;
    private Slider.SliderStyle sliderStyle;
    private Slider sliderBG, sliderSoundEff;

    public SoundSettings(Skin skin, Controller controller) {
        super(skin);
        this.skin = skin;
        uiTable = new Table();
        this.add(uiTable).width(V_WIDTH / 1.2f).height(V_HEIGHT / 1.2f);
        this.setBackground("menuBtn_up");

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        font.getData().setScale(0.7f);
        lstyle = new Label.LabelStyle(font, Color.DARK_GRAY);
        titleStyle = new Label.LabelStyle(font, Color.BLACK);

        titleLable = new Label("Настройки звука", titleStyle);
        bgSoundsLbl = new Label("Фоновые звуки", lstyle);
        soundEffLbl = new Label("Звуковые эффекты", lstyle);

        exitImage = new Image(skin.getDrawable("wrong"));
        exitImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setVisible(false);
                controller.setBtnsVisibility(true);
            }
        });

        sliderStyle = new Slider.SliderStyle();
        sliderStyle.knob = skin.getDrawable("menuBtn_up");
        sliderStyle.background = skin.getDrawable("menuBtn_up");

        sliderBG = new Slider(0, 100, 1, false, sliderStyle);
        sliderSoundEff = new Slider(0, 100, 1, false, sliderStyle);



        uiTable.add(titleLable).left().top();
        uiTable.add(bgSoundsLbl);
        uiTable.add(soundEffLbl);
        uiTable.add(sliderBG).align(Align.topLeft);
        uiTable.add(sliderSoundEff);
        uiTable.add(exitImage).setActorBounds(0, 0, exitImage.getWidth() * 5, exitImage.getHeight() * 5);

    }

    public float getSliderBgPercent() {
        return sliderBG.getPercent();
    }

    public float getSliderSoundEffPercent() {
        return sliderSoundEff.getPercent();
    }


}
