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
    private int countIdle = 0;
    private int countMove = 1;

    public Player2(Body body) {
        super(body);
        tex = MyGdxGame.res.getTexture("gnomik");
        tex2 = MyGdxGame.res.getTexture("gnomikrow");
        sprites = TextureRegion.split(tex2, 120, 130)[0];
        speed = 40f;
        setAnimation(sprites, 1 / 12f);
    }

    public void updatePL() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        //if (canCheck) checkControllerInput();
        checkController();
    }

    //новый метод с анимацией гнома для клавиатуры
    private void check() {
        velx = 0;
        vely = 0;
        move = false;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            move = true;
            velx = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            move = true;
            velx = -1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            move = true;
            vely = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            move = true;
            vely = -1;
        }

        if (move) {
            countIdle = 1;
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                setDir(RIGHT);
                sprites = TextureRegion.split(tex, 120, 129)[3];
                setAnimation(sprites, 1 / 12f);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                setDir(LEFT);
                sprites = TextureRegion.split(tex, 120, 129)[1];
                setAnimation(sprites, 1 / 12f);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                setDir(UP);
                sprites = TextureRegion.split(tex, 120, 130)[2];
                setAnimation(sprites, 1 / 12f);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                setDir(DOWN);
                sprites = TextureRegion.split(tex, 120, 129)[0];
                setAnimation(sprites, 1 / 12f);
            }
        } else if (countIdle == 1) {
            countIdle = 0;
            switch (dir) {
                case RIGHT:
                    sprites = TextureRegion.split(tex2, 120, 130)[3];
                    break;
                case LEFT:
                    sprites = TextureRegion.split(tex2, 120, 130)[1];
                    break;
                case UP:
                    sprites = TextureRegion.split(tex2, 120, 130)[2];
                    break;
                case DOWN:
                    sprites = TextureRegion.split(tex2, 120, 130)[0];
                    break;
                default:
                    return;
            }
            setAnimation(sprites, 1 / 6f);
        }
        body.setLinearVelocity(velx * speed, vely * speed);
    }

    private void checkController() {
        velx = 0;
        vely = 0;
        move = false;
        if (controller.isRightPressed()) {
            move = true;
            velx = 1;
        }
        if (controller.isLeftPressed()) {
            move = true;
            velx = -1;
        }
        if (controller.isUpPressed()) {
            move = true;
            vely = 1;
        }
        if (controller.isDownPressed()) {
            move = true;
            vely = -1;
        }
        if (controller.isUpRightPressed()) {
            move = true;
            velx = 1;
            vely = 1;
        }
        if (controller.isUpLeftPressed()) {
            move = true;
            velx = -1;
            vely = 1;
        }
        if (controller.isDownRightPressed()) {
            move = true;
            velx = 1;
            vely = -1;
        }
        if (controller.isDownLeftPressed()) {
            move = true;
            vely = -1;
            velx = -1;
        }

        if (move) {
            if (countMove == 1) {
                countIdle = 1;
                countMove = 0;
                if (controller.isRightPressed()) {
                    setDir(RIGHT);
                    sprites = TextureRegion.split(tex, 120, 129)[3];
                    setAnimation(sprites, 1 / 12f);
                } else if (controller.isLeftPressed()) {
                    setDir(LEFT);
                    sprites = TextureRegion.split(tex, 120, 129)[1];
                    setAnimation(sprites, 1 / 12f);
                } else if (controller.isUpPressed() || controller.isUpRightPressed() || controller.isUpLeftPressed()) {
                    setDir(UP);
                    sprites = TextureRegion.split(tex, 120, 130)[2];
                    setAnimation(sprites, 1 / 12f);
                } else if (controller.isDownPressed() || controller.isDownRightPressed() || controller.isDownLeftPressed()) {
                    setDir(DOWN);
                    sprites = TextureRegion.split(tex, 120, 129)[0];
                    setAnimation(sprites, 1 / 12f);
                }
            }
        } else if (countIdle == 1) {
            countIdle = 0;
            countMove = 1;
            switch (dir) {
                case RIGHT:
                    sprites = TextureRegion.split(tex2, 120, 130)[3];
                    break;
                case LEFT:
                    sprites = TextureRegion.split(tex2, 120, 130)[1];
                    break;
                case UP:
                    sprites = TextureRegion.split(tex2, 120, 130)[2];
                    break;
                case DOWN:
                    sprites = TextureRegion.split(tex2, 120, 130)[0];
                    break;
                default:
                    return;
            }
            setAnimation(sprites, 1 / 6f);
        }
        body.setLinearVelocity(velx * speed, vely * speed);
    }

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
            if (!move) {
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

    //не используется, т.к криво работает
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

