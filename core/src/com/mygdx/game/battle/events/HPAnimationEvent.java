package com.mygdx.game.battle.events;

import com.badlogic.gdx.math.Interpolation;
import com.mygdx.game.UI.HP_Bar;
import com.mygdx.game.UI.StatusBox;
import com.mygdx.game.battle.ENTITY_LIST;

public class HPAnimationEvent extends BattleEvent{
    private ENTITY_LIST entities;

    private int hpBefore;
    private int hpAfter;
    private int hpTotal;
    private float duration;

    private BattleEventPlayer eventPlayer;
    private float timer;
    private boolean finished;

    public HPAnimationEvent(ENTITY_LIST entities, int hpBefore, int hpAfter, int hpTotal, float duration) {
        this.entities = entities;
        this.hpBefore = hpBefore;
        this.hpAfter = hpAfter;
        this.hpTotal = hpTotal;
        this.duration = duration;
        this.timer = 0f;
        this.finished = false;
    }

    @Override
    public void update(float dt) {
        timer += dt;
        if (timer > duration) {
            finished = true;
        }

        float progress = timer/duration;
        float hpProgress = Interpolation.linear.apply(hpBefore, hpAfter, progress);
        float hpProgressRelative = hpProgress/hpTotal;

        //System.out.println("progress " + progress + " HPprogress " + hpProgress + " relative " + hpProgressRelative); //???

        HP_Bar hpbar = eventPlayer.getStatusBox(entities).getHPBar();
        hpbar.displayHPLeft(hpProgressRelative);

        StatusBox statusBox = eventPlayer.getStatusBox(entities);
        /*if (statusBox instanceof PlayerStatusBox) {
            ((PlayerStatusBox)statusBox).setHPText((int)hpProgress, hpTotal);
        }*/
    }

    @Override
    public void begin(BattleEventPlayer player) {
        super.begin(player);
        this.eventPlayer = player;
    }

    @Override
    public boolean finished() {
        return finished;
    }
}
