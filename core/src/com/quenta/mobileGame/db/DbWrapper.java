package com.quenta.mobileGame.db;

import com.quenta.mobileGame.paint.Figures.Figure;

import java.util.ArrayList;

public interface DbWrapper {
    public void saveFigure(Figure figure);
    public ArrayList<Figure> getFigures();
    public void saveProgress(Progress progress);
    public ArrayList<Progress> getProgress();
    public void clearAll();
}
