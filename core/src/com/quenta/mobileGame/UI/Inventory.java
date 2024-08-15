package com.quenta.mobileGame.UI;

import static com.quenta.mobileGame.MyGdxGame.*;

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
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.quenta.mobileGame.db.DbWrapper;
import com.quenta.mobileGame.db.Progress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Inventory extends Table {
    private Table scrollContent;
    private Table items;
    private final Skin skin;
    private final Label.LabelStyle lstyle;
    private final Label.LabelStyle titleStyle;
    private Label nothingLabel;
    private final Image[] images;
    private ArrayList<String> titles;
    private final HashMap<String, Item> itemsList = new HashMap<>();
    private int artefacts = 0;

    public Inventory(Skin skin, Controller controller) {
        super(skin);
        this.skin = skin;
        Table uiTable = new Table();
        this.add(uiTable).width(Gdx.graphics.getWidth() / 1.2f).height(Gdx.graphics.getHeight() / 1.1f);
        this.setBackground("menuBtn_up");

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        font.getData().setScale(1.0f);
        lstyle = new Label.LabelStyle(font, Color.DARK_GRAY);
        titleStyle = new Label.LabelStyle(font, Color.BLACK);

        Image exitImage = new Image(skin.getDrawable("wrong"));
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

        //картинки 3 предметов
        Image ringImage = new Image(new Texture("UI/ring.png"));
        Image swordImage = new Image(new Texture("UI/sword.png"));
        Image amuletImage = new Image(new Texture("UI/amulet2.png"));
        images = new Image[]{ringImage, swordImage, amuletImage};

        Image playerImage = new Image(new Texture("entitySprites/idle.png"));

        Table rightTable = new Table(getSkin());
        uiTable.add(exitImage).align(Align.topLeft).width(Gdx.graphics.getHeight() / 12f).height(Gdx.graphics.getHeight() / 12f);
        rightTable.add(playerImage).width(Value.percentWidth(0.25f, rightTable)).height(Value.percentWidth(0.25f, rightTable)).align(Align.center).padLeft(25f).expand().row();

        Table imagesTable = new Table();
        ArrayList<String> arts = new ArrayList<>(Arrays.asList("Кольцо\nМудрости", "Меч\nСилы", "Амулет\nВремени"));
        //???
        for (int i = 0; i < images.length; i++) {
            Table imageTable = new Table(getSkin());

            Label name = new Label("\n", titleStyle);
            name.setText(arts.get(i));
            name.setAlignment(Align.center);

            imageTable.setBackground("borders");
            imageTable.add(images[i]).width(Gdx.graphics.getWidth() / 10f).height(Gdx.graphics.getWidth() / 10f).row();
            imageTable.add(name);

            imagesTable.add(imageTable).align(Align.bottom).expand().pad(15f);
            images[i].setVisible(false);
        }
        rightTable.add(createItems()).expand().row();
        rightTable.add(imagesTable).padLeft(25f).expand().row();

        Label artifacts = new Label("\n", titleStyle);
        artifacts.setText("Артефакты");
        rightTable.add(artifacts).expand().top();

        uiTable.add(rightTable).right().height(Value.percentHeight(1F, uiTable));
        uiTable.add(createAchievements()).expand();
    }

    private Table createAchievements() {
        ArrayList<String> achievements = new ArrayList<>(Arrays.asList("Соберите все\nчудесные грибы.", "Вспугните зайца.", "Вытяните меч\n с силой 100.", "Как он\nтуда поместился?", "Сбейте цену\nдо 5 грибов."));
        titles = new ArrayList<>(Arrays.asList("Грибной повелитель", "Охотник\nза мгновениями", "Легендарный воин", "Сундук XXL", "Красноречие 100"));

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

    public void reload(DbWrapper dbWrapper) {
        Progress progress = dbWrapper.getProgress().get(dbWrapper.getProgress().size() - 1);
        gameTime = progress.getTime();
        setImgVisibility(0, progress.isRingImageVisible());
        setImgVisibility(1, progress.isSwordImageVisible());
        setImgVisibility(2, progress.isAmuletImageVisible());
        setArtefacts(progress.getArtefactsCount());
        for (int i = 0; i < progress.getAchievements().size(); i++) {
            setAchievementVisibility(progress.getAchievements().get(i));
        }
        HashMap<String, Integer> items = progress.getItems();
        for (String item : items.keySet()) {
            addItem(item);
            getItem(item).setCount(items.get(item));
        }
    }

    public void addItem(String name) {
        if (items.findActor("nothing") != null) {
            items.removeActor(nothingLabel);
        }
        if (itemsList.containsKey(name)) {
            getItem(name).addItemCount();
        } else {
            Item actor = new Item(name);
            itemsList.put(name, actor);
            items.add(actor).padRight(15f).padLeft(15f);
        }
    }

    public void removeItem(String name) {
        items.removeActor(itemsList.get(name));
        itemsList.remove(name);
        System.out.println(itemsList + " itemsList");
        if (itemsList.isEmpty()) {
            items.add(nothingLabel);
        }
    }

    public Item getItem(String name) {
        return itemsList.get(name);
    }

    public HashMap<String, Integer> getItems() {
        HashMap<String, Integer> items = new HashMap<>();
        for (String item : itemsList.keySet()) {
            items.put(item, itemsList.get(item).getCount());
        }
        return items;
    }

    public void setImgVisibility(int num, boolean visibility) {
        images[num].setVisible(visibility);
        artefacts++;
    }

    public boolean getImgVisibility(int num) {
        return images[num].isVisible();
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

    public ArrayList<Integer> getAchievementsVisibility() {
        ArrayList<Integer> acs = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            Achievement actor = scrollContent.findActor(titles.get(i));
            if (actor.getTextVisibility()) {
                acs.add(i);
            }
        }
        return acs;
    }

    public class Achievement extends Table {
        private final Label textLabel;
        private final Image image;

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
            table.add(title).align(Align.center).width(Gdx.graphics.getWidth() / 5f).height(Gdx.graphics.getHeight() / 12f).row();
            table.add(textLabel).align(Align.center).width(Gdx.graphics.getWidth() / 5f).height(Gdx.graphics.getHeight() / 12f);

            add(image).align(Align.left).width(Gdx.graphics.getWidth() / 12f).height(Gdx.graphics.getWidth() / 12f);
            add(table).align(Align.right);
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

        public boolean getTextVisibility() {
            return textLabel.isVisible();
        }
    }

    public class Item extends Table {
        private final Label textLabel;
        private final Image image;
        private final String name;
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

            add(image).align(Align.center).width(Gdx.graphics.getWidth() / 24f).height(Gdx.graphics.getWidth() / 24f).row();
            add(textLabel).align(Align.center).width(Gdx.graphics.getWidth() / 10f).height(Gdx.graphics.getHeight() / 24f).pad(10f);
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
            textLabel.setText(name + " " + count + " шт.");
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
