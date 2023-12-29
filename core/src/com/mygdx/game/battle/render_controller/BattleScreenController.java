package com.mygdx.game.battle.render_controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.mygdx.game.UI.Controller;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.OptionBox;
import com.mygdx.game.UI.SelectionBox;
import com.mygdx.game.battle.Battle;
import com.mygdx.game.battle.events.B_TextEvent;
import com.mygdx.game.battle.events.BattleEvent;
import com.mygdx.game.battle.steps.StepsDetails;

import java.util.Queue;

public class BattleScreenController extends InputAdapter {
    private Battle battle;
    private DialogBox dialogBox;
    private OptionBox optionBox;
    private SelectionBox selectionBox;
    private Queue<BattleEvent> queue;

    public enum STATE {
        CAN_RUN,
        SELECT_ACTION,
        DEACTIVATED,
        ;
    }

    private STATE state = STATE.DEACTIVATED;

    public BattleScreenController(Battle battle, Queue queue, DialogBox dialogBox, OptionBox optionBox, SelectionBox selectionBox) {
        this.battle = battle;
        this.dialogBox = dialogBox;
        this.optionBox = optionBox;
        this.selectionBox = selectionBox;
        this.queue = queue;
    }

    public void update(float dt) {
        if (itsRun() && dialogBox.isFinished() && !optionBox.isVisible()) {
            optionBox.clearChoices();
            optionBox.addOption("Да");
            optionBox.addOption("Нет");
            optionBox.setVisible(true);
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        /*if (this.state == STATE.CAN_RUN && !optionBox.isVisible()) {
            System.out.println("sasdasfasf");
            if (keycode == Input.Keys.Q) {
                optionBox.moveUp();
            } else if (keycode == Input.Keys.E) {
                optionBox.moveDown();
            } else if (keycode == Input.Keys.X) {
                if (optionBox.getIndex() == 0){
                    System.out.println(":)");
                } else if (optionBox.getIndex() == 1) {
                    battle.playerRun();
                    optionBox.setVisible(false);
                }
            }
        }*/
        if(selectionBox.isVisible()) {
            if (keycode == Input.Keys.X) {
                int selection = selectionBox.getSelection();
                if (battle.getPlayer().getSteps(selection) == null){
                    queue.add(new B_TextEvent("Всё :0", 0.5f));
                } else {
                    battle.progress(selectionBox.getSelection());
                    endTurn();
                }
            } else if (keycode == Input.Keys.W) {
                selectionBox.moveUp();
                return true;
            } else if (keycode == Input.Keys.S) {
                selectionBox.moveDown();
                return true;
            } else if (keycode == Input.Keys.A) {
                selectionBox.moveLeft();
                return true;
            } else if (keycode == Input.Keys.D) {
                selectionBox.moveRight();
                return true;
            }
        }
        return false;
    }

    public void restart() {
        battle.playExamples(battle.getPlayer().getExamples(battle.currentIndex));
        this.state = STATE.SELECT_ACTION;
        dialogBox.setVisible(false);
        battle.playAnswers(battle.getPlayer().getDetails(battle.currentAnswer), selectionBox);
        /*for (int i = 0; i <= 3; i++) {
            String label = "------";
            StepsDetails details = battle.getPlayer().getDetails(i);
            if (details != null) {
                label = details.getName();
            }
            selectionBox.setLabel(i, label);
        }*/
        selectionBox.setVisible(true);
    }

    public boolean itsRun() {
        return this.state == STATE.CAN_RUN;
    }

    private void endTurn() {
        selectionBox.setVisible(false);
        this.state = STATE.DEACTIVATED;
    }

    public void displayNextDialogue() {
        this.state = STATE.CAN_RUN;
        dialogBox.setVisible(true);
        dialogBox.animateText("Убежать?");
    }

    public STATE getState() {
        return state;
    }
}
