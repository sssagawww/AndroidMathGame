package com.quenta.mobileGame.UI;

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
    private final Slider sliderBG;
    private final Slider sliderSoundEff;

    public SoundSettings(Skin skin, Controller controller) {
        super(skin);
        Table uiTable = new Table();
        this.add(uiTable).width(Gdx.graphics.getWidth() / 2.2f).height(Gdx.graphics.getHeight() / 2f);
        this.setBackground("menuBtn_up");

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        font.getData().setScale(1.2f);
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.DARK_GRAY);
        Label.LabelStyle titleStyle = new Label.LabelStyle(font, Color.BLACK);

        Label titleLabel = new Label("Настройки звука", titleStyle);
        Label bgSoundsLbl = new Label("Фоновые звуки", lstyle);
        Label soundEffLbl = new Label("Звуковые эффекты", lstyle);

        Image exitImage = new Image(skin.getDrawable("wrong"));

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.knob = skin.getDrawable("sliderKnob");
        sliderStyle.knob.setMinHeight(Gdx.graphics.getHeight() / 12f);
        sliderStyle.knob.setMinWidth(Gdx.graphics.getHeight() / 16f);
        sliderStyle.background = skin.getDrawable("sliderKnob");

        sliderBG = new Slider(0, 100, 1, false, sliderStyle);
        sliderBG.setVisualPercent(Global.backgroundVolume);
        sliderSoundEff = new Slider(0, 100, 1, false, sliderStyle);
        sliderSoundEff.setVisualPercent(Global.soundEffVolume);

        exitImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Global.soundEffVolume = sliderSoundEff.getPercent();
                Global.backgroundVolume = sliderBG.getPercent();
                Global.bgSounds.forEach(sound -> sound.setVolume(Global.backgroundVolume));
                Global.soundEffs.forEach(sound -> sound.setVolume(Global.soundEffVolume));
                setVisible(false);
                controller.setBtnsVisibility(true);
            }
        });

        uiTable.add(titleLabel).center();
        uiTable.add(exitImage).width(Gdx.graphics.getHeight() / 11.8f).height(Gdx.graphics.getHeight() / 11.8f).right().row();

        Table bottomTable = new Table();
        bottomTable.add(bgSoundsLbl).pad(15f);
        bottomTable.add(sliderBG).width(Gdx.graphics.getWidth() / 6f).align(Align.topLeft).pad(15f).row();
        bottomTable.add(soundEffLbl).pad(15f);
        bottomTable.add(sliderSoundEff).width(Gdx.graphics.getWidth() / 6f).pad(15f);
        uiTable.add(bottomTable).expand().center();
    }

    public Slider getSliderBg() {
        return sliderBG;
    }

    public Slider getSliderSoundEff() {
        return sliderSoundEff;
    }
}
