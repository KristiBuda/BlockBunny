package com.buda.blockbunny.Handlers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by buda on 8/24/16.
 */

public class myContactListener implements ContactListener {

    //called when two fixtures collide
    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        System.out.println(fa.getUserData() + ", " + fb.getUserData());
    }

    //called when two fixtures are not colliding
    public void endContact(Contact c) {
    }


    //collision detection
    //presolve happens between this two so you decide how to handle the collision
    //collision handling
    public void preSolve(Contact c, Manifold m) {
    }

    public void postSolve(Contact c, ContactImpulse ci) {
    }


}
