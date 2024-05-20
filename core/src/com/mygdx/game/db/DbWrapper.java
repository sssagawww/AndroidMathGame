package com.mygdx.game.db;

import com.mygdx.game.paint.Figures.Figure;

import java.util.ArrayList;

public interface DbWrapper {
    public void saveFigure(Figure figure);
    public ArrayList<Figure> getFigures();
    public void saveProgress(Progress progress);
    public ArrayList<Progress> getProgress();
    public void clearAll();
}
