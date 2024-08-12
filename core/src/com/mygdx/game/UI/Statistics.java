package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.gameTime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Arrays;

public class Statistics extends Table {
    private Table uiTable;
    private Skin skin;
    private Label.LabelStyle lstyle;
    private Image exitImage;

    public Statistics(Skin skin) {
        super(skin);
        this.skin = skin;
        uiTable = new Table();
        this.add(uiTable).width(Gdx.graphics.getWidth() / 2f).height(Gdx.graphics.getHeight() / 1.2f);
        this.setBackground("menuBtn_up");

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        font.getData().setScale(0.7f);
        lstyle = new Label.LabelStyle(font, Color.BLACK);

        Table topTable = new Table();

        Label title = new Label("\n", lstyle);
        title.setText("Статистика");
        topTable.add(title).align(Align.center).expand();

        exitImage = new Image(skin.getDrawable("wrong"));
        exitImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setVisible(false);
            }
        });
        topTable.add(exitImage).align(Align.left).width(exitImage.getWidth() * 3).height(exitImage.getHeight() * 3);
        uiTable.add(topTable).align(Align.top).width(Gdx.graphics.getWidth() / 2f).height(exitImage.getHeight() * 3).row();

        String time = timeToString(gameTime);
        ArrayList<String> titles = new ArrayList<>(Arrays.asList("Время прохождения:", "Собрано грибов:"));
        ArrayList<String> stats = new ArrayList<>(Arrays.asList(time, "6"));

        Table scrollContent = new Table();
        for (int i = 0; i < titles.size(); i++) {
            Actor actor = new Item(titles.get(i), stats.get(i));
            actor.setName(titles.get(i));
            scrollContent.add(actor).padTop(15f).padBottom(15f).row();
        }

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = skin.getDrawable("menuBtn_down");
        scrollPaneStyle.vScrollKnob.setMinWidth(25f);
        //scrollPaneStyle.background = skin.getDrawable("borders");

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setStyle(scrollPaneStyle);
        scrollPane.setCancelTouchFocus(false);

        uiTable.add(scrollPane).width(getPrefWidth() / 1.2f).height(getPrefHeight() / 1.1f).align(Align.center).expand();
    }

    private static String timeToString(float time) {
        long hour = (long) (time / 3600f),
                min = (long) ((time % 3600) / 60),
                sec = (long) (time % 60f);
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

    public class Item extends Table {
        private Label tilteLabel;
        private Label textLabel;

        public Item(String titleText, String stat) {
            super(skin);
            Table ui = new Table();
            ui.background(getSkin().getDrawable("menuBtn_up"));
            add(ui).width(Gdx.graphics.getWidth() / 2.5f).height(Gdx.graphics.getHeight() / 7f);

            tilteLabel = new Label("\n", lstyle);
            tilteLabel.setText(titleText);
            tilteLabel.setAlignment(Align.center);

            textLabel = new Label("\n", lstyle);
            textLabel.setText("" + stat);
            textLabel.setAlignment(Align.center);

            ui.add(tilteLabel).left().expand();
            ui.add(textLabel).right().expand();
        }
    }
}
