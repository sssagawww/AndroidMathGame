package com.mygdx.game.battle.events;

import com.mygdx.game.UI.DialogBox;
import com.mygdx.game.UI.StatusBox;
import com.mygdx.game.battle.ENTITY_LIST;

public interface BattleEventPlayer {
    public DialogBox getDialogBox();
    public StatusBox getStatusBox(ENTITY_LIST entityList);
    public void queueEvent(BattleEvent event);
}
