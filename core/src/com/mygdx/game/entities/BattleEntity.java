package com.mygdx.game.entities;

import static com.mygdx.game.battle.steps.STEP_BOOLEAN.WRONG;

import com.mygdx.game.battle.STAT;
import com.mygdx.game.battle.examples.Example;
import com.mygdx.game.battle.examples.ExampleDatabase;
import com.mygdx.game.battle.steps.STEP_BOOLEAN;
import com.mygdx.game.battle.steps.Step;
import com.mygdx.game.battle.steps.StepDatabase;
import com.mygdx.game.battle.steps.StepsDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BattleEntity {
    private String name;
    private int level;
    private Map<STAT, Integer> stats;
    private HashMap<String, String> map;
    private int currentHP;
    private ArrayList<Step> steps = new ArrayList<>();
    private ArrayList<Example> examples = new ArrayList<>();
    private int count = 8;

    public BattleEntity(String name) {
        this.name = name;
        this.level = 5; //?

        map = new HashMap<>();
        stats = new HashMap<>();
        for (STAT stat : STAT.values()) {
            stats.put(stat, 15);
        }
        stats.put(STAT.HP, 10);
        currentHP = stats.get(STAT.HP);
    }

    public static BattleEntity generateEntity(String name, StepDatabase stepDatabase, ExampleDatabase exampleDatabase) {
        BattleEntity entity = new BattleEntity(name);

        for (int i = 0; i < entity.count; i++) {
            int r = (int) Math.floor(Math.random() * exampleDatabase.getExamples().size());
            if (entity.getExamples().contains(exampleDatabase.getExample(r))) {
                r = (int) Math.floor(Math.random() * exampleDatabase.getExamples().size());
            }
            entity.addExample(exampleDatabase.getExample(r));
            entity.addStep(stepDatabase.getStep(r));
        }

        for (int i = 0; i < stepDatabase.getSteps().size() - entity.count; i++) {
            if (!entity.getSteps().contains(stepDatabase.getStep(i))) {
                entity.addStep(stepDatabase.getStep(i));
            }
        }

        entity.createMap(stepDatabase, exampleDatabase);

        /*entity.setExamples(1, exampleDatabase.getExample(0)); //setExamples начинается с 1, а должен с 0
        entity.setExamples(2, exampleDatabase.getExample(1));
        entity.setExamples(3, exampleDatabase.getExample(2));
        entity.setExamples(4, exampleDatabase.getExample(3));
        entity.setExamples(5, exampleDatabase.getExample(4));*/

        return entity;
    }

    public void applyDamage(int amount) {
        currentHP -= amount;
        if (currentHP < 0) {
            currentHP = 0;
        }
    }

    private void createMap(StepDatabase stepDatabase, ExampleDatabase exampleDatabase) {
        for (int i = 0; i < exampleDatabase.getExamples().size(); i++) {
            map.put(exampleDatabase.getExample(i).getName(), stepDatabase.getStep(i).getName());
        }
    }

    public boolean isDefeated() {
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

    public Step getStep(int index) {
        return steps.get(index);
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public StepsDetails getDetails(int index) {
        return steps.get(index).getStepDetails();
    }

    public STEP_BOOLEAN getStepBoolean(int index) {
        if (index < 0) {
            return WRONG;
        }
        return steps.get(index).getStepBoolean();
    }

    public void setStepBoolean(int index, STEP_BOOLEAN stepBoolean) {
        steps.get(index).setStepBoolean(stepBoolean);
    }

    public Example getExample(int index) {
        return examples.get(index);
    }

    public void addExample(Example example) {
        examples.add(example);
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public ArrayList<Example> getExamples() {
        return examples;
    }

    public HashMap<String, String> getMap() {
        return map;
    }
}
