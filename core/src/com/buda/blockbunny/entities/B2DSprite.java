package com.buda.blockbunny.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.buda.blockbunny.Handlers.Animation;
import com.buda.blockbunny.Handlers.B2DVars;

/**
 * Created by buda on 8/26/16.
 */
public class B2DSprite {

    protected Body body;
    protected Animation animation;
    protected float width;
    protected float height;


    public B2DSprite(Body body) {
        this.body = body;
        animation = new Animation();
    }

    //calls the setframes from the animation class
    public void setAnimation(TextureRegion[] reg, float delay) {
        animation.setFrames(reg, delay);
        width = reg[0].getRegionWidth();
        height = reg[0].getRegionHeight();
    }

    //update the animation
    public void update(float dt){
        animation.update(dt);
    }

    //here we draw the sprite
    //scaling up to regular size
    public void render(SpriteBatch batch){
        batch.begin();
        batch.draw(animation.getCurrentFrame(),
                   body.getPosition().x * B2DVars.PPM - width /2,
                   body.getPosition().y * B2DVars.PPM - height /2
                );
        batch.end();
    }

    public Body getBody() {return body;}
    public Vector2 getPosition(){return  body.getPosition();}
    public float getWidth(){return width;}
    public float getHeight(){return height;}
}
