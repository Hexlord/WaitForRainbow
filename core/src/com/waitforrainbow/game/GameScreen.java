package com.waitforrainbow.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen implements Screen {
    
    private enum State {
        STATE_PLAYING,
        STATE_END,
    }
    
    // game dimensions
    static final int WIDTH = 1280;
    static final int HEIGHT = 768;
    
    // umbrella offset from cursor position
    private static final int UMBRELLA_OFFSET_X = 2;
    private static final int UMBRELLA_OFFSET_Y = 40;
    
    // umbrella texture size
    private static final int UMBRELLA_WIDTH = 128;
    private static final int UMBRELLA_HEIGHT = 128;
    
    // umbrella touch radius
    private static final int UMBRELLA_RADIUS = 74;
    
    // rainbow texture size
    private static final int RAINBOW_WIDTH = 640;
    private static final int RAINBOW_HEIGHT = 480;
    
    // rainbow heal per second
    private static final float RAINBOW_HEAL = 0.25f;
    
    private float umbrellaX = 0;
    private float umbrellaY = 0;
    
    private Vector3 touchVector;
    
    private OrthographicCamera camera;
    
    private Texture tUmbrella;
    private Texture tGuyGood;
    private Texture tGuyNormal;
    private Texture tGuySad;
    private Texture tGuyDead;
    private Texture tTear;
    private Texture tRainbow;
    
    private Sound sBubble;
    private Sound sWaterDrop;
    private Sound sSad;
    private Music mRain;
    
    private final WFRGame game;
    
    private State state;
    private float score;
    private float endTimer; // to prevent accidental tap to try again
    
    private ArrayList<Tear> tearList;
    private ArrayList<Person> personList;
    
    private float personSpawnTimer;
    private float rainbowSpawnTimer;
    private float rainbowTimer;
    
    private Rectangle rectangle1;
    private Rectangle rectangle2;
    
    private Random random;
    
    private int itest = 5;
    
    public GameScreen(final WFRGame game) {
        
        this.game = game;
        
        random = new Random();
        
        itest = 6;
        
        state = State.STATE_PLAYING;
        score = 0;
        endTimer = 0;
        personSpawnTimer = 1;
        rainbowSpawnTimer = 30;
        rainbowTimer = 0;
        umbrellaX = WIDTH/2;
        umbrellaY = HEIGHT/2;
        
        touchVector = new Vector3();
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        
        tUmbrella = new Texture("imageUmbrella.png");
        tGuyGood = new Texture("imageGuyGood.png");
        tGuyNormal = new Texture("imageGuyNormal.png");
        tGuySad = new Texture("imageGuySad.png");
        tGuyDead = new Texture("imageGuyDead.png");
        tTear = new Texture("imageTear.png");
        tRainbow = new Texture("imageRainbow.png");
        
        sBubble = Gdx.audio.newSound(Gdx.files.internal("bubble.mp3"));
        sWaterDrop = Gdx.audio.newSound(Gdx.files.internal("waterDrop.mp3"));
        sSad = Gdx.audio.newSound(Gdx.files.internal("sad.mp3"));
        mRain = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        
        mRain.setLooping(true);
        
        tearList = new ArrayList<Tear>();
        personList = new ArrayList<Person>();
        
        rectangle1 = new Rectangle();
        rectangle2 = new Rectangle();
    }
    
    public void update(float delta) {
        
        if (state == State.STATE_END) {
            // wait time before touch result in a new game
            // prevent accidental touch just after the game ends
            if(endTimer > 0) {
                endTimer -= delta;
            }
            if(endTimer <= 0) {
                if(Gdx.input.isTouched()) {
                    // set state to new game
                    state = State.STATE_PLAYING;
                    score = 0;
                    umbrellaX = WIDTH/2;
                    umbrellaY = HEIGHT/2;
                    personSpawnTimer = 1;
                    personList.clear();
                    tearList.clear();
                }
            }
        }
        
        if(state == State.STATE_PLAYING) {
            score += delta;
            
            personSpawnTimer -= delta;
            if(personSpawnTimer <= 0) {
                // time until next person is out
                // spawn a person
                // set time until next person to random number between 10 and 30 seconds
                personSpawnTimer = random.nextInt(21) + 10;
                // set person speed to random number between 30 adn 75
                float speed = random.nextInt(46) + 30;
                
                // pick random direction: 1 (right) or -1 (left)
                int direction = random.nextInt(2) == 1 ? 1 : -1;
                
                // set x position to rightmost if moving left and the other way around
                float x = direction == 1 ? (0 - Person.PERSON_WIDTH/2) : (WIDTH + Person.PERSON_WIDTH/2);
                
                // y is at the bottom, showing the whole person
                Person person = new Person(x, Person.PERSON_HEIGHT / 2, speed, direction);
                personList.add(person);
            }
            
            rainbowSpawnTimer -= delta;
            if(rainbowSpawnTimer <= 0) {
                // if time until rainbow is out,
                // set rainbow time span to 6 seconds
                // set time until rainbow to random number between 26 and (26 + 0.25 * seconds survived) seconds
                rainbowTimer = 6;
                rainbowSpawnTimer = 20 + 6 + random.nextFloat()*score*0.25f;
            }
            
            if(rainbowTimer > 0) {
                rainbowTimer -= delta;
            }
            
            if(rainbowTimer <= 0) {
                // if rainbow is not shown, spawn rain drops
                
                // generate random amount of water
                float water = random.nextFloat() * (15 + score * 1.5f) * delta;
                while(water > 0) {
                    // use some water to spawn tear
                    float r = random.nextFloat();
                    if(r <= water) {
                        // set position to random topmost position
                        float x =
                                random.nextFloat() * (WIDTH + 1 + Tear.TEAR_WIDTH) - Tear.TEAR_WIDTH / 2;
                        float y = HEIGHT + Tear.TEAR_HEIGHT/2 + random.nextFloat() * Tear.TEAR_HEIGHT * 4;
            
                        // set acceleration to random number between (15 + 2.5 * seconds survived)
                        // and (15 + 3.75 * seconds survived) seconds
                        float accel = 15 + score * 2.5f + random.nextFloat() * score * 1.25f;
                        // raise speed to as 1 second has already passed
                        float speed = accel * 1;
            
                        Tear tear = new Tear(x, y, speed, accel);
                        tearList.add(tear);
                    }
                    water -= r;
                }
            }
        }
        
        // move umbrella to cursor
        
        if (Gdx.input.isTouched()) {
            
            touchVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchVector);
            
            umbrellaX = touchVector.x + UMBRELLA_OFFSET_X;
            umbrellaY = touchVector.y + UMBRELLA_OFFSET_Y;
        }
        
        // perform activity
        
        for (Tear tear : tearList) {
            tear.update(delta);
            
            // tears below window disappear
            if (tear.getY() < 0 - Tear.TEAR_HEIGHT) {
                tear.setAlive(false);
            }
            
            if(state == State.STATE_PLAYING) {
                if (tear.isAlive()) {
                    float dx = tear.getX() - umbrellaX;
                    float dy = tear.getY() - umbrellaY;
                    float dSquared = dx * dx + dy * dy;
                    if (dSquared < UMBRELLA_RADIUS * UMBRELLA_RADIUS) {
                        // tears hitting umbrella disappear and result in a sound
                        tear.setAlive(false);
                        sBubble.play();
                    }
                }
            }
        }
        
        for (Person person : personList) {
            person.update(delta);
            
            // if rainbow is going to last longer than 100 ms, heal person
            if(rainbowTimer > 0.1) {
                person.setLife(person.getLife() + RAINBOW_HEAL * delta);
            }
            
            // remove persons leaving the window
            if (person.getX() > GameScreen.WIDTH + Person.PERSON_WIDTH ||
                    person.getX() < 0 - Person.PERSON_WIDTH) {
                person.setAlive(false);
            }
            
            if(state == State.STATE_PLAYING) {
                if (person.isAlive()) {
        
                    rectangle1.set(person.getX() - Person.PERSON_WIDTH / 2, person.getY() - Person.PERSON_HEIGHT / 2, Person.PERSON_WIDTH, Person.PERSON_HEIGHT);
        
                    for (Tear tear : tearList) {
                        if (tear.isAlive()) {
                            rectangle2.set(tear.getX() - Tear.TEAR_WIDTH / 2, tear.getY() - Tear.TEAR_HEIGHT / 2, Tear.TEAR_WIDTH, Tear.TEAR_HEIGHT);
                
                            if (rectangle1.overlaps(rectangle2) ||
                                    rectangle1.contains(rectangle2) ||
                                    rectangle2.contains(rectangle1)) {
                                // tear collides with this person, tear disappears, sound is played,
                                // person takes damage
                                tear.setAlive(false);
                                sWaterDrop.play();
                    
                                if (person.getLife() > 0) {
                                    person.setLife(person.getLife() - Tear.TEAR_DAMAGE);
                                    if (person.getLife() <= 0) {
                                        // person died, he screams and game state changes to game end
                                        sSad.play();
                                        state = State.STATE_END;
                                        endTimer = 1.0f;
                                        rainbowTimer = 0;
                                        rainbowSpawnTimer = 30;
                                    }
                                }
                            }
                        }
                    }
        
                }
            }
        }
        
        // remove dead stuff
        
        ArrayList<Tear> nextTearList = new ArrayList<Tear>();
        
        for (Tear tear : tearList) {
            if (tear.isAlive()) {
                nextTearList.add(tear);
            }
        }
        
        tearList = nextTearList;
        
        ArrayList<Person> nextPersonList = new ArrayList<Person>();
        
        for (Person person : personList) {
            if (person.isAlive()) {
                nextPersonList.add(person);
            }
        }
        
        personList = nextPersonList;
    }
    
    
    @Override
    public void render(float delta) {
        
        update(delta);
        
        
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        
        if(rainbowTimer > 0) {
            game.batch.draw(tRainbow,
                    WIDTH / 2 - RAINBOW_WIDTH / 2,
                    HEIGHT / 2 - RAINBOW_HEIGHT / 2);
        }
        
        for(Tear tear : tearList) {
            game.batch.draw(tTear,
                    tear.getX() - Tear.TEAR_WIDTH/2,
                    tear.getY() - Tear.TEAR_HEIGHT/2);
        }
        
        for(Person person : personList) {
            Texture texture = null;
            // use texture depending on guy mood
            if(person.getLife() <= 0.25f) {
                texture = tGuyDead;
            } else if(person.getLife() <= 0.5f) {
                texture = tGuySad;
            } else if(person.getLife() <= 0.75f) {
                texture = tGuyNormal;
            } else {
                texture = tGuyGood;
            }
            
            float w = Person.PERSON_WIDTH * person.getDirection();
            float h = Person.PERSON_HEIGHT;
            float x = person.getX() - Person.PERSON_WIDTH/2 * person.getDirection();
            float y = person.getY() - Person.PERSON_HEIGHT/2;
            
            game.batch.draw(texture,
                    x,
                    y,
                    w,
                    h);
            
        }
        
        if (state == State.STATE_PLAYING) {
            game.batch.draw(tUmbrella,
                    umbrellaX - UMBRELLA_WIDTH/2,
                    umbrellaY - UMBRELLA_HEIGHT/2);
    
            game.font.draw(game.batch, "Score: " + ((int)score) + "", 0, HEIGHT);
    
        }
        
        if (state == State.STATE_END) {
            game.font.draw(game.batch, "You survived " + ((int)score) + " seconds\n\nTap anywhere to try again.\n\nYour goal is to protect people from rain.\n\nTouch screen to move the umbrella.", 0, HEIGHT);
    
        }
        
        game.batch.end();
    }
    
    @Override
    public void dispose() {
        
        tUmbrella.dispose();
        tGuyGood.dispose();
        tGuyNormal.dispose();
        tGuySad.dispose();
        tGuyDead.dispose();
        tTear.dispose();
        tRainbow.dispose();
        
        sBubble.dispose();
        sWaterDrop.dispose();
        sSad.dispose();
        mRain.dispose();
    }
    
    @Override
    public void show() {
        mRain.play();
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
}
