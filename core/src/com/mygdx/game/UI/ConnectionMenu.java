package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.V_HEIGHT;
import static com.mygdx.game.MyGdxGame.V_WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.multiplayer.MushroomsRequest;
import com.mygdx.game.states.MushroomsState;

public class ConnectionMenu extends Table {
    private Table uiTable;
    private Skin skin;
    private Label.LabelStyle lStyle;
    private TextField ipField;
    private TextField nameField;
    private String lastText = "null";

    public ConnectionMenu(Skin skin, GameStateManager gsm) {
        super(skin);
        this.skin = skin;
        uiTable = new Table();
        this.add(uiTable);

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        font.getData().setScale(0.7f);
        lStyle = new Label.LabelStyle(font, Color.BLACK);

        TextField.TextFieldStyle tStyle = new TextField.TextFieldStyle();
        tStyle.background = skin.getDrawable("menuBtn_up");
        tStyle.fontColor = Color.BLACK;
        tStyle.font = font;
        tStyle.messageFontColor = Color.DARK_GRAY;

        ipField = new TextField("", tStyle);
        ipField.setMessageText("IP сервера");
        ipField.setPosition(0, 0);
        ipField.setSize(V_WIDTH / 3f, V_HEIGHT / 4f);

        nameField = new TextField("", tStyle);
        nameField.setMessageText("Имя игрока");
        nameField.setPosition(0, 0);
        nameField.setSize(V_WIDTH / 3f, V_HEIGHT / 4f);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        ImageButton btn = new ImageButton(style);
        style.up = skin.getDrawable("ok");
        style.down = skin.getDrawable("ok_down");

        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                try{
                    if (!ipField.getText().equals("") && !ipField.getText().equals("Введите IP!") && !lastText.equals(ipField.getText())) {
                        lastText = ipField.getText();
                        MushroomsRequest.setIp(ipField.getText());
                        gsm.game().getRequest().ping();
                    }
                } catch (Exception e){
                    System.out.println(e.fillInStackTrace());
                }

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    Thread.sleep(100);
                    if (MushroomsRequest.isUnableToConnect()) {
                        ipField.setText("Недействительный IP");
                        MushroomsRequest.setIp("");
                    } else {
                        MushroomsRequest.setUnableToConnect(false);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        uiTable.add(ipField).width(V_WIDTH / 3f).height(V_HEIGHT / 8f).pad(10f);
        uiTable.add(btn).align(Align.right).width(70f).height(70f).row();
        //uiTable.add(nameField).width(V_WIDTH / 3f).height(V_HEIGHT / 8f).pad(10f).row();
    }

    public String getText() {
        return ipField.getText();
    }

    public void setText(String text) {
        ipField.setText(text);
    }

    public TextField getIpField() {
        return ipField;
    }
}
