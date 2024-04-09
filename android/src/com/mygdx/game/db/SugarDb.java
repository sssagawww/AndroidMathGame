package com.mygdx.game.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mygdx.game.paint.Figures.Figure;

import java.util.ArrayList;
import java.util.List;

public class SugarDb implements DbWrapper {
    private GsonBuilder builder;
    private static Gson gson;

    public SugarDb() {
        builder = new GsonBuilder();
        gson = builder.create();
        //SugarFigure.deleteAll(SugarFigure.class);
    }

    @Override
    public void saveFigure(Figure figure) {
        SugarFigure sugarFigure = new SugarFigure(figure);
        sugarFigure.save();
    }

    @Override
    public ArrayList<Figure> getFigures() {
        List<SugarFigure> list = SugarFigure.listAll(SugarFigure.class);
        ArrayList<Figure> figures = new ArrayList<>();
        for (SugarFigure sf : list) {
            figures.add(new Figure(sf.getName(), sf.getFigureType(), sf.getPoints(), sf.getHints()));
        }
        return figures;
    }

    public static Gson getGson() {
        return gson;
    }
}
