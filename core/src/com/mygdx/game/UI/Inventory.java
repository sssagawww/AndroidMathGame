package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.*;
import static com.mygdx.game.handlers.GameStateManager.PLAY;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.states.Play;

public class Inventory extends Table {
    private MyGdxGame game;
    private Table uiTable;
    private Label.LabelStyle lstyle;
    private Image playerImage;
    private Image ringImage;
    private Image swordImage;
    private Image amuletImage;
    private Image exitImage;

    public Inventory(Skin skin, Play play) {
        super(skin);
        uiTable = new Table();
        this.add(uiTable).width(V_WIDTH / 1.2f).height(V_HEIGHT / 1.2f);
        this.setBackground("menuBtn_up");

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        lstyle = new Label.LabelStyle(font, Color.BLACK);
        lstyle.background = getSkin().getDrawable("borders");

        exitImage = new Image(skin.getDrawable("wrong"));
        exitImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setVisible(false);
                play.getController().setVisible(true);
            }
        });

        ringImage = new Image(new Texture("controller/square.png"));

        //картинки 3 предметов
        ringImage = new Image(new Texture("controller/square.png"));
        swordImage = new Image(new Texture("controller/star.png"));
        amuletImage = new Image(new Texture("controller/circle.png"));

        Image[] images = new Image[]{ringImage, swordImage, amuletImage};

        playerImage = new Image(new Texture("entitySprites/idle.png"));

        Table rightTable = new Table(getSkin());
        rightTable.add(exitImage).align(Align.topLeft).width(exitImage.getWidth()*5).height(exitImage.getHeight()*5).row();
        rightTable.add(playerImage).width(playerImage.getWidth()*2).height(playerImage.getHeight()*2).align(Align.center).padLeft(25f).row();

        Table imagesTable = new Table();
        //???
        for (int i = 0; i < images.length; i++) {
            Table imageTable = new Table(getSkin());
            imageTable.setBackground("borders");
            imageTable.add(images[i]).width(V_WIDTH / 10f).height(V_WIDTH / 10f);
            imagesTable.add(imageTable).align(Align.bottom).expand().pad(15f);
        }
        rightTable.add(imagesTable).padLeft(25f);

        Table achievementsTable = new Table(getSkin());
        achievementsTable.setBackground("borders");

        //rightTable.debug();
        uiTable.add(rightTable).center();
        uiTable.add(achievementsTable).width(getPrefWidth()/2.5f).height(getPrefHeight()/1.1f).align(Align.right).pad(10f).expand();
    }
}
