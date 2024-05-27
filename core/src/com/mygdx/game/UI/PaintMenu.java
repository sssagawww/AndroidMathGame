package com.mygdx.game.UI;

import static com.mygdx.game.MyGdxGame.*;
import static com.mygdx.game.UI.BtnBox.STATES.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.paint.Figures.FiguresDatabase;
import com.mygdx.game.states.PaintState;

public class PaintMenu extends Table {
    private MyGdxGame game;
    private Table uiTable;
    private BtnBox btnBox;
    private Label.LabelStyle resultStyle;
    private  Label.LabelStyle lstyle;
    private Label timerLabel;
    private Label figureLabel;
    private Label accuracyLabel;
    private Image figureImage;
    private float time;
    private int period;
    private FiguresDatabase figuresDatabase;

    public PaintMenu(Skin skin, MyGdxGame game) {
        super(skin);
        this.game = game;
        figuresDatabase = game.getFiguresDatabase();
        figuresDatabase.resetFigureNum();
        time = 0;
        period = 60;

        uiTable = new Table();
        this.add(uiTable).width(V_WIDTH / 3.5f).height(V_HEIGHT);
        this.setBackground("menuBtn_up");
        //uiTable.setDebug(true);

        BitmapFont font = new BitmapFont(Gdx.files.internal("mcRus.fnt"));
        lstyle = new Label.LabelStyle(font, Color.BLACK);
        lstyle.background = getSkin().getDrawable("borders");

        Label textLabel = new Label("\n", lstyle);
        textLabel.setText("Нарисуй эту\nфигуру:");
        textLabel.setAlignment(Align.center);
        uiTable.add(textLabel).align(Align.bottom).width(getPrefWidth() - 4).row();

        figureImage = new Image(new Texture("controller/square.png"));
        //uiTable.add(image).align(Align.top).width(200f).height(200f).row();

        Table imageTable = new Table(getSkin());
        imageTable.add(figureImage).width(200f).height(200f);
        imageTable.setBackground("borders");
        uiTable.add(imageTable).padTop(10f).padBottom(10f).row();

        figureLabel = new Label("\n", lstyle);
        figureLabel.setText(figuresDatabase.getFigure(figuresDatabase.getCurFigure()).getName());
        figureLabel.setAlignment(Align.center);
        uiTable.add(figureLabel).width(getPrefWidth() - 4).row();

        resultStyle = new Label.LabelStyle(font, Color.BLACK);
        timerLabel = new Label("\n", resultStyle);
        timerLabel.setText(time + "");
        timerLabel.setAlignment(Align.center);

        Table resultTable = new Table(getSkin());
        resultTable.add(timerLabel).width(150f).height(150f);
        resultTable.setBackground("borders");
        uiTable.add(resultTable).padBottom(20f).padTop(20f).row();

        initBtns();

        accuracyLabel = new Label("\n", lstyle);
        accuracyLabel.setText("Точность: ");
        accuracyLabel.setAlignment(Align.center);
        uiTable.add(accuracyLabel).align(Align.bottom).width(getPrefWidth() - 4).row();
    }

    private void setNextFigure(String name){
        figureLabel.setText(name);
        figureImage.setDrawable(this.getSkin(), name);
    }

    public void setAccuracy(double value){
        accuracyLabel.setText("Точность: " + String.format("%.2f",1-value));
    }

    private void initBtns() {
        btnBox = new BtnBox(getSkin());
        btnBox.addBtn("Стереть", CLEAR);
        uiTable.add(btnBox).align(Align.bottom).row();
    }

    //isSame() сетнул ok или wrong -> setResultImage() поставил нужную картинку -> checkProgress(), который обновляется в update(),
    //отсчитывает несколько секунд, чтобы показать картинку, и запускает таймер заново
    public void checkProgress(float dt) {
        if (btnBox.getState() == NON) {
            time += dt;
            timerLabel.setText("00:" + (period - Math.round(time)) + "");
            if (time > period) {
                time = 0;
                btnBox.setState(WRONG);
            }
        } else if(btnBox.getState() == OK || btnBox.getState() == WRONG){
            time += dt;
            if (time > 3f) {
                if(btnBox.getState() == OK || PaintState.isOnline()){
                    setNextFigure(figuresDatabase.getFigure(figuresDatabase.nextFigure() % figuresDatabase.getFiguresCount()).getName());
                    setAccuracy(1);
                }
                btnBox.setState(NON);
                resultStyle.background = null;
                time = 0;
            }
        }
        if(figuresDatabase.getCurFigure() == figuresDatabase.getFiguresCount()){
            figuresDatabase.resetFigureNum();
            btnBox.setState(DONE);
        }
    }

    public void setResultImage() {
        if (resultStyle.background == null) {
            time = 0;
            timerLabel.setText("");
            if (btnBox.getState() == OK)
                resultStyle.background = getSkin().getDrawable("ok");
            else if (btnBox.getState() == WRONG)
                resultStyle.background = getSkin().getDrawable("wrong");
        }
    }

    public BtnBox getBtnBox() {
        return btnBox;
    }
}
