package com.quenta.mobileGame.battle.steps;

import com.quenta.mobileGame.battle.BattleMechanics;
import com.quenta.mobileGame.battle.ENTITY_LIST;
import com.quenta.mobileGame.battle.STAT;
import com.quenta.mobileGame.battle.events.BattleEventQueue;
import com.quenta.mobileGame.battle.events.HPAnimationEvent;
import com.quenta.mobileGame.entities.BattleEntity;

public class DamageStep extends Step{

    public DamageStep(StepsDetails details) {
        super(details);
    }
    //for anim
    @Override
    public int useMove(BattleMechanics mechanics, BattleEntity user, BattleEntity target,  ENTITY_LIST list ,BattleEventQueue eventQueue){
        int hpBefore = target.getCurrentHitpoints();
        int damage = super.useMove(mechanics, user, target, list, eventQueue);

        eventQueue.queueEvent(
                new HPAnimationEvent(
                        ENTITY_LIST.getEntities(list),
                        hpBefore,
                        target.getCurrentHitpoints(),
                        target.getStats(STAT.HP),
                        0.5f));

        return damage;
    }

    @Override
    public String message() {
        return null;
    }

    @Override
    public boolean isDamaging() {
        return true;
    }

    @Override
    public Step clone() {
        return new DamageStep(details);
    }
}
