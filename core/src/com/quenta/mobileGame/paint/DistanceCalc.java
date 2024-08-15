package com.quenta.mobileGame.paint;

import com.quenta.mobileGame.states.PaintState;

import java.util.ArrayList;
import java.util.Collections;

public class DistanceCalc {
    private double maxLetDist = 0.25; //думаю, надо ставить 15-25 примерно, еще надо посмотреть, как лучше работать будет
    private PaintState paintState;
    private double maxDist;

    public DistanceCalc(PaintState paintState) {
        this.paintState = paintState;
    }

    public boolean isSame() {
        ArrayList<PixelPoint> points = resize(paintState.getPoints());
        ArrayList<PixelPoint> points1 = resize(paintState.getFigurePoints());

        maxDist = Double.MIN_VALUE;

        for (int i = 0; i < points.size(); i++) {
            PixelPoint point = points.get(i);
            double minDist = Double.MAX_VALUE;
            for (int j = 0; j < points1.size(); j++) {
                PixelPoint point1 = points1.get(j);
                minDist = Math.min(minDist, getDistance(point, point1));
            }
            maxDist = Math.max(maxDist, minDist);
        }
        return maxDist <= maxLetDist;
    }

    public double getAccuracy(){
        return maxDist;
    }

    private double getDistance(PixelPoint point, PixelPoint point1) {
        return Math.sqrt(Math.pow(point.getX() - point1.getX(), 2) + Math.pow(point.getY() - point1.getY(), 2));
    }

    private ArrayList resize(ArrayList<PixelPoint> points) {
        ArrayList<Float> pointsX = new ArrayList<>();
        ArrayList<Float> pointsY = new ArrayList<>();

        points.forEach(point -> {
            pointsX.add((float) point.getX());
            pointsY.add((float) point.getY());
        });

        Collections.sort(pointsX);
        Collections.sort(pointsY);

        float minx = pointsX.get(0);
        float miny = pointsY.get(0);

        float maxx = pointsX.get(pointsX.size() - 1);
        float maxy = pointsY.get(pointsY.size() - 1);

        float S = Math.max(maxx - minx, maxy - miny);

        ArrayList<Float> tPointsX = (ArrayList<Float>) pointsX.clone();
        ArrayList<Float> tPointsY = (ArrayList<Float>) pointsY.clone();

        pointsX.clear();
        pointsY.clear();

        tPointsX.forEach(aFloat -> {
            pointsX.add((aFloat - minx) / S);
        });

        tPointsY.forEach(aFloat -> {
            pointsY.add((aFloat - miny) / S);
        });

        ArrayList<PixelPoint> newPoints = new ArrayList<>();
        //points.clear(); //делает resize исходных точкек, не очень критично, но если нужно отображать их, то рисует их в углу

        for (int i = 0; i < pointsX.size(); i++) {
            //points.add(new Point(Integer.parseInt(String.valueOf(pointsX.get(i))), Integer.parseInt(String.valueOf(pointsY.get(i)))));
            newPoints.add(new PixelPoint(pointsX.get(i), pointsY.get(i)));
        }
        return newPoints;
    }

}
