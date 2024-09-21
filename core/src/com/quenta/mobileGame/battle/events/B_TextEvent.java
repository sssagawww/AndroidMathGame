package com.quenta.mobileGame.battle.events;

import com.quenta.mobileGame.UI.DialogBox;

public class B_TextEvent extends BattleEvent{
    private boolean finished = false;
    private float timer = 0f;
    private float delay;
    private boolean waitInput = false;
    private String text;
    private DialogBox dialogBox;

    public B_TextEvent(String text){
        this.text = text;
        this.delay = 0;
    }

    public B_TextEvent(String text, float delay) {
        this.text = text;
        this.delay = delay;
    }

    public B_TextEvent(String text, boolean awaitInput) {
        this(text);
        this.delay = 0f;
        this.waitInput = awaitInput;
    }

    @Override
    public void begin(BattleEventPlayer player) {
        super.begin(player);
        dialogBox = player.getDialogBox();
        dialogBox.setVisible(true);
        dialogBox.animateText(text);
    }

    @Override
    public void update(float dt) {
        if(dialogBox.isFinished()){
            if(waitInput){
                if (dialogBox.isPressed()) {
                    finished = true;
                }
            } else {
                timer += dt;
                if (timer >= delay){
                    timer = delay;
                    finished = true;
                }
            }
        }
    }

    @Override
    public boolean finished() {
        return finished;
    }
}
