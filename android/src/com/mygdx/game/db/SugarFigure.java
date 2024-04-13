package com.mygdx.game.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mygdx.game.paint.Figures.Figure;
import com.mygdx.game.paint.Figures.FiguresDatabase;
import com.mygdx.game.paint.PixelPoint;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SugarFigure extends SugarRecord {
    private String name;
    private FiguresDatabase.FIGURES_TYPES figureType;
    private String points;
    private String hints;
    @Ignore
    private Type type =  new TypeToken<ArrayList<PixelPoint>>(){}.getType();
    public SugarFigure() {
    }

    public SugarFigure(Figure figure) {
        this.name = figure.getName();
        this.figureType = figure.getFigureType();
        this.points = getJsonPoints(figure.getPoints());
        this.hints = getJsonHints(figure.getHints());
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
        return SugarDb.getGson().fromJson(this.points, type);
    }

    public ArrayList<PixelPoint> getHints() {
        return SugarDb.getGson().fromJson(hints, type);
    }

    public String getJsonPoints(ArrayList<PixelPoint> list) {
        return SugarDb.getGson().toJson(list, type);
    }

    public String getJsonHints(ArrayList<PixelPoint> list) {
        return SugarDb.getGson().toJson(list, type);
    }
}
