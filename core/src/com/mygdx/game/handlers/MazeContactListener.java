package com.mygdx.game.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.states.MazeState;
import com.mygdx.game.states.Play;

public class MazeContactListener implements com.badlogic.gdx.physics.box2d.ContactListener{

    private MazeState mazeState;
    private GameStateManager gsm;

    public MazeContactListener(GameStateManager gsm) {
        this.gsm = gsm;
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
            mazeState = gsm.getMazeState();
            System.out.println("contact with " + fb.getUserData());
            mazeState.loadStage((String) fb.getUserData());

        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
