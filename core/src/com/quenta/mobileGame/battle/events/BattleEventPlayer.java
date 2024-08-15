package com.quenta.mobileGame.battle.events;

import com.quenta.mobileGame.UI.DialogBox;
import com.quenta.mobileGame.UI.StatusBox;
import com.quenta.mobileGame.battle.ENTITY_LIST;

public interface BattleEventPlayer {
    public DialogBox getDialogBox();
    public StatusBox getStatusBox(ENTITY_LIST entityList);
    public void queueEvent(BattleEvent event);
}
