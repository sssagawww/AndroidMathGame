package com.mygdx.game.entities;

import static com.mygdx.game.handlers.B2DVars.PPM;
import static com.mygdx.game.handlers.B2DVars.PlayerAnim.IDLE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;

public class SlimeBoss extends B2DSprite {
    private TextureRegion[] sprites;
    private Texture tex;
    private int countIdle = 1;
    private int countMove = 1;
    private Sound sound;
    private long soundId;
    private float time = 0;
    private float velx = 0;
    private float vely = 0;
    private int randomAnimations[] = new int[]{1, 2};

    public SlimeBoss(Body body) {
        super(body);
        sound = Gdx.audio.newSound(Gdx.files.internal("music/steps_dun_and_maze.mp3"));
        tex = MyGdxGame.res.getTexture("slimeBoss");
        sprites = TextureRegion.split(tex, 32, 32)[0];
        speed = 40f;
        setAnimation(sprites, 1 / 5f);
    }

    public void updatePos() {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
    }

    public void setNewAnimation(int row, int width, int height) {
        TextureRegion[] sprites = TextureRegion.split(tex, width, height)[row];
        setAnimation(sprites, 1 / 5f);
    }

    public void randomAnimation(float dt) {
        time += dt;
        if (time >= 3 && time < 3.2f) {
            time = 3.2f;
            int r = (int) (Math.random() * 2);
            setNewAnimation(randomAnimations[r], 32, 32);
        } else if (time >= 6 && time <= 6.2f) {
            setNewAnimation(0, 32, 32);
        } else if(time >= 10){
            time = 0;
        }
    }

    public void setDirection(float velx, float vely, float speed, int width, int height) {
        this.velx = velx;
        this.vely = vely;
        if (velx < 0 || vely < 0) {
            setNewAnimation(2, width, height);
        } else if (velx == 0 && vely == 0) {
            setNewAnimation(0, width, height);
        } else {
            setNewAnimation(1, width, height);
        }
        body.setLinearVelocity(velx * speed, vely * speed);
    }

    public float getVelx() {
        return velx;
    }

    public float getVely() {
        return vely;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
