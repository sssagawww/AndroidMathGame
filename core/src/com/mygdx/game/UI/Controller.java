package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

/*сетаются кнопки контроллера на экране: каждая из них просто картинка, ставящая true or false для переменной btnNamePressed, когда на неё нажимают.
Player2 проверяет состояние переменной и ставит нужную траекторию, если она true*/
public class Controller extends Table {
    private boolean menuPressed;
    private Image menuImg;
    private Image inventImg;
    private Inventory inventory;

    public Controller(Skin skin) {
        super(skin);
        this.setFillParent(true);

        inventory = new Inventory(skin, this);
        inventory.setVisible(false);

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
                return true;
            }
        });

        //добавление в таблицу и выравнивание
        this.add(menuImg).width(menuImg.getWidth()*5).height(menuImg.getHeight()*5).align(Align.topLeft);
        this.add(inventory).expand().align(Align.center);
        this.add(inventImg).width(inventImg.getWidth()*5).height(inventImg.getHeight()*5).align(Align.topRight);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isMenuPressed() {
        return menuPressed;
    }

    public void setBtnsVisibility(boolean visibility){
        menuImg.setVisible(visibility);
        inventImg.setVisible(visibility);
    }

    public boolean isInventoryVisible() {
        return inventory.isVisible();
    }
}
