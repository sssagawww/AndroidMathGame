package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.Controller;
import com.mygdx.game.UI.JoyStick;
import com.mygdx.game.states.MazeState;
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
    private JoyStick joyStick;
    private int countIdle = 0;
    private int countMove = 1;
    private MazeState maze;

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

        checkJoyStick();
    }

    //новый метод с анимацией гнома для клавиатуры (наверное, не самый оптимальный способ)
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

    private void checkJoyStick() {
        velx = 0;
        vely = 0;
        move = false;
        if (joyStick.getState() == 1) {
            move = true;
            vely = 1; //up
        }
        if (joyStick.getState() == 2) {
            move = true;
            vely = 1; //up and right
            velx = 1;
        }
        if (joyStick.getState() == 3) {
            move = true;
            velx = 1; // right
        }
        if (joyStick.getState() == 4) {
            move = true;
            velx = 1; // right and down
            vely = -1;
        }
        if (joyStick.getState() == 5) {
            move = true; // down
            vely = -1;
        }
        if (joyStick.getState() == 6) {
            move = true;
            velx = -1; // down and left
            vely = -1;
        }
        if (joyStick.getState() == 7) {
            move = true; //left
            velx = -1;
        }
        if (joyStick.getState() == 8) {
            move = true;
            velx = -1; //up and left
            vely = 1;
        }

        if (move) {
            if (countMove == 1) {
                countIdle = 1;
                countMove = 0;
                if (joyStick.getState() == 3) {
                    setDir(RIGHT);
                    sprites = TextureRegion.split(tex, 120, 129)[3];
                    setAnimation(sprites, 1 / 12f);
                } else if (joyStick.getState() == 7) {
                    setDir(LEFT);
                    sprites = TextureRegion.split(tex, 120, 129)[1];
                    setAnimation(sprites, 1 / 12f);
                } else if (joyStick.getState() == 1 || joyStick.getState() == 2 || joyStick.getState() == 8) {
                    setDir(UP);
                    sprites = TextureRegion.split(tex, 120, 130)[2];
                    setAnimation(sprites, 1 / 12f);
                } else if (joyStick.getState() == 4 || joyStick.getState() == 5 || joyStick.getState() == 6) {
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

    public void setDir(int dir) {
        this.dir = dir;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    public void setPlay(Play play) {
        this.play = play;
        joyStick = play.getJoyStick();
    }

    public void setMaze (MazeState maze) {
        this.maze = maze;
        joyStick = maze.getJoystick();
    }

}

