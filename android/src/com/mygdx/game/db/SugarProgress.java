package com.mygdx.game.db;

import com.google.gson.reflect.TypeToken;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class SugarProgress extends SugarRecord {
    private boolean swordImage;
    private boolean ringImage;
    private boolean amuletImage;
    private int artefactsCount;
    private String achievements;
    private String items;
    @Ignore
    private Type achievementsType =  new TypeToken<ArrayList<Integer>>(){}.getType();
    @Ignore
    private Type itemsType =  new TypeToken<HashMap<String, Integer>>(){}.getType();

    public SugarProgress() {
    }

    public SugarProgress(Progress progress) {
        this.swordImage = progress.isSwordImageVisible();
        this.ringImage = progress.isRingImageVisible();
        this.amuletImage = progress.isAmuletImageVisible();
        this.artefactsCount = progress.getArtefactsCount();
        this.achievements = getJsonAchievements(progress.getAchievements());
        this.items = getJsonItems(progress.getItems());
    }

    public boolean isSwordImage() {
        return swordImage;
    }

    public boolean isRingImage() {
        return ringImage;
    }

    public boolean isAmuletImage() {
        return amuletImage;
    }

    public int getArtefactsCount() {
        return artefactsCount;
    }

    public ArrayList<Integer> getAchievements() {
        return SugarDb.getGson().fromJson(achievements, achievementsType);
    }

    public HashMap<String, Integer>  getItems() {
        return SugarDb.getGson().fromJson(items, itemsType);
    }

    public String getJsonAchievements(ArrayList<Integer> list) {
        return SugarDb.getGson().toJson(list, achievementsType);
    }

    public String getJsonItems(HashMap<String, Integer> list) {
        return SugarDb.getGson().toJson(list, itemsType);
    }
}
