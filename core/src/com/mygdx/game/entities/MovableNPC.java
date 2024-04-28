package com.mygdx.game.entities;

import static com.mygdx.game.handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;

public class MovableNPC extends B2DSprite {
    private TextureRegion[] sprites;
    private Texture tex;

    public MovableNPC(Body body, String texName) {
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

    public void setDirection(float velx, float vely) {
        body.setLinearVelocity(velx * speed, vely * speed);
    }
}