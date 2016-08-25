package com.buda.blockbunny.States;


import static com.buda.blockbunny.Handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.buda.blockbunny.Game;
import com.buda.blockbunny.Handlers.B2DVars;
import com.buda.blockbunny.Handlers.GameStateManager;
import com.buda.blockbunny.Handlers.MyInput;
import com.buda.blockbunny.Handlers.myContactListener;

/**
 * Created by buda on 8/24/16.
 */

public class Play extends GameState {

    private World world;
    private Box2DDebugRenderer b2dr;

    //Now by dividing with ppm to solve the pixel/meter ratio problem the objects are 100 times smaller so we need a new camera
    private OrthographicCamera b2dCam;

    //Make reference to the player body
    private Body playerBody;
    private myContactListener cl;

    //adding the map to the game
    private TiledMap tiledMap;
    //drawing the map
    private OrthogonalTiledMapRenderer tiledMapRenderer;

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

        //BOX2D USES MKS UNITS Meters Kilograms Seconds 1 pixel = 1 meter;
        //we have to change the ratio so 100p have to be 1 meter;
        //set up box2dCam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

        //////////////////////////////////////////////////////////////////////////////////////////////

        tiledMap = new TmxMapLoader().load("res/maps/test.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

        //reading the tile layers
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("red");
        tilesize = layer.getTileHeight();

    }
    
    public void handleInput() {

        //player jump
        if (MyInput.isPressed(MyInput.BUTTON1)) {

            //using the ghost fixture we can determine
            // if hes on the ground or no
            // so if his on the ground we can make him jump

            if (cl.isPlayerOnGround()) {
                playerBody.applyForceToCenter(0, 200, true);
            }
        }

    }

    public void update(float dt) {

        handleInput();

        //updating the world, velocity iteration
        // -accuracy of collision how many steps do you want to check for collison
        //setting bodys position after collision
        world.step(dt, 6, 2);

    }

    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //draw tilemap
        tiledMapRenderer.setView(cam);
        tiledMapRenderer.render();

        //draw box2d world
        b2dr.render(world, b2dCam.combined);

    }

    public void dispose() {
    }

    private void createPlayer() {

        BodyDef bdef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        //create falling player
        bdef.position.set(160 / PPM, 200 / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bdef);

        shape.setAsBox(5 / PPM, 5 / PPM);
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0;  //Elasticity of the object 1->Very Bouncy
        fixtureDef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fixtureDef.filter.maskBits = B2DVars.BIT_RED;
        playerBody.createFixture(fixtureDef).setUserData("player");
        //create foot sensor
        shape.setAsBox(2 / PPM, 2 / PPM, new Vector2(0, -5 / PPM), 0);
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fixtureDef.filter.maskBits = B2DVars.BIT_RED;
        //sensor fixture (footfixture) detects collision but doesn't handle it and you can travel through it.
        fixtureDef.isSensor = true;

        playerBody.createFixture(fixtureDef).setUserData("foot");


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
                fixtureDef.filter.categoryBits = B2DVars.BIT_RED;
                fixtureDef.filter.maskBits = B2DVars.BIT_PLAYER;
                fixtureDef.isSensor = false;
                world.createBody(bdef).createFixture(fixtureDef);
            }
        }

    }

}
