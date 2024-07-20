package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class SoundSettings extends Table {
    private Image exitImage;
    private Table uiTable;
    private Skin skin;
    private Label.LabelStyle lstyle;
    private Label.LabelStyle titleStyle;
    private Label titleLabel, bgSoundsLbl, soundEffLbl;
    private Slider.SliderStyle sliderStyle;
    private Slider sliderBG, sliderSoundEff;

    public SoundSettings(Skin skin, Controller controller) {
        super(skin);
        this.skin = skin;
        uiTable = new Table();
        this.add(uiTable).width(V_WIDTH / 2.2f).height(V_HEIGHT / 2f);
        this.setBackground("menuBtn_up");

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        font.getData().setScale(0.9f);
        lstyle = new Label.LabelStyle(font, Color.DARK_GRAY);
        titleStyle = new Label.LabelStyle(font, Color.BLACK);

        titleLabel = new Label("Настройки звука", titleStyle);
        bgSoundsLbl = new Label("Фоновые звуки", lstyle);
        soundEffLbl = new Label("Звуковые эффекты", lstyle);

        exitImage = new Image(skin.getDrawable("wrong"));

        sliderStyle = new Slider.SliderStyle();
        sliderStyle.knob = skin.getDrawable("sliderKnob");
        sliderStyle.knob.setMinHeight(V_HEIGHT/12f);
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
                Global.bgSounds.forEach(sound -> {
                    sound.setVolume(Global.backgroundVolume);
                });
                Global.soundEffs.forEach(sound -> {
                    sound.setVolume(Global.soundEffVolume);
                });
                setVisible(false);
                controller.setBtnsVisibility(true);
            }
        });

        uiTable.add(titleLabel).center();
        uiTable.add(exitImage).width(exitImage.getWidth() * 4).height(exitImage.getHeight() * 4).right().row();

        Table bottomTable = new Table();
        bottomTable.add(bgSoundsLbl).pad(15f);
        bottomTable.add(sliderBG).width(V_WIDTH/6f).align(Align.topLeft).pad(15f).row();
        bottomTable.add(soundEffLbl).pad(15f);
        bottomTable.add(sliderSoundEff).width(V_WIDTH/6f).pad(15f);
        uiTable.add(bottomTable).expand().center();
    }

    public Slider getSliderBg() {
        return sliderBG;
    }

    public Slider getSliderSoundEff() {
        return sliderSoundEff;
    }


}
