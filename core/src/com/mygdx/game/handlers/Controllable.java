package com.mygdx.game.handlers;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.UI.Controller;
import com.mygdx.game.UI.JoyStick;

public interface Controllable {
    public JoyStick getJoyStick();
    public void loadStage(String s);
    public void removeCollisionEntity(Body body);

    public Controller getController();
}
