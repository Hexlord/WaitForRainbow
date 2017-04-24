package com.waitforrainbow.game;

/**
 * Created by SashaBoss on 13.04.2017.
 */

public class Person {
    
    static final int PERSON_WIDTH = 80;
    static final int PERSON_HEIGHT = 210;
    
    private float x;
    private float y;
    private float speed;
    private int direction;
    private float life;
    
    private boolean alive;
    
    public Person(float x, float y, float speed, int direction) {
        
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.direction = direction;
        
        this.setLife(1);
        
        alive = true;
    }
    
    public void update(float delta) {
        if(getLife() > 0) {
            x += delta * speed * direction;
        }
        
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
    
    public int getDirection() {
        return direction;
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    
    public float getLife() {
        return life;
    }
    
    public void setLife(float life) {
        this.life = life;
    }
}
