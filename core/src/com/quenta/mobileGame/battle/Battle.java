package com.quenta.mobileGame.battle;

import com.quenta.mobileGame.UI.SelectionBtnBox;
import com.quenta.mobileGame.battle.events.B_TextEvent;
import com.quenta.mobileGame.battle.events.BattleEvent;
import com.quenta.mobileGame.battle.events.BattleEventPlayer;
import com.quenta.mobileGame.battle.events.BattleEventQueue;
import com.quenta.mobileGame.battle.examples.Example;
import com.quenta.mobileGame.battle.steps.STEP_BOOLEAN;
import com.quenta.mobileGame.battle.steps.Step;
import com.quenta.mobileGame.battle.steps.StepsDetails;
import com.quenta.mobileGame.entities.BattleEntity;

import java.util.ArrayList;

public class Battle implements BattleEventQueue {
    public enum STATE {
        READY_TO_PROGRESS,
        WIN,
        LOSE,
        RUN,
        ;
    }

    public enum ENEMY_STATE {
        WAITING,
        WIN,
        LOSE,
        ATTACK,
        MISS,
        HURT
    }

    private STATE state;
    private ENEMY_STATE enemyState;
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
                enemyState = ENEMY_STATE.HURT;
                queueEvent(new B_TextEvent("Правильный ответ. Атака!", 0.5f));
                step.useMove(mechanics, battleUser, battleTarget, entity, this);
            }
        } else if (player.getStepBoolean(input + currentStepNum - randomNum) == STEP_BOOLEAN.WRONG && battleUser == player) {
            enemyState = ENEMY_STATE.WIN;
            queueEvent(new B_TextEvent("Неправильный ответ. Промах!", 0.5f));
        } else if (battleUser == enemy) {
            double p = Math.random();
            if (p <= 0.6) {
                enemyState = ENEMY_STATE.ATTACK;
                queueEvent(new B_TextEvent(battleUser.getName() + " атакует!", 0.5f));
                if (mechanics.attemptHit(step, battleUser, battleTarget)) {
                    step.useMove(mechanics, battleUser, battleTarget, entity, this);
                }
            } else {
                enemyState = ENEMY_STATE.MISS;
                queueEvent(new B_TextEvent(battleUser.getName() + " промахнулся!", 0.5f));
            }
            currentStepNum++;
        }

        if (player.isDefeated()) {
            enemyState = ENEMY_STATE.WIN;
            queueEvent(new B_TextEvent("Проигрыш...", true));
            this.state = STATE.LOSE;
        } else if (enemy.isDefeated()) {
            enemyState = ENEMY_STATE.LOSE;
            queueEvent(new B_TextEvent("Ура, победа!", true));
            this.state = STATE.WIN;
        }
    }

    public void playAnswers(SelectionBtnBox selectionBox) {
        ArrayList<Integer> prevNums = new ArrayList<>();
        int randomStepNum = -1;
        System.out.println(currentAnswer + " currentAnswer");
        randomNum = (int) (Math.random() * 4); //позиция в кнопках, на которой ответ будет стоять
        for (int i = 0; i <= 3; i++) {
            prevNums.add(randomStepNum);
            randomStepNum = (int) Math.floor(Math.random() * player.getSteps().size()); //номер другого неправильного ответа

            if(randomStepNum == currentIndex - 1)
                prevNums.add(randomStepNum);

            if (prevNums.contains(randomStepNum) || randomStepNum == -1)
                while (prevNums.contains(randomStepNum)){
                    randomStepNum = (int) Math.floor(Math.random() * player.getSteps().size() - 1);
                }

            String label = "---";
            StepsDetails step = player.getDetails(randomStepNum);
            if (step != null) {
                label = step.getName();
            }

            //если i-ая позиция кнопки не равна той, где верный ответ, ставим туда рандомный ответ
            if (i != randomNum) {
                selectionBox.setLabel(i, label);
            } else {
                step = player.getDetails(currentIndex - 1);

                label = step.getName();
                selectionBox.setLabel(i, label);
            }
            System.out.println(step.getStepBoolean());
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

    public ENEMY_STATE getEnemyState() {
        return enemyState;
    }

    public void setEnemyState(ENEMY_STATE enemyState) {
        this.enemyState = enemyState;
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
