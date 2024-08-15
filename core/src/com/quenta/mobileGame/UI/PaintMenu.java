package com.quenta.mobileGame.UI;

import static com.quenta.mobileGame.UI.BtnBox.STATES.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.quenta.mobileGame.MyGdxGame;
import com.quenta.mobileGame.paint.Figures.FiguresDatabase;
import com.quenta.mobileGame.states.PaintState;

public class PaintMenu extends Table {
    private final Table uiTable;
    private BtnBox btnBox;
    private final Label.LabelStyle resultStyle;
    private final Label timerLabel;
    private final Label figureLabel;
    private final Label accuracyLabel;
    private final Image figureImage;
    private float time;
    private final int period;
    private final FiguresDatabase figuresDatabase;

    public PaintMenu(Skin skin, MyGdxGame game) {
        super(skin);
        figuresDatabase = game.getFiguresDatabase();
        figuresDatabase.resetFigureNum();
        time = 0;
        period = 60;

        uiTable = new Table();
        this.add(uiTable).width(Gdx.graphics.getWidth() / 3.5f).height(Gdx.graphics.getHeight());
        this.setBackground("menuBtn_up");

        //стиль для текстовых полей с обводкой
        BitmapFont font = getSkin().getFont("font");
        Label.LabelStyle lstyle = new Label.LabelStyle(font, Color.BLACK);
        lstyle.background = getSkin().getDrawable("borders");

        //стиль без обводки
        resultStyle = new Label.LabelStyle(font, Color.BLACK);

        //верхнее текстовое поле
        Label textLabel = new Label("\n", lstyle);
        textLabel.setText("Нарисуй эту\nфигуру:");
        textLabel.setAlignment(Align.center);
        uiTable.add(textLabel).align(Align.bottom).width(getPrefWidth()).row();

        //картинка фигуры
        Table imageTable = new Table(getSkin());
        figureImage = new Image(skin.getDrawable(figuresDatabase.getFigure(0).getName()));
        imageTable.add(figureImage).width(Value.percentWidth(0.4f, uiTable)).height(Value.percentWidth(0.4f, uiTable));
        imageTable.setBackground("borders");
        uiTable.add(imageTable).padTop(10f).padBottom(10f).row();

        //текстовое поле с названием фигуры
        figureLabel = new Label("\n", lstyle);
        figureLabel.setText(figuresDatabase.getFigure(figuresDatabase.getCurFigure()).getName());
        figureLabel.setAlignment(Align.center);
        uiTable.add(figureLabel).width(getPrefWidth()).row();

        //таймер
        timerLabel = new Label("\n", resultStyle);
        timerLabel.setText(time + "");
        timerLabel.setAlignment(Align.center);

        Table resultTable = new Table(getSkin());
        resultTable.add(timerLabel).width(Value.percentWidth(0.3f, uiTable)).height(Value.percentWidth(0.3f, uiTable));
        resultTable.setBackground("borders");
        uiTable.add(resultTable).padBottom(20f).padTop(20f).row();

        //кнопки
        initBtns();
        uiTable.add().padBottom(20f).row();

        //поле точности
        accuracyLabel = new Label("\n", lstyle);
        accuracyLabel.setText("Точность: ");
        accuracyLabel.setAlignment(Align.center);
        uiTable.add(accuracyLabel).align(Align.bottom).width(getPrefWidth()).row();
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
            timerLabel.setText("00:" + (period - Math.round(time)));
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
