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
    private boolean inventoryPressed;

    public Controller(Skin skin) {
        super(skin);
        Table uiTableRight = new Table();
        Table uiTableTop = new Table();
        this.setFillParent(true);
        this.add(uiTableTop).align(Align.topLeft).expand();
        this.add(uiTableRight).align(Align.topRight).expand();

        // Эту реализацию можно доработать или переделать (не конечный вариант)
        // --сделать атлас текстур и использовать его вместо нескольких картинок, как в других классах

        //кнопки взаимодействия
        Image menuImg = new Image(new Texture("controller/menuBtn.png"));
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

        Image inventImg = new Image(new Texture("controller/circle.png"));
        inventImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                inventoryPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                inventoryPressed = false;
            }
        });

        //добавление в таблицу и выравнивание
        uiTableTop.add(menuImg).width(menuImg.getWidth()*5).height(menuImg.getHeight()*5);
        uiTableRight.add(inventImg).width(inventImg.getWidth()*5).height(inventImg.getHeight()*5);
    }

    public boolean isMenuPressed() {
        return menuPressed;
    }

    public boolean isInventoryPressed() {
        return inventoryPressed;
    }
}
