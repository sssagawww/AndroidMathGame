package com.quenta.mobileGame.handlers;

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

        //контакт с коллизией
        if (fa.getUserData() != null) {
            if(fb.getUserData().equals("player")){
                state.loadStage((String) fa.getUserData(), fa.getBody());
            } else {
                state.loadStage((String) fb.getUserData(), fb.getBody());
                if(/*fb.getUserData().equals("enemy") || fb.getUserData().equals("enemy2") ||*/ fb.getUserData().equals("chest") || fb.getUserData().equals("mushroom")){
                    fb.getBody().getFixtureList().get(0).setUserData("collided");
                    state.removeCollisionEntity(fb.getBody());
                }
            }
        } else if (fa.getUserData() == null && fb.getUserData() != "player" && !fb.getUserData().equals("rabbit")){
            state.loadStage("null", null);
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
