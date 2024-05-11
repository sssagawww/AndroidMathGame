package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.Sort;
import com.mygdx.game.MyGdxGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class Inventory extends Table {
    private Table uiTable;
    private Table scrollContent;
    private Table items;
    private Skin skin;
    private Label.LabelStyle lstyle;
    private Label.LabelStyle titleStyle;
    private Label nothingLabel;
    private Image[] images;
    private Image playerImage;
    private Image ringImage;
    private Image swordImage;
    private Image amuletImage;
    private Image exitImage;
    private ArrayList<String> titles;
    private HashMap<String, Item> itemsList = new HashMap<>();
    private int artefacts = 0;

    public Inventory(Skin skin, Controller controller) {
        super(skin);
        this.skin = skin;
        uiTable = new Table();
        this.add(uiTable).width(V_WIDTH / 1.2f).height(V_HEIGHT / 1.2f);
        this.setBackground("menuBtn_up");

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        font.getData().setScale(0.7f);
        lstyle = new Label.LabelStyle(font, Color.DARK_GRAY);
        titleStyle = new Label.LabelStyle(font, Color.BLACK);

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

        ringImage = new Image(new Texture("controller/square.png"));

        //картинки 3 предметов
        ringImage = new Image(new Texture("UI/ring.png"));
        swordImage = new Image(new Texture("UI/sword.png"));
        amuletImage = new Image(new Texture("UI/amulet.png"));
        images = new Image[]{ringImage, swordImage, amuletImage};

        playerImage = new Image(new Texture("entitySprites/idle.png"));

        Table rightTable = new Table(getSkin());
        uiTable.add(exitImage).align(Align.topLeft).width(exitImage.getWidth() * 5).height(exitImage.getHeight() * 5);
        rightTable.add(playerImage).width(playerImage.getWidth() * 1.5f).height(playerImage.getHeight() * 1.5f).align(Align.center).padLeft(25f).row();

        Table imagesTable = new Table();
        //???
        for (int i = 0; i < images.length; i++) {
            Table imageTable = new Table(getSkin());
            imageTable.setBackground("borders");
            imageTable.add(images[i]).width(V_WIDTH / 10f).height(V_WIDTH / 10f);
            imagesTable.add(imageTable).align(Align.bottom).expand().pad(15f);
            images[i].setVisible(false);
        }
        rightTable.add(createItems()).row();
        rightTable.add(imagesTable).padLeft(25f).row();

        Label artifacts = new Label("\n", titleStyle);
        artifacts.setText("Артефакты");
        rightTable.add(artifacts);

        //uiTable.debug();
        uiTable.add(rightTable).right();
        uiTable.add(createAchievements()).expand();
    }

    private Table createAchievements() {
        ArrayList<String> achievements = new ArrayList<>(Arrays.asList("Соберите все\nчудесные грибы.", "Вспугните зайца.", "Вытяните меч\n с силой 100.", "Как он\nтуда поместился?"));
        titles = new ArrayList<>(Arrays.asList("Грибной повелитель", "Охотник\nза мгновениями", "Легендарный воин", "Сундук XXL"));

        scrollContent = new Table();
        for (int i = 0; i < achievements.size(); i++) {
            Actor actor = new Achievement(titles.get(i), achievements.get(i));
            actor.setName(titles.get(i));
            scrollContent.add(actor).padTop(15f).padBottom(15f).row();
        }

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = skin.getDrawable("menuBtn_down");
        scrollPaneStyle.vScrollKnob.setMinWidth(25f);
        scrollPaneStyle.background = skin.getDrawable("borders");

        ScrollPane scrollPane = new ScrollPane(scrollContent);
        scrollPane.setStyle(scrollPaneStyle);
        scrollPane.setCancelTouchFocus(false);

        Table achievementsTable = new Table(getSkin());
        Label label = new Label("\n", titleStyle);
        label.setText("Достижения");

        achievementsTable.add(label).row();
        achievementsTable.add(scrollPane).width(getPrefWidth() / 2.5f).height(getPrefHeight() / 1.1f).align(Align.left).pad(10f);
        return achievementsTable;
    }

    private Table createItems() {
        items = new Table();

        nothingLabel = new Label("\n", lstyle);
        nothingLabel.setText("Пока в сумке ничего нет");
        nothingLabel.setName("nothing");
        items.add(nothingLabel);

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.vScrollKnob = skin.getDrawable("menuBtn_down");
        scrollPaneStyle.vScrollKnob.setMinWidth(25f);
        scrollPaneStyle.background = skin.getDrawable("borders");

        ScrollPane scrollPane = new ScrollPane(items);
        scrollPane.setStyle(scrollPaneStyle);
        scrollPane.setOverscroll(true, false);

        Table itemsTable = new Table(getSkin());
        itemsTable.add(scrollPane).width(getPrefWidth() / 2f).height(getPrefHeight() / 4.5f).align(Align.left).pad(10f);
        return itemsTable;
    }

    public void addItem(String name) {
        if (items.findActor("nothing") != null) {
            items.removeActor(nothingLabel);
        }
        Item actor = new Item(name);
        itemsList.put(name, actor);
        items.add(actor).padRight(15f).padLeft(15f);
    }

    public void removeItem(String name){
        items.removeActor(itemsList.get(name));
        itemsList.remove(name);
        System.out.println(itemsList);
        if (itemsList == null) {
            items.add(nothingLabel);
        }
    }

    public Item getItem(String name) {
        return itemsList.get(name);
    }

    public void setImgVisibility(int num, boolean visibility) {
        images[num].setVisible(visibility);
        artefacts++;
    }

    public int getArtefacts() {
        return artefacts;
    }

    public void setArtefacts(int artefacts) {
        this.artefacts = artefacts;
    }

    public void setAchievementVisibility(int num) {
        Achievement i = scrollContent.findActor(titles.get(num));
        i.setTextVisibility(true);
        i.changeImage();
    }

    public class Achievement extends Table {
        private Label textLabel;
        private Image image;

        public Achievement(String titleText, String text) {
            super(skin);
            background(getSkin().getDrawable("menuBtn_up"));

            image = new Image(new Texture("controller/questionMark.png"));

            Label title = new Label("\n", titleStyle);
            title.setText(titleText);
            title.setAlignment(Align.center);

            textLabel = new Label("\n", lstyle);
            textLabel.setText(text);
            textLabel.setAlignment(Align.center);
            textLabel.setVisible(false);

            Table table = new Table();
            table.add(title).align(Align.center).width(V_WIDTH / 5f).height(V_HEIGHT / 12f).row();
            table.add(textLabel).align(Align.center).width(V_WIDTH / 5f).height(V_HEIGHT / 12f);

            add(image).align(Align.left).width(V_WIDTH / 12f).height(V_WIDTH / 12f);
            add(table).align(Align.right);
        }

        public Label getTextLabel() {
            return textLabel;
        }

        public Image getImage() {
            return image;
        }

        public void changeImage() {
            image.setDrawable(skin.getDrawable("ok"));
        }

        public void setTextVisibility(boolean visibility) {
            textLabel.setVisible(visibility);
        }
    }

    public class Item extends Table {
        private Label textLabel;
        private Image image;
        private String name;
        private int count = 1;

        public Item(String name) {
            super(skin);
            this.name = name;
            background(getSkin().getDrawable("menuBtn_up"));

            image = new Image(getSkin().getDrawable(name));

            textLabel = new Label("\n", titleStyle);
            textLabel.setText(name + " " + count + " шт.");
            textLabel.setAlignment(Align.center);
            textLabel.setVisible(true);

            add(image).align(Align.center).width(V_WIDTH / 24f).height(V_WIDTH / 24f).row();
            add(textLabel).align(Align.center).width(V_WIDTH / 10f).height(V_HEIGHT / 24f);
        }

        public void addItemCount() {
            count++;
            textLabel.setText(name + " " + count + " шт.");
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Label getTextLabel() {
            return textLabel;
        }

        public Image getImage() {
            return image;
        }

        public void setVisibility(boolean visibility) {
            textLabel.setVisible(visibility);
            image.setVisible(visibility);
        }
    }
}
