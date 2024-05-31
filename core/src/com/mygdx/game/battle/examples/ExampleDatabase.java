package com.mygdx.game.battle.examples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExampleDatabase {
    private List<Example> examples = new ArrayList<Example>();
    private HashMap<String, Integer> mappings = new HashMap<String, Integer>();
    private Example currentExample;

    public ExampleDatabase() {
        initializeExamples();
    }

    public void initializeExamples() {
        examples.clear();
        mappings.clear();
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_1, "     59-36=       ", 1)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_2, "     236:4=       ", 2)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_3, "     23*3=        ", 3)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_4, "     75+46=       ", 4)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_5, "     32*3-7=      ", 5)));
    }

    public void initializeExamples2() {
        examples.clear();
        mappings.clear();
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_1, "     360:9=       ", 1)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_2, "     6*6=          ", 2)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_3, "     180:(6*5)=   ", 3)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_4, "     81-46=       ", 4)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_5, "     2*3*4=       ", 5)));
    }

    public void initializeExamples3() {
        examples.clear();
        mappings.clear();
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_1, "     42+18=       ", 1)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_2, "     75-36+24=    ", 2)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_3, "     45*3-18=     ", 3)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_4, "     14*3=        ", 4)));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_5, "     256:4=       ", 5)));
    }

    public void addExample(Example example) {
        examples.add(example);
        mappings.put(example.getName(), examples.size() - 1);
    }

    public Example getExample(int index) {
        return examples.get(index).clone();
    }

    /*public ExampleDetails getNextExample(int index){
        Example nextExample = examples.get(index);
        return nextExample;
    }*/
}
