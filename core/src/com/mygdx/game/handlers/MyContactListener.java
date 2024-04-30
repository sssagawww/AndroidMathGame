package com.mygdx.game.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class MyContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {
    private Controllable state;

    public MyContactListener(Controllable state) {
        this.state = state;
    }

    @Override
    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        //контакт с нпс
        if (fa.getUserData() != null) {
            if(fb.getUserData().equals("player")){
                state.loadStage((String) fa.getUserData());
            } else {
                state.loadStage((String) fb.getUserData());
                if(fb.getUserData().equals("enemy") || fb.getUserData().equals("chest")){
                    fb.getBody().getFixtureList().get(0).setUserData("collided");
                    state.removeCollisionEntity(fb.getBody());
                }
            }
        } else if (fa.getUserData() == null && fb.getUserData() != "player"){
            state.loadStage("null");
        }
        System.out.println(fa.getUserData() + " contact with " + fb.getUserData());
    }

    @Override
    public void endContact(Contact c) {
        System.out.println("End Contact");
    }

    @Override
    public void preSolve(Contact c, Manifold m) {
    }

    @Override
    public void postSolve(Contact c, ContactImpulse ci) {
    }
}
