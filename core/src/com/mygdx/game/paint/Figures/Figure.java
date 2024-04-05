package com.mygdx.game.paint.Figures;

import com.mygdx.game.paint.PixelPoint;
import java.util.ArrayList;

public class Figure {
    private String name;
    private FiguresDatabase.FIGURES_TYPES figureType;
    private ArrayList<PixelPoint> points;
    private ArrayList<PixelPoint> hints;

    public Figure(String name ,FiguresDatabase.FIGURES_TYPES figureName, ArrayList<PixelPoint> points, ArrayList<PixelPoint> hints) {
        this.name = name;
        this.figureType = figureName;
        this.points = points;
        this.hints = hints;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public FiguresDatabase.FIGURES_TYPES getFigureType() {
        return figureType;
    }

    public void setFigureType(FiguresDatabase.FIGURES_TYPES figureType) {
        this.figureType = figureType;
    }
}
