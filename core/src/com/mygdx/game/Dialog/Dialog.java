package com.mygdx.game.Dialog;

import java.util.HashMap;
import java.util.Map;

public class Dialog {
    private Map<Integer, DialogNode> nodes = new HashMap<Integer, DialogNode>();

    public DialogNode getNode(int id){
        return nodes.get(id);
    }
    public void addNode(DialogNode node){
        this.nodes.put(node.getId(), node);
    }
    public int getStart(){
        return 0;
    }
}
