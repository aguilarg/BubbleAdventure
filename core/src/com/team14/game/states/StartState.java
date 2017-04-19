package com.team14.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.team14.game.BubbleAdventure;

/**
 * Created by Arianne on 3/31/17.
 * State between Menu and Play, freezes the bubble to prep user for tapping
 */

public class StartState extends State {

    private Texture startBubble, bg, tap;

    public StartState(GameStateManager gsm) {
        super(gsm);
        cam.setToOrtho(false, BubbleAdventure.WIDTH / 2, BubbleAdventure.HEIGHT / 2);
        bg = new Texture("bg1.jpg");
        tap = new Texture("Tap.png");
        startBubble = new Texture("SmallBubble.png");

    }

    //If player taps screen, transition to play state
    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
            gsm.set(new PlayState(gsm));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg, 0,0);
        sb.draw(tap, 50, 200); //should probably not hard code will deal wid it l8r
        sb.draw(startBubble, 50, 150);
        sb.end();
    }

    @Override
    public void dispose() {
        startBubble.dispose();
        bg.dispose();
        tap.dispose();
    }
}
