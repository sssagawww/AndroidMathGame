package com.mygdx.game.paint.Figures;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.db.DbWrapper;
import com.mygdx.game.paint.PixelPoint;

import java.util.ArrayList;
import java.util.Arrays;

public class FiguresDatabase {
    private ArrayList<Figure> figures = new ArrayList<>();
    private MyGdxGame game;
    private DbWrapper dbWrapper;
    private int curFigure;

    public enum FIGURES_TYPES {
        SQUARE,
        CIRCLE,
        STAR,
        TRIANGLE,
        RHOMBUS
    }

    public FiguresDatabase(MyGdxGame game) {
        this.game = game;
        dbWrapper = game.getDbWrapper();
        curFigure = 0;
        initializeFigures();
        //figures = dbWrapper.getFigures(); // загружает из дб, которая пока доступна только на 1 устройстве, не переносится
    }

    private void saveFigures(){
        dbWrapper.saveFigure(figures.get(1));
        dbWrapper.saveFigure(figures.get(2));
        dbWrapper.saveFigure(figures.get(4));
        dbWrapper.saveFigure(figures.get(6));
        dbWrapper.saveFigure(figures.get(8));
    }

    public void addFigure(Figure figure) {
        game.getDbWrapper().saveFigure(figure);
    }

    //???
    private void initializeFigures() {
        addArrayFigure(new Figure("Квадрат",FIGURES_TYPES.SQUARE, new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210),
                new PixelPoint(110, 220),
                new PixelPoint(110, 230),
                new PixelPoint(120, 210),
                new PixelPoint(130, 210),
                new PixelPoint(140, 210),
                new PixelPoint(140, 220),
                new PixelPoint(140, 230),
                new PixelPoint(130, 240),
                new PixelPoint(120, 240),
                new PixelPoint(140, 240),
                new PixelPoint(110, 240)
        )), new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210),
                new PixelPoint(110, 240),
                new PixelPoint(140, 210),
                new PixelPoint(140, 240)
        ))));

        addArrayFigure(new Figure("Круг", FIGURES_TYPES.CIRCLE, new ArrayList<>(Arrays.asList(
                new PixelPoint(310, 310),
                new PixelPoint(320, 320),
                new PixelPoint(330, 330),
                new PixelPoint(340, 330),
                new PixelPoint(350, 330),
                new PixelPoint(360, 330),
                new PixelPoint(370, 320),
                new PixelPoint(380, 310),
                new PixelPoint(380, 300),
                new PixelPoint(380, 290),
                new PixelPoint(380, 280),
                new PixelPoint(370, 270),
                new PixelPoint(360, 260),
                new PixelPoint(350, 260),
                new PixelPoint(340, 260),
                new PixelPoint(330, 260),
                new PixelPoint(320, 270),
                new PixelPoint(310, 280),
                new PixelPoint(310, 290),
                new PixelPoint(310, 300)

        )), new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        ))));

        addArrayFigure(new Figure("Звезда",FIGURES_TYPES.STAR, new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        )), new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        ))));

        addArrayFigure(new Figure("Треугольник",FIGURES_TYPES.TRIANGLE, new ArrayList<>(
                Arrays.asList(new PixelPoint(110, 210)
                )), new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        ))));

        addArrayFigure(new Figure("Ромб",FIGURES_TYPES.RHOMBUS, new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        )), new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        ))));
    }

    public void addArrayFigure(Figure figure) {
        figures.add(figure);
    }

    public Figure getFigure(int index) {
        return figures.get(index);
    }

    public int getFiguresCount() {
        return figures.size();
    }

    public int getCurFigure() {
        return curFigure;
    }

    public int nextFigure() {
        return ++curFigure;
    }

    public void resetFigureNum() {
        curFigure = 0;
    }
}
