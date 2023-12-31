package com.mygdx.game.Dialog;

import java.util.ArrayList;

public class DialogNode {
    private ArrayList<Integer> pointers = new ArrayList<Integer>();
    private ArrayList<String> labels = new ArrayList<String>();

    private String text;
    private int id;

    private NODE_TYPE type;

    public enum NODE_TYPE {
        MULTIPLE_CHOICE,
        LINEAR,
        END;
    }
    public DialogNode(String text, int id){
        this.text = text;
        this.id = id;
        type = NODE_TYPE.END;
    }
    public void addChoice(String option, int nodeId){
        if (type == NODE_TYPE.LINEAR) {
            pointers.clear();
        }
        labels.add(option);
        pointers.add(nodeId);
        type = NODE_TYPE.MULTIPLE_CHOICE;
    }
    public void makeLinear(int nodeId){
        pointers.clear();
        labels.clear();
        pointers.add(nodeId);
        type = NODE_TYPE.LINEAR;
    }

    public ArrayList<Integer> getPointers() {
        return pointers;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }

    public NODE_TYPE getType() {
        return type;
    }
}
