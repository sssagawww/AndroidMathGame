package com.mygdx.game.paint.Figures;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.paint.PixelPoint;

import java.util.ArrayList;
import java.util.Arrays;

public class FiguresDatabase {
    private ArrayList<Figure> figures = new ArrayList<>();
    private MyGdxGame game;

    public enum FIGURES_TYPES {
        SQUARE,
        CIRCLE,
        STAR,
        TRIANGLE,
        RHOMBUS
    }

    public FiguresDatabase(MyGdxGame game) {
        this.game = game;
        //initializeFigures();
        figures = game.getDbWrapper().getFigures();
    }

    public void addFigure(Figure figure){
        game.getDbWrapper().saveFigure(figure);
    }

    //???
    private void initializeFigures() {
        addFigure(new Figure("Квадрат",FIGURES_TYPES.SQUARE, new ArrayList<>(Arrays.asList(
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

        addFigure(new Figure("Круг",FIGURES_TYPES.CIRCLE, new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        )), new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        ))));

        addFigure(new Figure("Звезда",FIGURES_TYPES.STAR, new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        )), new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        ))));

        addFigure(new Figure("Треугольник",FIGURES_TYPES.TRIANGLE, new ArrayList<>(
                Arrays.asList(new PixelPoint(110, 210)
                )), new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        ))));

        addFigure(new Figure("Ромб",FIGURES_TYPES.RHOMBUS, new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        )), new ArrayList<>(Arrays.asList(
                new PixelPoint(110, 210)
        ))));
    }

    /*public void addFigure(Figure figure) {
        figures.add(figure);
    }*/

    public Figure getFigure(int index){
        return figures.get(index);
    }

    public int getFiguresCount(){
        return figures.size();
    }
}
