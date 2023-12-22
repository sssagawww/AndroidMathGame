package com.mygdx.game.Dialog;

import java.util.List;

public class DialogGo {
    private Dialog dialogue;
    private DialogNode currentNode;

    public DialogGo(Dialog dialogue){
        this.dialogue = dialogue;
        currentNode = dialogue.getNode(dialogue.getStart());
    }
    public DialogNode getNextNode(int pointerIndex){
        DialogNode nextNode = dialogue.getNode(currentNode.getPointers().get(pointerIndex));
        currentNode = nextNode;
        return nextNode;
    }
    public List<String> getOptions(){
        return currentNode.getLabels();
    }
    public String getText(){
        return currentNode.getText();
    }
    public DialogNode.NODE_TYPE getType(){
        return currentNode.getType();
    }
}
