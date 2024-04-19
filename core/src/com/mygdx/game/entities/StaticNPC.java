package com.mygdx.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;

public class StaticNPC extends B2DSprite {
    public Texture tex;
    public TextureRegion[] sprites;

    public StaticNPC(Body body, String texName, float animationSpeed) {
        super(body);
        tex = MyGdxGame.res.getTexture(texName);
        sprites = TextureRegion.split(tex, tex.getHeight(), tex.getHeight())[0];
        setAnimation(sprites, 1 / animationSpeed);
    }
}
