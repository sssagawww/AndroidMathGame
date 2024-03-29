package com.mygdx.game.Dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.OptionBox;

public class DialogController extends InputAdapter{
    private DialogGo dialogGo;
    private DialogBox dialogBox;
    private OptionBox optionBox;

    public DialogController(DialogBox dialogBox, OptionBox optionBox){
        this.dialogBox = dialogBox;
        this.optionBox = optionBox;
    }

    @Override
    public boolean keyDown(int keycode){
        if (dialogBox.isVisible()){
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode){
        if (optionBox.isVisible()){
            if(keycode == Input.Keys.E){
                optionBox.moveDown();
                return true;
            } else if(keycode == Input.Keys.Q){
                optionBox.moveUp();
                return true;
            }
        }
        if (dialogGo != null && keycode == Input.Keys.X && dialogBox.isFinished()){
            if(dialogGo.getType() == DialogNode.NODE_TYPE.END){
                dialogGo = null;
                dialogBox.setVisible(false);
            } else if (dialogGo.getType() == DialogNode.NODE_TYPE.LINEAR) {
                progress(0);
            } else if (dialogGo.getType() == DialogNode.NODE_TYPE.MULTIPLE_CHOICE) {
                progress(optionBox.getID());
            }
            return true;
        }
        if(dialogBox.isVisible()){
            return true;
        }
        return false;
    }

    public void update(float dt){
        if (dialogBox.isFinished() && dialogGo != null) {
            if(dialogGo.getType() == DialogNode.NODE_TYPE.MULTIPLE_CHOICE){
                optionBox.setVisible(true);
            }
        }

        if (dialogGo != null && Gdx.input.justTouched() && dialogBox.isFinished()){
            if(dialogGo.getType() == DialogNode.NODE_TYPE.END){
                dialogGo = null;
                dialogBox.setVisible(false);
            } else if (dialogGo.getType() == DialogNode.NODE_TYPE.LINEAR) {
                progress(0);
            } else if (dialogGo.getType() == DialogNode.NODE_TYPE.MULTIPLE_CHOICE) {
                progress(optionBox.getID());
            }
        }
    }

    public void startDialog(Dialog dialog){
        dialogGo = new DialogGo(dialog);
        dialogBox.setVisible(true);
        dialogBox.animateText(dialogGo.getText());
        if(dialogGo.getType() == DialogNode.NODE_TYPE.MULTIPLE_CHOICE){
            optionBox.clearChoices();
            for (String s : dialog.getNode(dialog.getStart()).getLabels()) {
                optionBox.addOption(s);
            }
        }
    }

    private void progress(int index){
        optionBox.setVisible(false);
        DialogNode nextNode = dialogGo.getNextNode(index);
        dialogBox.animateText(nextNode.getText());
        if (nextNode.getType() == DialogNode.NODE_TYPE.MULTIPLE_CHOICE) {
            optionBox.clearChoices();
            for (String s : nextNode.getLabels()) {
                optionBox.addOption(s);
            }
        }
    }

}
