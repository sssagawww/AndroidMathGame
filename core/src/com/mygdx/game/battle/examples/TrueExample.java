package com.mygdx.game.battle.examples;

public class TrueExample extends Example{

    public TrueExample(ExampleDetails details) {
        super(details);
    }

    @Override
    public Example clone() {
        return new TrueExample(details);
    }
}
