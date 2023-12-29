package com.mygdx.game.handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.states.Play;

public class MyContactListener implements com.badlogic.gdx.physics.box2d.ContactListener {
    private Play play;
    private GameStateManager gsm;
    public MyContactListener(GameStateManager gsm){
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
        if (fa.getUserData() != null && fb.getUserData().equals("npc")) {
            play = gsm.getPlay();
            System.out.println("npc contact");
            play.canDraw = true;
            //play.initUI();
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
