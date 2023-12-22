package com.mygdx.game.handlers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PlayerAnimation {
    private TextureRegion[][] frames;
    private float time;
    private float delay;
    private int currentFrame;
    private int index;

    public PlayerAnimation(){

    }

    public void setFrames(TextureRegion[][] frames, float delay) {
        this.frames = frames;
        this.delay = delay;
        time = 0;
        currentFrame = 0;
        index = 0;
    }

    public void update(float dt){
        if (delay <= 0) {
            return;
        }
        time+=dt;
        while (time >= delay){
            step();
        }
    }
    private void step() {
        time -= delay;
        currentFrame++;
        if (currentFrame == frames[0].length) {
            currentFrame = 0;
            index++;
            if(index >= frames.length){
                index = 0;
            }
        }
    }

    public TextureRegion getFrames() {
        return frames[0][currentFrame];
    }
    public int getIndex(){
        return index;
    }
}
