package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.battle.STAT;
import com.mygdx.game.battle.examples.Example;
import com.mygdx.game.battle.examples.ExampleDatabase;
import com.mygdx.game.battle.steps.STEP_BOOLEAN;
import com.mygdx.game.battle.steps.Step;
import com.mygdx.game.battle.steps.StepDatabase;
import com.mygdx.game.battle.steps.StepsDetails;

import java.util.HashMap;
import java.util.Map;

public class BattleEntity {
    private String name;
    private int level;
    private Map<STAT, Integer> stats;
    private int currentHP;
    private Step[] steps = new Step[10];
    private Example[] examples = new Example[6];
    private Texture tex;

    public BattleEntity(String name, Texture tex){
        this.name = name;
        this.tex = tex;
        this.level = 5; //?

        stats = new HashMap<STAT, Integer>();
        for (STAT stat : STAT.values()) {
            stats.put(stat, 15);
        }
        stats.put(STAT.HP, 10);
        currentHP = stats.get(STAT.HP);
    }

    public static BattleEntity generateEntity(String name, Texture tex, StepDatabase stepDatabase, ExampleDatabase exampleDatabase) {
        BattleEntity entity = new BattleEntity(name, tex);

        for (int i = 0; i < 10; i++){
            entity.setSteps(i, stepDatabase.getSteps(i));
        }

        /*entity.setSteps(0, stepDatabase.getSteps(0));
        entity.setSteps(1, stepDatabase.getSteps(1));
        entity.setSteps(2, stepDatabase.getSteps(2));
        entity.setSteps(3, stepDatabase.getSteps(3));*/

        entity.setExamples(1, exampleDatabase.getExample(0)); //setExamples начинается с 1, а должен с 0 <-- fix?
        entity.setExamples(2, exampleDatabase.getExample(1));
        entity.setExamples(3, exampleDatabase.getExample(2));
        entity.setExamples(4, exampleDatabase.getExample(3));
        entity.setExamples(5, exampleDatabase.getExample(4));

        return entity;
    }

    public void applyDamage(int amount){
        currentHP -= amount;
        if (currentHP < 0) {
            currentHP = 0;
        }
    }

    public boolean isDefeated(){
        return currentHP == 0;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public Texture getTex() {
        return tex;
    }

    public int getStats(STAT stat) {
        return stats.get(stat);
    }

    public void setStats(STAT stat, int value) {
        stats.put(stat, value);
    }

    public int getCurrentHitpoints() {
        return currentHP;
    }

    public void setCurrentHitpoints(int currentHitpoints) {
        this.currentHP = currentHitpoints;
    }

    public Step getSteps(int index) {
        return steps[index].clone();
    }

    public void setSteps(int index, Step step) {
        steps[index] = step;
    }

    public StepsDetails getDetails(int index) {
        return steps[index].getStepDetails();
    }

    public STEP_BOOLEAN getBoolean(int index) {
        return steps[index].getStepBoolean();
    }

    public void setStepBoolean(int index, STEP_BOOLEAN stepBoolean){
        steps[index].setStepBoolean(stepBoolean);
    }

    public Example getExamples(int index) {
        return examples[index].clone();
    }

    public void setExamples(int index, Example example) {
        examples[index] = example;
    }
}
