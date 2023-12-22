package com.mygdx.game.battle.examples;

import com.mygdx.game.battle.steps.STEP_TYPE;
import com.mygdx.game.battle.steps.Step;
import com.mygdx.game.battle.steps.StepsDetails;

public abstract class Example {
    protected ExampleDetails details;

    public Example(ExampleDetails details){
        this.details = details;
    }

    public String getName() {
        return details.getName();
    }

    public EXAMPLE_LIST getList(){
        return details.getExampleList();
    }

    public void setList(EXAMPLE_LIST exampleList) {
        details.setExampleList(exampleList);
    }

    public ExampleDetails getExampleDetails() {
        return details;
    }

    public abstract Example clone();
}
