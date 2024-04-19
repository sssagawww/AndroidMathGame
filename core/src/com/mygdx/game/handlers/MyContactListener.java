package com.mygdx.game.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class MyContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {
    private Controllable state;
    private GameStateManager gsm;
    public MyContactListener(Controllable state){
        this.state = state;
    }
    @Override
    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        //была проверка дополнительного сенсора у персонажа, сейчас не юзается?
        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            System.out.println("fa is foot");
        }
        if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
            System.out.println("fb is foot");
        }

        //контакт с нпс, юзается
        if (fa.getUserData() != null) {
            System.out.println("contact with " + fb.getUserData());
            state.loadStage((String) fb.getUserData());
        }
        //System.out.println(fa.getUserData() + ", " + fb.getUserData());
    }

    @Override
    public void endContact(Contact c) {
        System.out.println("End Contact");
    }

    @Override
    public void preSolve(Contact c, Manifold m) {}

    @Override
    public void postSolve(Contact c, ContactImpulse ci) {}
}
