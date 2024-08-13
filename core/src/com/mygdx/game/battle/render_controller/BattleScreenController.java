package com.mygdx.game.battle.render_controller;

import com.badlogic.gdx.InputAdapter;
import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.OptionBox2;
import com.mygdx.game.UI.SelectionBtnBox;
import com.mygdx.game.battle.Battle;
import com.mygdx.game.battle.events.B_TextEvent;
import com.mygdx.game.battle.events.BattleEvent;

import java.util.Queue;

public class BattleScreenController extends InputAdapter {
    private Battle battle;
    private DialogBox dialogBox;
    private OptionBox2 optionBox;
    private SelectionBtnBox selectionBox;
    private Queue<BattleEvent> queue;

    public enum STATE {
        CAN_RUN,
        SELECT_ACTION,
        DEACTIVATED,
        ;
    }

    private STATE state = STATE.DEACTIVATED;

    public BattleScreenController(Battle battle, Queue queue, DialogBox dialogBox, OptionBox2 optionBox, SelectionBtnBox selectionBox) {
        this.battle = battle;
        this.dialogBox = dialogBox;
        this.optionBox = optionBox;
        this.selectionBox = selectionBox;
        this.queue = queue;
    }

    public void update(float dt) {
        if (itsRun() && dialogBox.isFinished() && !optionBox.isVisible()) {
            optionBox.clearChoices();
            optionBox.addBtn("Да");
            optionBox.addBtn("Нет");
            optionBox.setVisible(true);
        }

        checkTouch();
    }

    public void restart() {
        battle.playExamples(battle.getPlayer().getExample(battle.currentIndex));
        this.state = STATE.SELECT_ACTION;
        dialogBox.setVisible(false);
        battle.playAnswers(selectionBox);
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

    private void checkTouch() {
        dialogBox.setPressed(false);
        if (selectionBox.isPressed() && selectionBox.isVisible()) {
            int selection = selectionBox.getSelectedIndex();
            if (battle.getPlayer().getStep(selection) == null) {
                queue.add(new B_TextEvent("Всё :0", 0.5f));
            } else {
                dialogBox.setPressed(true);
                battle.progress(selection);
                endTurn();
            }
        }
    }
}
