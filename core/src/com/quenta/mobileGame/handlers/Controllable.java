package com.quenta.mobileGame.handlers;

import com.badlogic.gdx.physics.box2d.Body;
import com.quenta.mobileGame.UI.Controller;
import com.quenta.mobileGame.UI.JoyStick;

public interface Controllable {
    public JoyStick getJoyStick();
    public void loadStage(String s, Body contactBody);
    public void removeCollisionEntity(Body body);

    public Controller getController();
}
