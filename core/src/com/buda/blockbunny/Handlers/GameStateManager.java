package com.buda.blockbunny.Handlers;

import com.buda.blockbunny.Game;
import com.buda.blockbunny.States.GameState;
import com.buda.blockbunny.States.Play;

import java.util.Stack;

/**
 * Created by buda on 8/24/16.
 */
public class GameStateManager {

    private Game game;
    private Stack<GameState> gameStates;

    public static final int PLAY = 912837;


    public GameStateManager(Game game) {
        this.game = game;
        gameStates = new Stack<GameState>();
        pushState(PLAY);
    }

    public void update(float dt){
        gameStates.peek().update(dt);
    }

    public void render(){
        gameStates.peek().render();
    }

    public  Game game(){return game;}

    private GameState getState(int state){
        if(state == PLAY) return new Play(this);
        return null;
    }

    public void setState(int state){
        popState();
        pushState(state);
    }

    public void pushState(int state){
        gameStates.push(getState(state));
    }

    public void popState(){
        GameState g = gameStates.pop();
        g.dispose();
    }
}
