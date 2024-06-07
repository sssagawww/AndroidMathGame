package com.mygdx.game.battle.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StepDatabase {
    private List<Step> steps = new ArrayList<Step>();
    private HashMap<String, Integer> mappings = new HashMap<String, Integer>();

    public StepDatabase() {
        initializeSteps();
    }

    public void initializeSteps() {
        steps.clear();
        mappings.clear();
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "56", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "23", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "69", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "59", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "4", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "45", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "121", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "89", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "28", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "89", STEP_BOOLEAN.WRONG)));
    }

    public void initializeSteps2() {
        steps.clear();
        mappings.clear();
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "43", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "40", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "6", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "36", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "14", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "55", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "35", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "24", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "28", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "89", STEP_BOOLEAN.WRONG)));
    }

    public void initializeSteps3() {
        steps.clear();
        mappings.clear();
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "70", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "60", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "117", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "63", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "4", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "32", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "42", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "64", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "75", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "56", STEP_BOOLEAN.WRONG)));
    }

    public void addStep(Step step) {
        steps.add(step);
        mappings.put(step.getName(), steps.size() - 1);
    }

    public Step getSteps(int index) {
        return steps.get(index).clone();
    }
}
