package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.MUSHROOMS_GAME;
import static com.mygdx.game.MyGdxGame.PAINT_GAME;
import static com.mygdx.game.MyGdxGame.PREF_ID;
import static com.mygdx.game.MyGdxGame.PREF_USERNAME;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.handlers.GameStateManager;
import com.mygdx.game.multiplayer.MushroomsRequest;

public class ConnectionMenu extends Table {
    private Table uiTable;
    private Table rightTable;
    private TextField ipField;
    private TextField nameField;
    private TextField roomIdField;
    private Label statusLabel;
    private TextButton.TextButtonStyle textBtnStyle;
    private TextField.TextFieldStyle tStyle;
    private Label.LabelStyle lStyle;
    private String lastText = "null";
    private MushroomsRequest request;
    private String miniGame;
    private final int id = MyGdxGame.getPrefs().getInteger(PREF_ID);

    public ConnectionMenu(Skin skin, GameStateManager gsm) {
        super(skin);
        uiTable = new Table();
        request = gsm.game().getRequest();
        this.add(uiTable);

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        BitmapFont btnFont = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        font.getData().setScale(0.7f);

        //стиль для текста
        lStyle = new Label.LabelStyle(btnFont, Color.BLACK);

        //стиль для полей ввода
        tStyle = new TextField.TextFieldStyle();
        tStyle.background = skin.getDrawable("menuBtn_up");
        tStyle.fontColor = Color.BLACK;
        tStyle.font = font;
        tStyle.messageFontColor = Color.DARK_GRAY;

        //стиль для кнопок с текстом
        textBtnStyle = new TextButton.TextButtonStyle();
        textBtnStyle.font = btnFont;
        textBtnStyle.fontColor = Color.BLACK;
        textBtnStyle.downFontColor = Color.BLACK;
        textBtnStyle.up = getSkin().getDrawable("menuBtn_up");
        textBtnStyle.down = getSkin().getDrawable("menuBtn_down");

        //стиль для кнопки-галочки
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.up = skin.getDrawable("ok");
        style.down = skin.getDrawable("ok_down");

        //поле ввода ip сервера
        ipField = new TextField("", tStyle);
        ipField.setText("192.168.1.42:8080");
        ipField.setMessageText("IP сервера");
        ipField.setPosition(0, 0);
        ipField.setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 4f);

        //поле ввода имени игрока
        nameField = new TextField("", tStyle);
        if (!MyGdxGame.getPrefs().getString(PREF_USERNAME).equals("name")) {
            nameField.setText(MyGdxGame.getPrefs().getString(PREF_USERNAME));
        }
        nameField.setMessageText("Имя игрока");
        nameField.setPosition(0, 0);
        nameField.setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 4f);

        statusLabel = new Label("Статус:", lStyle);
        statusLabel.setAlignment(Align.center);

        //галочка
        ImageButton okBtn = new ImageButton(style);
        okBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    if (ipField.getText().equals("")) {
                        getIpField().setMessageText("Введите IP!");
                        statusLabel.setText("Статус: IP не введено");
                    } else if (!ipField.getText().equals("") && !ipField.getText().equals("Введите IP!") && !lastText.equals(ipField.getText())) {
                        lastText = ipField.getText();
                        MushroomsRequest.setIp(ipField.getText());
                        if (!nameField.getText().equals("")) {
                            MushroomsRequest.setName(nameField.getText());
                            MyGdxGame.getPrefs().putString(PREF_USERNAME, nameField.getText()).flush();
                        } else {
                            nameField.setText(MyGdxGame.getPrefs().getString(PREF_USERNAME));
                            MushroomsRequest.setName(nameField.getText());
                        }
                        gsm.game().getRequest().ping();
                    }
                } catch (Exception e) {
                    System.out.println(e.fillInStackTrace());
                }

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    Thread.sleep(250);
                    if (MushroomsRequest.isUnableToConnect() && !ipField.getText().equals("") && !ipField.getText().equals("Введите IP!")) {
                        ipField.setText("");
                        ipField.setMessageText("Недействительный IP");
                        statusLabel.setText("Статус: Недействительный IP");
                        MushroomsRequest.setIp("");
                    } else if (!MushroomsRequest.isUnableToConnect()) {
                        MushroomsRequest.setUnableToConnect(false);
                        statusLabel.setText("Статус: Можно подключиться");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        //инструкция
        Label textLabel = new Label("Режимы рассчитаны на 2 игроков.\nВведите IP с портом и ник, затем\nнажмите на зеленую галочку,\nчтобы подтвердить ввод.\nСтатус подключения\nотображается сверху.", lStyle);
        textLabel.setFontScale(0.8f);
        textLabel.setAlignment(Align.center);

        Table leftTable = new Table();
        leftTable.add(statusLabel).pad(15f).row();
        leftTable.add(ipField).width(Gdx.graphics.getWidth() / 3f).height(Gdx.graphics.getHeight() / 8f).pad(10f);
        leftTable.add(okBtn).align(Align.right).width(70f).height(70f).row();
        leftTable.add(nameField).width(Gdx.graphics.getWidth() / 3f).height(Gdx.graphics.getHeight() / 8f).pad(10f).row();
        leftTable.add(textLabel).padBottom(okBtn.getHeight() * 3).padTop(okBtn.getHeight() * 3).row();

        createRightTable();

        uiTable.add(leftTable).width(Gdx.graphics.getWidth() / 2.2f).left();
        uiTable.add(rightTable).width(Gdx.graphics.getWidth() / 2.2f).left();
    }

    private void createRightTable() {
        //заголвок-подсказка, что нужно делать
        Label textLabel = new Label("Выбранный режим:", lStyle);
        textLabel.setAlignment(Align.center);

        //кнопка для выбора режима с грибами
        TextButton mushrooms = new TextButton("Сбор грибов", textBtnStyle);
        mushrooms.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                miniGame = MUSHROOMS_GAME;
                textLabel.setText("Выбранный режим: Сбор грибов");
            }
        });

        //кнопка для выбора режима с рисованием
        TextButton drawings = new TextButton("Рисование", textBtnStyle);
        drawings.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                miniGame = PAINT_GAME;
                textLabel.setText("Выбранный режим: Рисование");
            }
        });

        //заголовок
        Label roomLabel = new Label("Комната:", lStyle);
        roomLabel.setAlignment(Align.center);

        //кнопка создания команты
        TextButton createRoomBtn = new TextButton("Создать комнату", textBtnStyle);
        createRoomBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (statusLabel.getText().toString().equals("Статус: Можно подключиться") && !textLabel.getText().toString().equals("Выбранный режим:")) {
                    request.createRoom(id, miniGame);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    roomLabel.setText("Комната: создана");
                    roomIdField.setText(request.getRoomId() + "");
                } else {
                    roomLabel.setText("Комната: введена не вся информация!");
                }
            }
        });

        //поле ввода id комнаты
        roomIdField = new TextField("", tStyle);
        roomIdField.setMessageText("ID комнаты");
        roomIdField.setPosition(0, 0);
        roomIdField.setSize(Gdx.graphics.getWidth() / 3f, Gdx.graphics.getHeight() / 4f);

        //кнопка подкючения к команте
        TextButton joinRoomBtn = new TextButton("Войти в комнату", textBtnStyle);
        joinRoomBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!roomIdField.getText().isEmpty()) {
                    request.joinRoom(id, Integer.parseInt(roomIdField.getText()));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(!request.isJoined()){
                        roomLabel.setText("Комната: не найдена");
                    } else {
                        request.setRoomId(Integer.parseInt(roomIdField.getText()));
                    }
                } else {
                    roomLabel.setText("Комната: введите ID!");
                }
            }
        });

        rightTable = new Table();
        rightTable.add(textLabel).row();
        rightTable.add(mushrooms).pad(10f).row();
        rightTable.add(drawings).row();
        rightTable.add(roomLabel).pad(10f).row();
        rightTable.add(roomIdField).width(Gdx.graphics.getWidth() / 3f).height(Gdx.graphics.getHeight() / 8f).pad(10f).row();
        rightTable.add(createRoomBtn).row();
        rightTable.add(joinRoomBtn).row();
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
