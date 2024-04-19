package com.mygdx.game.handlers;

import com.mygdx.game.UI.JoyStick;

public interface Controllable {
    public JoyStick getJoyStick();
    public void loadStage(String s);
}
