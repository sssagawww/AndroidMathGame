package com.mygdx.game.db;

import static com.orm.SugarRecord.findById;

import com.mygdx.game.paint.Figures.Figure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SugarDb implements DbWrapper {
    @Override
    public void saveFigure(Figure figure) {
        SugarFigure sugarFigure = new SugarFigure(figure);
        sugarFigure.save();
    }

    @Override
    public ArrayList<Figure> getFigures() {
        List<SugarFigure> list = SugarFigure.listAll(SugarFigure.class);
        /*SugarFigure figure1 = findById(SugarFigure.class, 1);
        SugarFigure figure2 = findById(SugarFigure.class, 2);
        List<SugarFigure> list = new ArrayList<>(Arrays.asList(figure1,figure2));*/
        ArrayList<Figure> figures = new ArrayList<>();
        for (SugarFigure sf : list) {
            figures.add(new Figure(sf.getName(), sf.getFigureType(), sf.getPoints(), sf.getHints()));
        }

        return figures;
    }
}
