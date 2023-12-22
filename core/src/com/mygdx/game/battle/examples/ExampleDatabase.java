package com.mygdx.game.battle.examples;

import com.mygdx.game.battle.steps.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleDatabase {
    private List<Example> examples = new ArrayList<Example>();
    private HashMap<String, Integer> mappings = new HashMap<String, Integer>();
    private Example currentExample;

    public ExampleDatabase(){
        initializeExamples();
    }
    private void initializeExamples() {
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_LIST.EXAMPLE_1, "     59-36=       ", 1)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_LIST.EXAMPLE_2, "     236:4=       ", 2)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_LIST.EXAMPLE_3, "     23*3=        ", 3)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_LIST.EXAMPLE_4, "     75+46=       ", 4)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_LIST.EXAMPLE_5, "     32*3-7=      ", 5)));
    }
    public void addExample(Example example){
        examples.add(example);
        mappings.put(example.getName(), examples.size()-1);
    }
    public Example getExample(int index) {
        return examples.get(index).clone();
    }

    /*public ExampleDetails getNextExample(int index){
        Example nextExample = examples.get(index);
        return nextExample;
    }*/
}
