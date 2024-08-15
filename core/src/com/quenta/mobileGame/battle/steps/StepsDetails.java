package com.quenta.mobileGame.battle.steps;

public class StepsDetails {
    STEP_TYPE type;
    STEP_BOOLEAN stepBoolean;
    private int damage;
    private String name;

    public StepsDetails(STEP_TYPE type, int damage, String name, STEP_BOOLEAN stepBoolean){
        this.type = type;
        this.damage = damage;
        this.name = name;
        this.stepBoolean = stepBoolean;
    }

    public STEP_TYPE getType() {
        return type;
    }

    public int getDamage() {
        return damage;
    }

    public String getName() {
        return name;
    }

    public STEP_BOOLEAN getStepBoolean() {
        return stepBoolean;
    }

    public void setStepBoolean(STEP_BOOLEAN stepBoolean) {
        this.stepBoolean = stepBoolean;
    }
}
