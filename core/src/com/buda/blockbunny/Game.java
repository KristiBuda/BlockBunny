package com.buda.blockbunny;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.buda.blockbunny.Handlers.Content;
import com.buda.blockbunny.Handlers.GameStateManager;
import com.buda.blockbunny.Handlers.MyInput;
import com.buda.blockbunny.Handlers.MyInputProcessor;

public class Game extends ApplicationAdapter {


    //time step
    public static final float STEP = 1/60f;
    private float accum;

    public static final int V_HEIGHT =  240;
    public static final int V_WIDTH  =  320;
    public static final int SCALE = 2;
    public static final String TITLE = "Block Bunny";


	private    SpriteBatch batch;
    protected  OrthographicCamera cam;
    protected  OrthographicCamera hudCam;

    private GameStateManager gsm;

    public static Content res;

    @Override
	public void create () {
        //now game is using our custom made proccesor
        Gdx.input.setInputProcessor(new MyInputProcessor());

        //LOADING TEXTURES WITH THE CONTENT CLASS
//        res = new Content();
//        res.loadTexture("res/images/bunny.png", "bunny");

        batch = new SpriteBatch();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        hudCam = new OrthographicCamera();
        hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        gsm = new GameStateManager(this);
	}

	@Override
	public void render () {

		accum +=Gdx.graphics.getDeltaTime();
        while (accum >= STEP){
            accum -= STEP;
            gsm.update(STEP);
            gsm.render();
            MyInput.update();
        }

	}
	
	@Override
	public void dispose () {batch.dispose();}

    public SpriteBatch getBatch() {return batch;}
    public OrthographicCamera getCam() {return cam;}
    public OrthographicCamera getHudCam() {return hudCam;}

    public void resize(int w, int h){}
    public void pause(){}
    public void resume(){}

}
