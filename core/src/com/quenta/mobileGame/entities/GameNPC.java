package com.quenta.mobileGame.entities;

import static com.quenta.mobileGame.handlers.B2DVars.PPM;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.quenta.mobileGame.MyGdxGame;

public class GameNPC extends B2DSprite {
    private TextureRegion[] sprites;
    private Texture tex;
    private float time = 0;
    private float velx = 0;
    private float vely = 0;

    public GameNPC(Body body, String texName) {
        super(body);
        tex = MyGdxGame.res.getTexture(texName);
        sprites = TextureRegion.split(tex, 58, 58)[0];
        speed = 20f;
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

    public void randomDirection(int speed, float dt) {
        time += dt;
        if (time >= 2) {
            time = 0;
            setDirection((float) (0.5f - Math.random()), (float) (0.5f - Math.random()), speed, 58, 58);
        }
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