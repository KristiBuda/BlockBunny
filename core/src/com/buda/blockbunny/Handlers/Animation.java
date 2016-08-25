package com.buda.blockbunny.Handlers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by buda on 8/25/16.
 */
public class Animation {

    //array takes the textureregion and gives back the texture that you want
    private TextureRegion[] frames;

    //current time
    private float time;

    //how much time between each frame to move on to the next image
    private float delay;

    //what frame we are currently on
    private int currentFrame;

    //how much the animation is played
    private int timesPlayed;

    public Animation(){

    }

    public Animation(TextureRegion[] frames) {
        this(frames, 1 / 12f);
    }

    public Animation(TextureRegion[] frames, float delay) {
        setFrames(frames, delay);
    }

    public void setFrames(TextureRegion[] frames, float delay){
        this.frames = frames;
        this.delay = delay;
        time = 0;
        currentFrame = 0;
        timesPlayed = 0;
    }

    public void update(float dt){
        if (delay <= 0) return;
        time+=dt;
        while (time >= delay){
            step();
        }
    }

    private void step(){
        time -= delay;
        currentFrame++;
        if (currentFrame == frames.length){
            currentFrame = 0;
            timesPlayed++;
        }
    }

    public TextureRegion getCurrentFrame() {return frames[currentFrame];}
    public int getTimesPlayed() {return timesPlayed;}
}
