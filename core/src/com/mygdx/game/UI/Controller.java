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
    private boolean upPressed, downPressed, rightPressed, leftPressed, upRightPressed, upLeftPressed, downRightPressed, downLeftPressed, interactPressed, menuPressed;

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

        // Кнопки перемещения
        Image upImg = new Image(new Texture("upBtn.png"));
        upImg.setScale(7, 7);
        upImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;
            }
        });

        Image downImg = new Image(new Texture("downBtn.png"));
        downImg.setScale(7, 7);
        downImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downPressed = false;
            }
        });

        Image rightImg = new Image(new Texture("rightBtn.png"));
        rightImg.setScale(7, 7);
        rightImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });

        Image leftImg = new Image(new Texture("leftBtn.png"));
        leftImg.setScale(7, 7);
        leftImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });

        Image upRightImg = new Image(new Texture("upRightBtn.png"));
        upRightImg.setScale(7, 7);
        upRightImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upRightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upRightPressed = false;
            }
        });

        Image upLeftImg = new Image(new Texture("upLeftBtn.png"));
        upLeftImg.setScale(7, 7);
        upLeftImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                upLeftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upLeftPressed = false;
            }
        });

        Image downRightImg = new Image(new Texture("downRightBtn.png"));
        downRightImg.setScale(7, 7);
        downRightImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downRightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downRightPressed = false;
            }
        });

        Image downLeftImg = new Image(new Texture("downLeftBtn.png"));
        downLeftImg.setScale(7, 7);
        downLeftImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                downLeftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                downLeftPressed = false;
            }
        });

        //кнопки взаимодействия
        Image interactImg = new Image(new Texture("interactBtn.png"));
        interactImg.setScale(7, 7);
        interactImg.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                interactPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                interactPressed = false;
            }
        });

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
        uiTable.row().pad(45f);
        uiTable.add(upLeftImg);
        uiTable.add(upImg);
        uiTable.add(upRightImg);
        uiTable.row().pad(45f);
        uiTable.add(leftImg);
        uiTable.add();
        uiTable.add(rightImg);
        uiTable.row().pad(45f);
        uiTable.add(downLeftImg);
        uiTable.add(downImg);
        uiTable.add(downRightImg);

        uiTableRight.add(interactImg);
        uiTableTop.add(menuImg);
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isUpRightPressed() {
        return upRightPressed;
    }

    public boolean isUpLeftPressed() {
        return upLeftPressed;
    }

    public boolean isDownRightPressed() {
        return downRightPressed;
    }

    public boolean isDownLeftPressed() {
        return downLeftPressed;
    }

    public boolean isInteractPressed() {
        return interactPressed;
    }

    public boolean isMenuPressed() {
        return menuPressed;
    }
}
