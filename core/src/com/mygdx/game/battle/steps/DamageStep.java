package com.mygdx.game.battle.steps;

import com.mygdx.game.battle.BattleMechanics;
import com.mygdx.game.battle.ENTITY_LIST;
import com.mygdx.game.battle.STAT;
import com.mygdx.game.battle.events.BattleEventQueue;
import com.mygdx.game.battle.events.HPAnimationEvent;
import com.mygdx.game.entities.BattleEntity;

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
