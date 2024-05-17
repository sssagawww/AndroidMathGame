package com.mygdx.game.db;

import java.util.ArrayList;
import java.util.HashMap;

public class Progress {
    private boolean swordImage;
    private boolean ringImage;
    private boolean amuletImage;
    private int artefactsCount;
    private ArrayList<Integer> achievements;
    private HashMap<String, Integer>  items;

    public Progress() {
    }

    public Progress(boolean ringImage, boolean swordImage, boolean amuletImage, int artefactsCount, ArrayList<Integer> achievements, HashMap<String, Integer> items) {
        this.ringImage = ringImage;
        this.swordImage = swordImage;
        this.amuletImage = amuletImage;
        this.artefactsCount = artefactsCount;
        this.achievements = achievements;
        this.items = items;
    }

    public boolean isSwordImageVisible() {
        return swordImage;
    }

    public void setSwordImage(boolean swordImage) {
        this.swordImage = swordImage;
    }

    public boolean isRingImageVisible() {
        return ringImage;
    }

    public void setRingImage(boolean ringImage) {
        this.ringImage = ringImage;
    }

    public boolean isAmuletImageVisible() {
        return amuletImage;
    }

    public void setAmuletImage(boolean amuletImage) {
        this.amuletImage = amuletImage;
    }

    public int getArtefactsCount() {
        return artefactsCount;
    }

    public void setArtefactsCount(int artefactsCount) {
        this.artefactsCount = artefactsCount;
    }

    public ArrayList<Integer> getAchievements() {
        return achievements;
    }

    public void setAchievements(ArrayList<Integer> achievements) {
        this.achievements = achievements;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public void setItems(HashMap<String, Integer>  items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Progress{" +
                "swordImage=" + swordImage +
                ", ringImage=" + ringImage +
                ", amuletImage=" + amuletImage +
                ", artefactsCount=" + artefactsCount +
                ", achievements=" + achievements +
                ", items=" + items +
                '}';
    }
}
