package com.mygdx.game.db;

import com.mygdx.game.paint.Figures.Figure;
import com.mygdx.game.paint.Figures.FiguresDatabase;
import com.mygdx.game.paint.PixelPoint;
import com.orm.SugarRecord;

import java.util.ArrayList;

public class SugarFigure extends SugarRecord {
    private String name;
    private FiguresDatabase.FIGURES_TYPES figureType;
    private ArrayList<PixelPoint> points;
    private ArrayList<PixelPoint> hints;

    public SugarFigure() {
    }

    public SugarFigure(Figure figure) {
        this.name = figure.getName();
        this.figureType = figure.getFigureType();
        this.points = figure.getPoints();
        this.hints = figure.getHints();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FiguresDatabase.FIGURES_TYPES getFigureType() {
        return figureType;
    }

    public void setFigureType(FiguresDatabase.FIGURES_TYPES figureType) {
        this.figureType = figureType;
    }

    public ArrayList<PixelPoint> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<PixelPoint> points) {
        this.points = points;
    }

    public ArrayList<PixelPoint> getHints() {
        return hints;
    }

    public void setHints(ArrayList<PixelPoint> hints) {
        this.hints = hints;
    }
}
