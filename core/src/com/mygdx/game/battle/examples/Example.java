package com.mygdx.game.battle.examples;

public abstract class Example {
    protected ExampleDetails details;

    public Example(ExampleDetails details){
        this.details = details;
    }

    public String getName() {
        return details.getName();
    }

    public EXAMPLE_NUM getList(){
        return details.getExampleList();
    }

    public void setList(EXAMPLE_NUM exampleList) {
        details.setExampleList(exampleList);
    }

    public ExampleDetails getExampleDetails() {
        return details;
    }

    public abstract Example clone();
}
