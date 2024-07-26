package com.mygdx.game.battle.steps;

import java.util.ArrayList;
import java.util.List;

public class StepDatabase {
    private List<Step> steps = new ArrayList<Step>();

    public StepDatabase() {
        initializeSteps();
    }

    public void initializeSteps() {
        steps.clear();
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

    public void initializeAllSteps() {
        steps.clear();
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "23", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "59", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "69", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "121", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "89", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "40", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "36", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "6", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "35", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "24", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "60", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "63", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "117", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "42", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "64", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "48", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "34", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "24", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "28", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "83", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "71", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "68", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "127", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "63", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "4", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "32", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "42", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "68", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.DEFAULT, 10, "75", STEP_BOOLEAN.WRONG)));
        addStep(new DamageStep(new StepsDetails(STEP_TYPE.SPECIAL, 15, "56", STEP_BOOLEAN.WRONG)));
    }

    public void addStep(Step step) {
        steps.add(step);
    }

    public Step getStep(int index) {
        return steps.get(index).clone();
    }

    public List<Step> getSteps() {
        return steps;
    }
}
