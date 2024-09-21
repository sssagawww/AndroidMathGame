package com.quenta.mobileGame.battle.examples;

import java.util.ArrayList;
import java.util.List;

public class ExampleDatabase {
    private List<Example> examples = new ArrayList<>();

    public ExampleDatabase() {}

    public void initializeAllExamples() {
        examples.clear();
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_1, "     59-36=       ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_2, "     236:4=       ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_3, "     23*3=        ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_4, "     75+46=       ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_5, "     32*3-7=      ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_6, "     360:9=       ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_7, "     6*6=         ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_8, "     180:(6*5)=   ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_9, "     81-46=       ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_10, "     2*3*4=       ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_11, "     42+18=       ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_12, "     75-36+24=    ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_13, "     45*3-18=     ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_14, "     14*3=        ")));
        addExample(new TrueExample(new ExampleDetails(EXAMPLE_NUM.EXAMPLE_15, "     256:4=       ")));
    }

    public void addExample(Example example) {
        examples.add(example);
    }

    public Example getExample(int index) {
        return examples.get(index).clone();
    }

    public List<Example> getExamples() {
        return examples;
    }
}
