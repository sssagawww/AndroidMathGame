package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.inputs.MyInputProcessor;

import static com.mygdx.game.handlers.B2DVars.*;

public class Player extends B2DSprite {
    private TextureRegion[] sprites;
    private Texture tex;

    private static final int idle = 0;
    private static final int top = 1;
    private static final int bottom = 2;
    private static final int left = 3;
    private static final int right = 4;

    private int animState = idle;

    private MyInputProcessor mip;

    public Player(Body body) {
        super(body);
        tex = MyGdxGame.res.getTexture("gnomik");
        sprites = TextureRegion.split(tex, 80, 88)[0]; //110 130 - 1row, 120 130 - step, 60 88 - s

        speed = 40f;
        //animUpdate(); // ?????
        setAnimation(sprites, 1 / 12f);
    }

    public void updatePL() {
        animState = idle;
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        checkUserInput();
        //checkAnim();
    }

    private void checkUserInput() {
        velx = 0;
        vely = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velx = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velx = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            vely = 1;
            animState = top;
        }
        /*if (!Gdx.input.isKeyPressed(Input.Keys.W)) {
            //canGo = false; // -------
        }*/
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            vely = -1;
        }
        body.setLinearVelocity(velx * speed, vely * speed);
    }

    private void checkAnim(){ // -------
        /*if ((Gdx.input.isKeyJustPressed(Input.Keys.W) & canGo == true)){
            sprites = TextureRegion.split(tex, 120, 129)[2];
            setAnimation(sprites, 1 / 12f);
            System.out.println(canGo + " canGO");
        }
        if ((!Gdx.input.isKeyJustPressed(Input.Keys.W) & canGo == false)){
            sprites = TextureRegion.split(tex, 120, 129)[2];
            setAnimation(sprites, 1 / 12f);
        }*/
        //if(Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY))
        switch (animState) {
            case top:
                System.out.println("top");
                sprites = TextureRegion.split(tex, 150, 130)[0];
                setAnimation(sprites, 1 / 12f);
                break;
            case idle:
                System.out.println("idle");
                sprites = TextureRegion.split(tex, 110, 130)[0];
                setAnimation(sprites, 1 / 12f);
                break;
            default:
                break;
        }
    }

    private void animUpdate(){
        //через myInpProccesor???
        sprites = TextureRegion.split(tex, 110, 130)[0];
        System.out.println("true");
        setAnimation(sprites, 1 / 12f);
    }
}

