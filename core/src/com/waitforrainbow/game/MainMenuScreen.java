package com.waitforrainbow.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created by SashaBoss on 13.04.2017.
 */

class MainMenuScreen implements Screen {
    
    private final WFRGame game;
    
    private OrthographicCamera camera;
    
    MainMenuScreen(final WFRGame game) {
        this.game = game;
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameScreen.WIDTH, GameScreen.HEIGHT);
    }
    
    @Override
    public void show() {
        
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
    
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.draw(game.batch, "Wait For Rainbow by Sasha Knorre\n\nTap anywhere to start playing.\n\nYour goal is to protect people from rain.\n\nTouch screen to move the umbrella.", 0, GameScreen.HEIGHT);
        game.batch.end();
        
        if(Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }
    
    @Override
    public void resize(int width, int height) {
        
    }
    
    @Override
    public void pause() {
        
    }
    
    @Override
    public void resume() {
        
    }
    
    @Override
    public void hide() {
        
    }
    
    @Override
    public void dispose() {
        
    }
}
