package com.buda.blockbunny.Handlers;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

/**
 * Created by buda on 8/24/16.
 */

public class myContactListener implements ContactListener {

    private int numFootContacts;
    //keep a list of all the bodies that i want to remove
    private Array<Body> bodiesToRemove;


    public myContactListener() {
        super();
        bodiesToRemove = new Array<Body>();
    }



    //called when two fixtures collide
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa == null || fb == null) return;

        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts++;
        }
        if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
            numFootContacts++;
        }

        if (fa.getUserData() != null && fa.getUserData().equals("crystal")) {
            bodiesToRemove.add(fa.getBody());
        }
        if (fb.getUserData() != null && fb.getUserData().equals("crystal")) {
            bodiesToRemove.add(fb.getBody());
        }
    }

    //called when two fixtures are not colliding
    public void endContact(Contact contact) {

        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if (fa == null || fb == null) return;

        if (fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts--;
        }
        if (fb.getUserData() != null && fb.getUserData().equals("foot")) {
            numFootContacts--;
        }
    }


    public boolean isPlayerOnGround(){ return numFootContacts > 0;}
    public Array<Body> getBodiesToRemove(){return bodiesToRemove;}

    //collision detection
    //presolve happens between this two so you decide how to handle the collision
    //collision handling
    public void preSolve(Contact c, Manifold m) {
    }

    public void postSolve(Contact c, ContactImpulse ci) {
    }


}
