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
import java.util.HashSet;
import java.util.Map;

public class BattleEntity {
    private final String name;
    private final int level;
    private final Map<STAT, Integer> stats;
    private final HashMap<String, String> map;
    private int currentHP;
    private final ArrayList<Step> steps = new ArrayList<>();
    private final ArrayList<Example> examples = new ArrayList<>();
    private final int count = 14;

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

        HashSet<Integer> exampleNums = new HashSet<>();
        for (int i = 0; i < entity.count; i++) {
            int r = (int) Math.floor(Math.random() * exampleDatabase.getExamples().size());
            if (exampleNums.contains(r)) {
                while (exampleNums.contains(r)) {
                    r = (int) Math.floor(Math.random() * exampleDatabase.getExamples().size());
                }
                exampleNums.add(r);
            } else {
                exampleNums.add(r);
            }
            entity.addExample(exampleDatabase.getExample(r));
            entity.addStep(stepDatabase.getStep(r));
        }

        for (int i = entity.count; i < stepDatabase.getSteps().size(); i++) {
            if (!entity.getStepsNames().contains(stepDatabase.getStep(i).getName())) {
                entity.addStep(stepDatabase.getStep(i));
            }
        }

        entity.createMap(stepDatabase, exampleDatabase);

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

    public int getCurrentHitpoints() {
        return currentHP;
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

    public ArrayList<String> getStepsNames() {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            arr.add(steps.get(i).getName());
        }
        return arr;
    }

    public HashMap<String, String> getMap() {
        return map;
    }
}
