package com.buda.blockbunny.States;


import static com.buda.blockbunny.Handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.buda.blockbunny.Game;
import com.buda.blockbunny.Handlers.B2DVars;
import com.buda.blockbunny.Handlers.GameStateManager;
import com.buda.blockbunny.Handlers.myContactListener;

/**
 * Created by buda on 8/24/16.
 */

public class Play extends GameState {

    private World world;
    private Box2DDebugRenderer b2dr;

    //Now by dividing with ppm to solve the pixel/meter ratio problem the objects are 100 times smaller so we need a new camera
    private OrthographicCamera b2dCam;

    public Play(GameStateManager gsm) {
        super(gsm);

        //Takes two arguments (first Vector2 is the Gravity)
        // (second boolean set to true meaning that any bodies
        // that are inactive are not calculated for collision)
        world = new World(new Vector2(0, -0.81f), true);
        world.setContactListener(new myContactListener());
        b2dr = new Box2DDebugRenderer();

        //create platform box (DEFINE THE BODY)
        BodyDef bdef = new BodyDef();
        bdef.position.set(160 / PPM, 120 / PPM);
        bdef.type = BodyDef.BodyType.StaticBody;//-> they dont move at all
        //dynamic body - get affected by firces
        //kinematic body- dont get affected by body forces but you can set their forces(moving platform)

        //CREATING THE BODY
        Body body = world.createBody(bdef);


        //set property of the fixture
        //set the shape of the fixture
        PolygonShape shape = new PolygonShape();

        //shape is half width and half height so box would be 100 and 10
        shape.setAsBox(50 / PPM, 5 / PPM);

        //to create actual objects you have to define/create fiztures
        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.shape = shape;

        //setting the category for platform
        fixtureDef.filter.categoryBits = B2DVars.BIT_GROUND;

        //allowing ground to collide with box
        fixtureDef.filter.maskBits = B2DVars.BIT_BOX | B2DVars.BIT_BALL;

        //define which fixture is which
        body.createFixture(fixtureDef).setUserData("ground");





        //create falling box
        bdef.position.set(160 / PPM, 200 / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        shape.setAsBox(5 / PPM, 5 / PPM);
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0;  //Elasticity of the object 1->Very Bouncy


        fixtureDef.filter.categoryBits = B2DVars.BIT_BALL;
        fixtureDef.filter.maskBits = B2DVars.BIT_GROUND;
        body.createFixture(fixtureDef).setUserData("ball");



        //Create ball
        bdef.position.set(153 / PPM, 220 / PPM);
        body = world.createBody(bdef);

        CircleShape cshape = new CircleShape();
        cshape.setRadius(5 / PPM);
        fixtureDef.shape = cshape;


        fixtureDef.filter.categoryBits = B2DVars.BIT_BOX;
        fixtureDef.filter.maskBits = B2DVars.BIT_GROUND;
        body.createFixture(fixtureDef).setUserData("box");


        //BOX2D USES MKS UNITS Meters Kilograms Seconds 1 pixel = 1 meter;
        //we have to change the ratio so 100p have to be 1 meter;
        //set up box2dCam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

    }


    public void handleInput() {
    }

    public void update(float dt) {
        //updating the world, velocity iteration
        // -accuracy of collision how many steps do you want to check for collison
        //setting bodys position after collision
        world.step(dt, 6, 2);

    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        b2dr.render(world, b2dCam.combined);

    }

    public void dispose() {
    }
}
