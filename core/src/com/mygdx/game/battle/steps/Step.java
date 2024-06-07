package com.mygdx.game.battle.steps;

import com.mygdx.game.battle.BattleMechanics;
import com.mygdx.game.battle.ENTITY_LIST;
import com.mygdx.game.battle.events.BattleEventQueue;
import com.mygdx.game.entities.BattleEntity;

public abstract class Step {
    protected StepsDetails details;
    //protected Class<? extends BattleAnimation> animationClass;

    public Step(StepsDetails details){
        this.details = details;
    }

    public int useMove(BattleMechanics mechanics, BattleEntity user, BattleEntity enemy, ENTITY_LIST list, BattleEventQueue eventQueue){
        int damage = mechanics.calculateDamage(this, user, enemy);
        enemy.applyDamage(damage);
        return damage;
    }

    public abstract String message();

    public abstract boolean isDamaging();

    public String getName() {
        return details.getName();
    }

    public STEP_TYPE getType(){
        return details.getType();
    }

    public int getDamage() {
        return details.getDamage();
    }

    public StepsDetails getStepDetails() {
        return details;
    }

    public STEP_BOOLEAN getStepBoolean(){
        return details.getStepBoolean();
    }
    public void setStepBoolean(STEP_BOOLEAN stepBoolean) {
        details.setStepBoolean(stepBoolean);
    }

    public abstract Step clone();
}
