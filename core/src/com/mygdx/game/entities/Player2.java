package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.Controller;
import com.mygdx.game.states.Play;

import static com.mygdx.game.handlers.B2DVars.*;
import static com.mygdx.game.handlers.B2DVars.PlayerAnim.*;

public class Player2 extends B2DSprite {
    private TextureRegion[] sprites;
    private Texture tex;
    private Texture tex2;
    private int dir = IDLE;
    private boolean move = false;
    private Play play;
    private Controller controller;
    private boolean canCheck = false;

    public Player2(Body body) {
        super(body);
        tex = MyGdxGame.res.getTexture("gnomik");
        tex2 = MyGdxGame.res.getTexture("gnomikrow");
        sprites = TextureRegion.split(tex, 80, 88)[0];
        speed = 40f;
        setAnimation(sprites, 1 / 12f);
    }

    public void updatePL() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        if (canCheck) checkControllerInput();
    }

    /*private void loadAnim(int array){
        //sprites = new TextureRegion[8][10];
        sprites[array] = TextureRegion.split(tex, 120, 140)[array];
    }*/

    // для клавиатуры
    private void checkUserInput() {
        velx = 0;
        vely = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            setDir(RIGHT);
            velx = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            setDir(LEFT);
            velx = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            setDir(UP);
            vely = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            setDir(DOWN);
            vely = -1;
        }
        body.setLinearVelocity(velx * speed, vely * speed);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            checkAnim();
            System.out.println(dir + " direction");
        } else {
            if (move) {
                System.out.println("MOVE TRUE");
                setDir(IDLE);
                checkAnim();
                move = false;
            }
        }
    }

    // для андроида
    private void checkControllerInput() {
        velx = 0;
        vely = 0;
        if (controller.isRightPressed()) {
            setDir(RIGHT);
            velx = 1;
        }
        if (controller.isLeftPressed()) {
            setDir(LEFT);
            velx = -1;
        }
        if (controller.isUpPressed()) {
            setDir(UP);
            vely = 1;
        }
        if (controller.isDownPressed()) {
            setDir(DOWN);
            vely = -1;
        }
        if (controller.isUpRightPressed()) {
            velx = 1;
            vely = 1;
        }
        if (controller.isUpLeftPressed()) {
            velx = -1;
            vely = 1;
        }
        if (controller.isDownRightPressed()) {
            velx = 1;
            vely = -1;
        }
        if (controller.isDownLeftPressed()) {
            vely = -1;
            velx = -1;
        }
        body.setLinearVelocity(velx * speed, vely * speed);
    }

    //переделать!
    private void checkAnim() {
        switch (dir) {
            case RIGHT:
                sprites = TextureRegion.split(tex, 80, 86)[3];
                break;
            case LEFT:
                sprites = TextureRegion.split(tex, 80, 86)[1];
                break;
            case UP:
                sprites = TextureRegion.split(tex, 80, 88)[2];
                break;
            case DOWN:
                sprites = TextureRegion.split(tex, 80, 88)[0];
                break;
            case IDLE:
                sprites = TextureRegion.split(tex2, 110, 130)[0];
                break;
            default:
                return;
        }
        setAnimation(sprites, 1 / 12f);
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    public void setPlay(Play play) {
        this.play = play;
        controller = play.getController();
        canCheck = true;
    }
}

