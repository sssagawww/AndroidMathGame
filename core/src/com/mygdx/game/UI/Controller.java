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

    public Controller(Skin skin) {
        super(skin);
        Table uiTable = new Table();
        Table uiTableRight = new Table();
        Table uiTableTop = new Table();
        this.add(uiTableTop).align(Align.topLeft).pad(0, 0, V_HEIGHT / 1.11f, 0);
        this.add(uiTable).align(Align.bottomLeft);
        this.add(uiTableRight).align(Align.right).pad(V_HEIGHT / 1.8f, V_WIDTH / 1.9f, 0, 0);

        // Эту реализацию можно доработать или переделать (не конечный вариант)
        // --сделать атлас текстур и использовать его вместо нескольких картинок, как в других классах

        //кнопки взаимодействия
        Image menuImg = new Image(new Texture("menuBtn.png"));
        menuImg.setScale(5, 5);
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

        //добавление в таблицу и выравнивание
        //uiTableRight.add(interactImg);
        uiTableTop.add(menuImg);
    }

    public boolean isMenuPressed() {
        return menuPressed;
    }
}
