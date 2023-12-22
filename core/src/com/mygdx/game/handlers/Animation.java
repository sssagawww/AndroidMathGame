package com.mygdx.game.handlers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation {
    private TextureRegion[] frames;
    private float time;
    private float delay;
    private int currentFrame;
    private int timesPlayed;

    public Animation(){

    }

    public Animation(TextureRegion[] frames){
        this(frames, 1/12);
    }

    public Animation(TextureRegion[] frames, float delay){
        setFrames(frames, delay);
    }

    public void setFrames(TextureRegion[] frames, float delay) {
        this.frames = frames;
        this.delay = delay;
        time = 0;
        currentFrame = 1;
        timesPlayed = 0;
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
        if (currentFrame == frames.length) {
            currentFrame = 0;
            timesPlayed++;
        }
    }

    /*private void unStep() {
        System.out.println("unstep");
        time -= delay;
        currentFrame--;
        if (currentFrame == -1) {
            currentFrame = 2;
            timesPlayed--;
        }
    }*/

    public TextureRegion getFrames() {
        return frames[currentFrame];
    }
    public int getTimesPlayed(){
        return timesPlayed;
    }
}
