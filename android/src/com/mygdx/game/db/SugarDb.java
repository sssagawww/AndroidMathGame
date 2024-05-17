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

    @Override
    public void saveProgress(Progress progress) {
        SugarProgress sugarProgress = new SugarProgress(progress);
        sugarProgress.save();
    }

    @Override
    public ArrayList<Progress> getProgress() {
        List<SugarProgress> list = SugarProgress.listAll(SugarProgress.class);
        ArrayList<Progress> progresses = new ArrayList<>();
        for (SugarProgress sf : list) {
            progresses.add(new Progress(sf.isRingImage(), sf.isSwordImage(), sf.isAmuletImage(), sf.getArtefactsCount(), sf.getAchievements(), sf.getItems()));
        }
        return progresses;
    }

    @Override
    public void clearAll(){
        SugarProgress.deleteAll(SugarProgress.class);
    }
}
