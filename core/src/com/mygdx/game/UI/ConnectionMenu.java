package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.PREF_USERNAME;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.multiplayer.MushroomsRequest;

public class ConnectionMenu extends Table {
    private Table uiTable;
    private TextField ipField;
    private TextField nameField;
    private String lastText = "null";

    public ConnectionMenu(Skin skin, GameStateManager gsm) {
        super(skin);
        uiTable = new Table();
        this.add(uiTable);

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        font.getData().setScale(0.7f);

        TextField.TextFieldStyle tStyle = new TextField.TextFieldStyle();
        tStyle.background = skin.getDrawable("menuBtn_up");
        tStyle.fontColor = Color.BLACK;
        tStyle.font = font;
        tStyle.messageFontColor = Color.DARK_GRAY;

        ipField = new TextField("", tStyle);
        ipField.setMessageText("IP сервера");
        ipField.setPosition(0, 0);
        ipField.setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 4f);

        nameField = new TextField("", tStyle);
        if(!MyGdxGame.getPrefs().getString(PREF_USERNAME).equals("name")){
            nameField.setText(MyGdxGame.getPrefs().getString(PREF_USERNAME));
        }
        nameField.setMessageText("Имя игрока");
        nameField.setPosition(0, 0);
        nameField.setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 4f);

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
                        if(!nameField.getText().equals("")){
                            MushroomsRequest.setName(nameField.getText());
                            MyGdxGame.getPrefs().putString(PREF_USERNAME, nameField.getText()).flush();
                        } else {
                            nameField.setText(MyGdxGame.getPrefs().getString(PREF_USERNAME));
                            MushroomsRequest.setName(nameField.getText());
                        }
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
                        ipField.setText("");
                        ipField.setMessageText("Недействительный IP");
                        MushroomsRequest.setIp("");
                    } else {
                        MushroomsRequest.setUnableToConnect(false);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        uiTable.add(ipField).width(Gdx.graphics.getWidth() / 3f).height(Gdx.graphics.getHeight() / 8f).pad(10f);
        uiTable.add(btn).align(Align.right).width(70f).height(70f).row();
        uiTable.add(nameField).width(Gdx.graphics.getWidth() / 3f).height(Gdx.graphics.getHeight() / 8f).pad(10f).row();
    }

    public String getIpText() {
        return ipField.getText();
    }

    public void setIpText(String text) {
        ipField.setText(text);
    }

    public TextField getIpField() {
        return ipField;
    }

    public TextField getNameField() {
        return nameField;
    }
}
