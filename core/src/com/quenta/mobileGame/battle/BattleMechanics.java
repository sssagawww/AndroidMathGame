package com.quenta.mobileGame.battle;

import com.quenta.mobileGame.battle.steps.STEP_TYPE;
import com.quenta.mobileGame.battle.steps.Step;
import com.quenta.mobileGame.entities.BattleEntity;

public class BattleMechanics {
    private String message = "";
    private int damage;

    /*private boolean criticalHit(Step step, BattleEntity user, BattleEntity enemy){
        float probability = 1f/16f;
        if (probability >= MathUtils.random(1.0f)) {
            return true;
        } else {
            return false;
        }
    }*/

    public boolean isFirst(BattleEntity player, BattleEntity enemy){
        return true;
    }
    public boolean attemptHit(Step step, BattleEntity user, BattleEntity enemy) {
        return true;
    }

    public int calculateDamage(Step step, BattleEntity user, BattleEntity enemy){
        message = "";

        float attack = 0f;
        if(step.getType() == STEP_TYPE.DEFAULT){
            attack = user.getStats(STAT.ATTACK);
        } else {
            attack = user.getStats(STAT.SPECIAL_ATTACK);
        }

        float defence = 0f;
        if(step.getType() == STEP_TYPE.DEFAULT){
            defence = enemy.getStats(STAT.DEFENCE);
        } else {
            defence = enemy.getStats(STAT.SPECIAL_DEFENCE);
        }

        int level = user.getLevel();
        float base = step.getDamage();

        damage = (int) (((2f*level+10f)/250f * (float)attack/defence * base + 2)); //?
        System.out.println(damage + " damage");
        return damage;
    }

    public int getCalcDamage(){
        return damage;
    }
    public boolean hasMessage() {
        return !message.isEmpty();
    }

    public String getMessage() {
        return message;
    }
}
