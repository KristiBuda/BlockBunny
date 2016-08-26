package com.buda.blockbunny.States;


import static com.buda.blockbunny.Handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.buda.blockbunny.Game;
import com.buda.blockbunny.Handlers.B2DVars;
import com.buda.blockbunny.Handlers.GameStateManager;
import com.buda.blockbunny.Handlers.MyInput;
import com.buda.blockbunny.Handlers.myContactListener;
import com.buda.blockbunny.entities.Crystal;
import com.buda.blockbunny.entities.HUD;
import com.buda.blockbunny.entities.Player;

/**
 * Created by buda on 8/24/16.
 */

public class Play extends GameState {

    private boolean debug = false;

    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;
    private myContactListener cl;

    //adding the map to the game
    private TiledMap tiledMap;

    //drawing the map
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    //crate the player
    private Player player;

    //create the array of crystals
    private Array<Crystal> crystals;

    private HUD hud;

    //tilesize
    private float tilesize;


    public Play(GameStateManager gsm) {

        super(gsm);
        //setup box2d stuff
        //Takes two arguments (first Vector2 is the Gravity)
        // (second boolean set to true meaning that any bodies
        // that are inactive are not calculated for collision)
        world = new World(new Vector2(0, -9.81f), true);
        cl = new myContactListener();
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        //create player
        createPlayer();

        //create tiles
        createTiles();

//        //create crystals
//        createCrystals();

        //BOX2D USES MKS UNITS Meters Kilograms Seconds 1 pixel = 1 meter;
        //we have to change the ratio so 100p have to be 1 meter;
        //set up box2dCam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
        //setup hud
        hud = new HUD(player);
    }

    public void handleInput() {

        //player jump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            //using the ghost fixture we can determine
            // if hes on the ground or no
            // so if his on the ground we can make him jump
            if (cl.isPlayerOnGround()) {
                player.getBody().applyForceToCenter(0, 250, true);
            }
        }
        //switch block color
        if (MyInput.isPressed(MyInput.BUTTON2)) {
            switchBlocks();
        }


    }

    public void update(float dt) {
        handleInput();
        //updating the world, velocity iteration
        // -accuracy of collision how many steps do you want to check for collison
        //setting bodys position after collision
        world.step(dt, 6, 2);

//        //remove the crystals
//        Array<Body> bodies = cl.getBodiesToRemove();
//        for (int i = 0; i < bodies.size; i++) {
//            Body b = bodies.get(i);
//            crystals.removeValue((Crystal) b.getUserData(), true);
//            world.destroyBody(bodies.get(i));
//            player.collectCrystal();
//        }
//        bodies.clear();

        player.update(dt);
//        //update crystals
//        for (int i = 0; i < crystals.size; i++) {
//            crystals.get(i).update(dt);
//        }
    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //draw tilemap
        tiledMapRenderer.setView(cam);
        tiledMapRenderer.render();

        //set camera to follow the player
        cam.position.set(player.getPosition().x * PPM + Game.V_WIDTH / 4, Game.V_WIDTH / 2, 0);
        cam.update();

        //draw the player
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);

//        //draw crystals
//        for (int i = 0; i < crystals.size; i++) {
//            crystals.get(i).render(sb);
//        }

        //darw hud
        sb.setProjectionMatrix(hudCam.combined);
        hud.render(sb);


        //draw box2D debugger
        if (debug) {
            //draw box2d world
            b2dr.render(world, b2dCam.combined);
        }
    }

    public void dispose() {
    }

    private void createPlayer() {

        BodyDef bdef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        //create falling player
        bdef.position.set(70 / PPM, 200 / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        //bunny needs to be moving constantly to the right
        bdef.linearVelocity.set(1, 0);
        Body body = world.createBody(bdef);

        shape.setAsBox(13 / PPM, 13 / PPM);
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0;  //Elasticity of the object 1->Very Bouncy
        fixtureDef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fixtureDef.filter.maskBits = B2DVars.BIT_RED | B2DVars.BIT_CRYSTAL;
        body.createFixture(fixtureDef).setUserData("player");

        //create foot sensor
        shape.setAsBox(13 / PPM, 2 / PPM, new Vector2(0, -13 / PPM), 0);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fixtureDef.filter.maskBits = B2DVars.BIT_RED;
        //sensor fixture (foot fixture) detects collision but doesn't handle it and you can travel through it.
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef).setUserData("foot");

        //create player
        player = new Player(body);

        //we have a circular reference
        body.setUserData(player);
//        //get the player class
//        player.getBody();
    }

    private void createTiles() {

        tiledMap = new TmxMapLoader().load("res/maps/test.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        tilesize = (int) tiledMap.getProperties().get("tilewidth", Integer.class);

        //reading the tile layers
        TiledMapTileLayer layer;
        layer = (TiledMapTileLayer) tiledMap.getLayers().get("red");
        createLayer(layer, B2DVars.BIT_RED);

        layer = (TiledMapTileLayer) tiledMap.getLayers().get("green");
        createLayer(layer, B2DVars.BIT_GREEN);

        layer = (TiledMapTileLayer) tiledMap.getLayers().get("blue");
        createLayer(layer, B2DVars.BIT_BLUE);
    }

    private void createLayer(TiledMapTileLayer layer, short bits) {

        BodyDef bdef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        //row -> Y axis
        //col -> X axis
        //Go through all the cells in the layer to create box2D bodies;
        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {

                //Get Cell
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                //check if cell exists
                if (cell == null) continue;
                if (cell.getTile() == null) continue;

                //cell found create a body and a fixture
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((col + 0.5f) * tilesize / PPM, (row + 0.5f) * tilesize / PPM);

                ChainShape chainShape = new ChainShape();

                Vector2[] v = new Vector2[3];

                v[0] = new Vector2(-tilesize / 2 / PPM, -tilesize / 2 / PPM);
                v[1] = new Vector2(-tilesize / 2 / PPM, tilesize / 2 / PPM);
                v[2] = new Vector2(tilesize / 2 / PPM, tilesize / 2 / PPM);
                chainShape.createChain(v);
                fixtureDef.friction = 0;
                fixtureDef.shape = chainShape;
                fixtureDef.filter.categoryBits = bits;
                fixtureDef.filter.maskBits = B2DVars.BIT_PLAYER;
                fixtureDef.isSensor = false;
                world.createBody(bdef).createFixture(fixtureDef);
            }
        }

    }
//
//    private void createCrystals() {
//        crystals = new Array<Crystal>();
//        MapLayer layer = tiledMap.getLayers().get("crystals");
//
//        BodyDef bodyDef = new BodyDef();
//        FixtureDef fixtureDef = new FixtureDef();
//
//        for (MapObject mo : layer.getObjects()) {
//
//            bodyDef.type = BodyDef.BodyType.StaticBody;
//
//            float x = Float.parseFloat((mo.getProperties().get("x").toString())) / PPM;
//            float y = Float.parseFloat((mo.getProperties().get("y").toString())) / PPM;
//
//            bodyDef.position.set(x, y);
//
//            CircleShape circleShape = new CircleShape();
//            circleShape.setRadius(8 / PPM);
//
//            fixtureDef.shape = circleShape;
//            fixtureDef.isSensor = true;
//            fixtureDef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
//            fixtureDef.filter.maskBits = B2DVars.BIT_PLAYER;
//
//            Body body = world.createBody(bodyDef);
//            body.createFixture(fixtureDef).setUserData("crystal");
//            Crystal crystal = new Crystal(body);
//            crystals.add(crystal);
//
//            body.setUserData(crystals);
//        }
//    }

    private void switchBlocks() {

        Filter filter = player.getBody().getFixtureList().first().getFilterData();
        short bits = filter.maskBits;

        //switch to next color
        if ((bits & B2DVars.BIT_RED) != 0) {
            bits &= ~B2DVars.BIT_RED;
            bits |= B2DVars.BIT_GREEN;
        } else if ((bits & B2DVars.BIT_GREEN) != 0) {
            bits &= ~B2DVars.BIT_BLUE;
            bits |= B2DVars.BIT_GREEN;
        } else if ((bits & B2DVars.BIT_BLUE) != 0) {
            bits &= ~B2DVars.BIT_BLUE;
            bits |= B2DVars.BIT_RED;
        }

        //set new mask bits for the players
        filter.maskBits = bits;
        player.getBody().getFixtureList().first().setFilterData(filter);

        //set new mask bits for foot
        filter = player.getBody().getFixtureList().get(1).getFilterData();
        bits &= ~B2DVars.BIT_CRYSTAL;
        filter.maskBits = bits;
        player.getBody().getFixtureList().get(1).setFilterData(filter);
    }
}
