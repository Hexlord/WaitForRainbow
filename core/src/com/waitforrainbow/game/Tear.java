package com.waitforrainbow.game;

/**
 * Created by SashaBoss on 13.04.2017.
 */

public class Tear {
    
    static final int TEAR_WIDTH = 16;
    static final int TEAR_HEIGHT = 16;
    static final float TEAR_DAMAGE = 0.025f;
    
    private float x;
    private float y;
    private float speed;
    private float accel;
    private boolean alive;
    
    public Tear(float x, float y, float speed, float accel) {
        
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.accel = accel;
    
        setAlive(true);
    }
    
    public void update(float delta) {
        this.speed += delta * getAccel();
        this.y -= delta * speed;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public float getAccel() {
        return accel;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
