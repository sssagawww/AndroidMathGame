package com.mygdx.game.battle;

import com.mygdx.game.UI.SelectionBtnBox;
import com.mygdx.game.battle.events.B_TextEvent;
import com.mygdx.game.battle.events.BattleEvent;
import com.mygdx.game.battle.events.BattleEventPlayer;
import com.mygdx.game.battle.events.BattleEventQueue;
import com.mygdx.game.battle.examples.EXAMPLE_NUM;
import com.mygdx.game.battle.examples.Example;
import com.mygdx.game.battle.steps.STEP_BOOLEAN;
import com.mygdx.game.battle.steps.Step;
import com.mygdx.game.battle.steps.StepsDetails;
import com.mygdx.game.entities.BattleEntity;

public class Battle implements BattleEventQueue {
    public enum STATE{
        READY_TO_PROGRESS,
        WIN,
        LOSE,
        RUN,
        ;
    }
    private STATE state;
    private BattleEventPlayer eventPlayer;
    private BattleEntity player;
    private BattleEntity enemy;
    private BattleMechanics mechanics;
    public int currentIndex;
    public int currentAnswer;
    public int currentStepNum;
    public boolean isRight = false;

    public Battle(BattleEntity player, BattleEntity enemy){
        this.player = player;
        this.enemy = enemy;
        mechanics = new BattleMechanics();
        this.state = STATE.READY_TO_PROGRESS;
        currentIndex = 1;
        currentAnswer = 0;
        currentStepNum = 0;
    }

    public void beginBattle(){
        queueEvent(new B_TextEvent("Враг готов к атаке", true));
    }

    public void progress(int input){
        if(state != STATE.READY_TO_PROGRESS){
            return;
        }
        if(mechanics.isFirst(player, enemy)){
            playTurn(ENTITY_LIST.PLAYER, input);
            System.out.println(player.getCurrentHP() + " HP " + enemy.getCurrentHP());
            if (state == STATE.READY_TO_PROGRESS) {
                playTurn(ENTITY_LIST.ENEMY, 0);
            }
        }
    }

    private void playTurn(ENTITY_LIST entity, int input){
        ENTITY_LIST list = ENTITY_LIST.getEntities(entity);
        BattleEntity battleUser = null;
        BattleEntity battleTarget = null;

        if (entity == ENTITY_LIST.PLAYER) {
            battleUser = player;
            battleTarget = enemy;
        } else if (entity == ENTITY_LIST.ENEMY) {
            battleUser = enemy;
            battleTarget = player;
        }

        Step step = battleUser.getSteps(input);

        queueEvent(new B_TextEvent(battleUser.getName() + " атакует!", 0.5f));

        if(player.getBoolean(input + currentStepNum) == STEP_BOOLEAN.RIGHT && battleUser == player){
            if(mechanics.attemptHit(step, battleUser, battleTarget)){
                step.useMove(mechanics, battleUser, battleTarget, entity, this);
            }
        } else if(player.getBoolean(input + currentStepNum) == STEP_BOOLEAN.WRONG && battleUser == player){
            queueEvent(new B_TextEvent("Неправильный ответ. Промах!", 0.5f));
        } else if(battleUser == enemy){
            if(mechanics.attemptHit(step, battleUser, battleTarget)){
                step.useMove(mechanics, battleUser, battleTarget, entity, this);
            }
            currentStepNum++;
        }

        if(player.isDefeated()){
            queueEvent(new B_TextEvent("Проигыш...", true));
            this.state = STATE.LOSE;
        } else if(enemy.isDefeated()){
            queueEvent(new B_TextEvent("Ура, победа!", true));
            this.state = STATE.WIN;
        }
    }

    public void playAnswers(StepsDetails steps, SelectionBtnBox selectionBox){
        System.out.println(currentAnswer + " currentAnswer");
        for (int i = 0; i <= 3; i++) {
            String label = "---";
            steps = player.getDetails(currentAnswer + i);
            if (steps != null) {
                label = steps.getName();
            }
            selectionBox.setLabel(i, label);
            System.out.println(steps.getStepBoolean());
        }
        currentAnswer++;
    }

    public void playExamples(Example example){ //if else???
        for (int i = 0; i < 10; i++){
            player.setStepBoolean(i, STEP_BOOLEAN.WRONG);
        }
        System.out.println(example.getList());
        Example thisEx = example;
        if(example.getList() == EXAMPLE_NUM.EXAMPLE_1){
            queueEvent(new B_TextEvent(thisEx.getName(), true));
            player.setStepBoolean(1, STEP_BOOLEAN.RIGHT);
            currentIndex++;
        } else if(example.getList() == EXAMPLE_NUM.EXAMPLE_2){
            queueEvent(new B_TextEvent(thisEx.getName(), true));
            player.setStepBoolean(3, STEP_BOOLEAN.RIGHT);
            currentIndex++;
        } else if(example.getList() == EXAMPLE_NUM.EXAMPLE_3){
            queueEvent(new B_TextEvent(thisEx.getName(), true));
            player.setStepBoolean(2, STEP_BOOLEAN.RIGHT);
            currentIndex++;
        } else if(example.getList() == EXAMPLE_NUM.EXAMPLE_4){
            queueEvent(new B_TextEvent(thisEx.getName(), true));
            player.setStepBoolean(6, STEP_BOOLEAN.RIGHT);
            currentIndex++;
        } else if(example.getList() == EXAMPLE_NUM.EXAMPLE_5){
            queueEvent(new B_TextEvent(thisEx.getName(), true));
            player.setStepBoolean(7, STEP_BOOLEAN.RIGHT);
        }
    }

    public BattleEntity getPlayer() {
        return player;
    }

    public BattleEntity getEnemy() {
        return enemy;
    }

    public STATE getState() {
        return state;
    }

    public BattleEventPlayer getEventPlayer() {
        return eventPlayer;
    }

    public void setEventPlayer(BattleEventPlayer player) {
        this.eventPlayer = player;
    }

    @Override
    public void queueEvent(BattleEvent event) {
        eventPlayer.queueEvent(event);
    }
}
