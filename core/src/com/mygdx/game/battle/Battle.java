package com.mygdx.game.battle;

import com.mygdx.game.UI.SelectionBtnBox;
import com.mygdx.game.battle.events.B_TextEvent;
import com.mygdx.game.battle.events.BattleEvent;
import com.mygdx.game.battle.events.BattleEventPlayer;
import com.mygdx.game.battle.events.BattleEventQueue;
import com.mygdx.game.battle.examples.Example;
import com.mygdx.game.battle.steps.STEP_BOOLEAN;
import com.mygdx.game.battle.steps.Step;
import com.mygdx.game.battle.steps.StepsDetails;
import com.mygdx.game.entities.BattleEntity;

import java.util.ArrayList;

public class Battle implements BattleEventQueue {
    public enum STATE {
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
    private int randomNum;

    public Battle(BattleEntity player, BattleEntity enemy) {
        this.player = player;
        this.enemy = enemy;
        mechanics = new BattleMechanics();
        this.state = STATE.READY_TO_PROGRESS;
        currentIndex = 0;
        currentAnswer = 0;
        currentStepNum = 0;
    }

    public void beginBattle() {
        queueEvent(new B_TextEvent("Враг готов к атаке", true));
    }

    public void progress(int input) {
        if (state != STATE.READY_TO_PROGRESS) {
            return;
        }
        if (mechanics.isFirst(player, enemy)) {
            playTurn(ENTITY_LIST.PLAYER, input);
            System.out.println(player.getCurrentHP() + " HP " + enemy.getCurrentHP());
            if (state == STATE.READY_TO_PROGRESS) {
                playTurn(ENTITY_LIST.ENEMY, 0);
            }
        }
    }

    private void playTurn(ENTITY_LIST entity, int input) {
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

        Step step = battleUser.getStep(input);

        if (player.getStepBoolean(input + currentStepNum - randomNum) == STEP_BOOLEAN.RIGHT && battleUser == player) {
            if (mechanics.attemptHit(step, battleUser, battleTarget)) {
                queueEvent(new B_TextEvent("Правильный ответ. Атака!", 0.5f));
                step.useMove(mechanics, battleUser, battleTarget, entity, this);
            }
        } else if (player.getStepBoolean(input + currentStepNum - randomNum) == STEP_BOOLEAN.WRONG && battleUser == player) {
            queueEvent(new B_TextEvent("Неправильный ответ. Промах!", 0.5f));
        } else if (battleUser == enemy) {
            double p = Math.random();
            if (p <= 0.6) {
                queueEvent(new B_TextEvent(battleUser.getName() + " атакует!", 0.5f));
                if (mechanics.attemptHit(step, battleUser, battleTarget)) {
                    step.useMove(mechanics, battleUser, battleTarget, entity, this);
                }
            } else {
                queueEvent(new B_TextEvent(battleUser.getName() + " промахнулся!", 0.5f));
            }
            currentStepNum++;
        }

        if (player.isDefeated()) {
            queueEvent(new B_TextEvent("Проигрыш...", true));
            this.state = STATE.LOSE;
        } else if (enemy.isDefeated()) {
            queueEvent(new B_TextEvent("Ура, победа!", true));
            this.state = STATE.WIN;
        }
    }

    public void playAnswers(StepsDetails steps, SelectionBtnBox selectionBox) {
        ArrayList<Integer> prevNums = new ArrayList<>();
        int randomStepNum = -1;
        System.out.println(currentAnswer + " currentAnswer");
        randomNum = (int) (Math.random() * 4);
        for (int i = 0; i <= 3; i++) {
            prevNums.add(randomStepNum);
            randomStepNum = (int) Math.floor(Math.random() * player.getSteps().size());
            if (prevNums.contains(randomStepNum) || randomStepNum == currentIndex - 1 || randomStepNum == -1)
                randomStepNum = (int) Math.floor(Math.random() * player.getSteps().size() - 1);

            if(randomStepNum == -1)
                randomStepNum = 0;

            String label = "---";
            steps = player.getDetails(randomStepNum);
            if (steps != null) {
                label = steps.getName();
            }
            if (i != randomNum) {
                selectionBox.setLabel(i, label);
            } else {
                steps = player.getDetails(currentIndex - 1);

                label = steps.getName();
                selectionBox.setLabel(i, label);
            }
            System.out.println(steps.getStepBoolean());
        }
        prevNums.clear();
        currentAnswer++;
    }

    public void playExamples(Example example) {
        for (int i = 0; i < player.getSteps().size(); i++) {
            player.setStepBoolean(i, STEP_BOOLEAN.WRONG);
        }

        for (int i = 0; i < player.getSteps().size(); i++) {
            if (player.getStep(i).getName().equals(player.getMap().get(example.getName()))) {
                queueEvent(new B_TextEvent(example.getName(), true));
                player.setStepBoolean(i, STEP_BOOLEAN.RIGHT);
                currentIndex++;
                break;
            }
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
