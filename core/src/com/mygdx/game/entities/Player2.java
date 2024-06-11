package com.mygdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UI.Global;
import com.mygdx.game.UI.JoyStick;

import com.mygdx.game.handlers.Controllable;
import com.mygdx.game.states.DungeonState;

import static com.mygdx.game.handlers.B2DVars.*;
import static com.mygdx.game.handlers.B2DVars.PlayerAnim.*;

public class Player2 extends B2DSprite {
    private TextureRegion[] sprites;
    private Texture tex;
    private Texture tex2;
    private int dir = IDLE;
    private boolean move = false;
    private Controllable state;
    private JoyStick joyStick;
    private int countIdle = 1;
    private int countMove = 1;
    private Music sound;
    private long soundId;

    public Player2(Body body) {
        super(body);
//        sound = ;
        tex = MyGdxGame.res.getTexture("gnomik");
        tex2 = MyGdxGame.res.getTexture("gnomikrow");
        sprites = TextureRegion.split(tex2, 120, 130)[0];
        speed = 40f;
        setAnimation(sprites, 1 / 12f);
    }

    public void updatePL() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        if(state.getController().getSoundSettings().getSliderSoundEff().isDragging()) sound.setVolume(getSoundEffVolume());
        checkJoyStick();
    }

    private float getSoundEffVolume() {
        return state.getController().getSoundSettings().getSliderSoundEff().getPercent();
    }

    //новый метод с анимацией гнома для клавиатуры (не самый оптимальный способ)
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

    private int prevDir = IDLE;

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
            vely = (float) Math.sqrt(0.7f); //up and right
            velx = (float) Math.sqrt(0.7f);
        }
        if (joyStick.getState() == 3) {
            move = true;
            velx = 1; // right
        }
        if (joyStick.getState() == 4) {
            move = true;
            velx = (float) Math.sqrt(0.7f); // right and down
            vely = -(float) Math.sqrt(0.7f);
        }
        if (joyStick.getState() == 5) {
            move = true; // down
            vely = -1;
        }
        if (joyStick.getState() == 6) {
            move = true;
            velx = -(float) Math.sqrt(0.7f); // down and left
            vely = -(float) Math.sqrt(0.7f);
        }
        if (joyStick.getState() == 7) {
            move = true; //left
            velx = -1;
        }
        if (joyStick.getState() == 8) {
            move = true;
            velx = -(float) Math.sqrt(0.7f); //up and left
            vely = (float) Math.sqrt(0.7f);
        }

        if (move) {
            if (joyStick.getState() == 3) {
                setDir(RIGHT);
                sprites = TextureRegion.split(tex, 120, 129)[3];
            } else if (joyStick.getState() == 7) {
                setDir(LEFT);
                sprites = TextureRegion.split(tex, 120, 129)[1];
            } else if (joyStick.getState() == 1 || joyStick.getState() == 2 || joyStick.getState() == 8) {
                setDir(UP);
                sprites = TextureRegion.split(tex, 120, 130)[2];
            } else if (joyStick.getState() == 4 || joyStick.getState() == 5 || joyStick.getState() == 6) {
                setDir(DOWN);
                sprites = TextureRegion.split(tex, 120, 129)[0];
            }
            if (getDir() != prevDir || prevDir == IDLE) {
                setAnimation(sprites, 1 / 12f);
            }
            prevDir = dir;
            if (countMove == 1) {
                sound.play();
                sound.setLooping(true);
                countIdle = 1;
                countMove = 0;
            }
        } else if (countIdle == 1) {
            sound.stop();
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
            prevDir = IDLE;
            setAnimation(sprites, 1 / 6f);
        }
        body.setLinearVelocity(velx * speed, vely * speed);
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    public int getDir() {
        return dir;
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    public void setCountIdle(int countIdle) {
        this.countIdle = countIdle;
    }

    public void setState(Controllable state) {
        this.state = state;
        joyStick = state.getJoyStick();
        sound = (state instanceof DungeonState ? Gdx.audio.newMusic(Gdx.files.internal("music/steps_dun_and_maze.mp3")) : Gdx.audio.newMusic(Gdx.files.internal("music/steps_grass3.mp3")));
        Global.soundEffs.add(sound);
    }

    public void stopSounds(){
        sound.stop();
    }

    public void setSpeed(float speed){
        this.speed = speed;
    }
}

