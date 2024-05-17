package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

/*сетаются кнопки контроллера на экране: каждая из них просто картинка, ставящая true or false для переменной btnNamePressed, когда на неё нажимают.
Player2 проверяет состояние переменной и ставит нужную траекторию, если она true*/
public class Controller extends Table {
    private Image settingsImg;
    private boolean menuPressed;
    private Image menuImg;
    private Image inventImg;
    private Inventory inventory;
    private SoundSettings soundSettings;
    private Cell cell;

    public Controller(Skin skin) {
        super(skin);
        this.setFillParent(true);

        inventory = new Inventory(skin, this);
        inventory.setVisible(false);

        soundSettings = new SoundSettings(skin, this);
        soundSettings.setVisible(false);

        // Эту реализацию можно доработать или переделать (не конечный вариант)
        // --сделать атлас текстур и использовать его вместо нескольких картинок, как в других классах

        //кнопки взаимодействия
        menuImg = new Image(new Texture("controller/menuBtn.png"));
        menuImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                menuPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                menuPressed = false;
            }
        });

        inventImg = new Image(new Texture("controller/circle.png"));
        inventImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setBtnsVisibility(false);
                inventory.setVisible(true);
                cell.setActor(inventory);
                return true;
            }
        });

        settingsImg = new Image(new Texture("controller/settings.png"));
        settingsImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setBtnsVisibility(false);
                soundSettings.setVisible(true);
                cell.setActor(soundSettings);
                return true;
            }
        });

        //добавление в таблицу и выравнивание
        Table table = new Table();
        table.add(menuImg).width(menuImg.getWidth() * 5).height(menuImg.getHeight() * 5).align(Align.topLeft).row();
        table.add(settingsImg).width(settingsImg.getWidth() * 5).height(settingsImg.getHeight() * 5).align(Align.topRight);
        this.add(table).top();
        this.add(inventory).expand().align(Align.center);
        this.add(inventImg).width(inventImg.getWidth() * 5).height(inventImg.getHeight() * 5).align(Align.topRight);
        cell = getCell(inventory);
    }

    public SoundSettings getSoundSettings() {
        return soundSettings;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isMenuPressed() {
        return menuPressed;
    }

    public void setBtnsVisibility(boolean visibility) {
        menuImg.setVisible(visibility);
        inventImg.setVisible(visibility);
        settingsImg.setVisible(visibility);
    }

    public boolean isInventoryVisible() {
        return inventory.isVisible() || soundSettings.isVisible();
    }
}
