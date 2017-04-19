package com.team14.game.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.team14.game.BubbleAdventure;
import com.team14.game.sprites.BottomObstacle;
import com.team14.game.sprites.Bubble;
import com.team14.game.sprites.JunkFood;
import com.team14.game.sprites.TopObstacle;
import com.team14.game.sprites.Vegetable;

/**
 * Created by Arianne on 3/26/17.
 */

public class PlayState extends State {
    private static final int VEGETABLE_SPACING = 150; //space between each vegetable
    private static final int VEGETABLE_COUNT = 10;
    private static final int JUNK_COUNT = 3;
    private static final int JUNK_SPACING = 150;

    private static final int OBSTACLE_SPACING = 200;
    private static final int OBSTACLE_COUNT = 1;
    //keeps track of whether or not the last state of the character was large or not. Used to determine whether score increments
    private boolean wasBig = false;

    private boolean gameover;
    private Texture gameoverImg;


    private Bubble bubble;
    private Texture bg;

    private Array<Vegetable> vegetables;
    private Array<JunkFood> junkFoods;
    private Array<TopObstacle> topObstacles;
    private Array<BottomObstacle> bottomObstacles;

    private String scoreString;
    private BitmapFont scoreFont;

    /*Constructor*/
    public PlayState(GameStateManager gsm) {
        super(gsm);

        bubble = new Bubble(50, 150);
        cam.setToOrtho(false, BubbleAdventure.WIDTH / 2, BubbleAdventure.HEIGHT / 2); //sets view on screen to a certain part of Game World
        bg = new Texture("bg1.jpg");
        gameoverImg = new Texture("GameOver.png");

        // this part only instantiates the first vegetables, the rest are reused and repositioned in update
        vegetables = new Array<Vegetable>();
        for(int i = 1; i <= VEGETABLE_COUNT; i++){
            vegetables.add(new Vegetable(i * (VEGETABLE_SPACING + Vegetable.VEGETABLE_WIDTH) + BubbleAdventure.WIDTH / 2));
        }

        junkFoods = new Array<JunkFood>();

        for(int i = 1; i <= JUNK_COUNT; i++){
            junkFoods.add(new JunkFood(i * (JUNK_SPACING + JunkFood.JF_WIDTH) + BubbleAdventure.WIDTH / 2));
        }

        topObstacles= new Array<TopObstacle>();

        // this part only instantiates the first obstacle, the rest are done in reposition
        for(int i = 1; i <= OBSTACLE_COUNT; i++){
            topObstacles.add(new TopObstacle(i * (OBSTACLE_SPACING + TopObstacle.OBSTACLE_WIDTH)));
        }

        bottomObstacles= new Array<BottomObstacle>();

        // this part only instantiates the first obstacle, the rest are done in reposition
        for(int i = 1; i <= OBSTACLE_COUNT; i++){
            bottomObstacles.add(new BottomObstacle(i * BottomObstacle.OBSTACLE_WIDTH));
        }

        //construct score string and font
        scoreString = "Score: 0";
        scoreFont = new BitmapFont();

        gameover = false;


    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()){
            if(gameover) {
                BubbleAdventure.resetScore();//reset score to 0 before returning
                gsm.set(new StartState(gsm));
            }
            else
                bubble.jump();
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        bubble.update(dt);
        cam.position.x = bubble.getPosition().x + 80;

        for(Vegetable vegetable : vegetables){
            if(cam.position.x - (cam.viewportWidth / 2) > vegetable.getPosVegetable().x + vegetable.getVegetable().getWidth()) {
                vegetable.reposition(vegetable.getPosVegetable().x + ((Vegetable.VEGETABLE_WIDTH + VEGETABLE_SPACING) * VEGETABLE_COUNT));
                vegetable.resetLock();//resets the lock on the object once it is out of view.
            }
            if(vegetable.collides(bubble.getBounds())) {
                if(!wasBig)//if character wasn't big previously gains points
                {
                    BubbleAdventure.increment();
                    scoreString = "Score: " + BubbleAdventure.getScore();//update string with now score value
                }
                bubble.shrink((int) bubble.getPosition().x,(int)bubble.getPosition().y);
                wasBig = false;//last state updated for character
            }
        }

        for(JunkFood junk : junkFoods){
            if(cam.position.x - (cam.viewportWidth / 2) > junk.getPosJunk().x + junk.getJunk().getWidth())
                junk.reposition(junk.getPosJunk().x + ((JunkFood.JF_WIDTH + JUNK_SPACING) * JUNK_COUNT));

            if(junk.collides(bubble.getBounds())) {
                bubble.grow((int) bubble.getPosition().x, (int) bubble.getPosition().y);
                wasBig = true;//state of the character has changed. will not be able to score until small again
            }
        }


        for(TopObstacle topObstacle : topObstacles) {
            if(cam.position.x - (cam.viewportWidth / 2) > topObstacle.getPosTop().x + topObstacle.getTopObstacle().getWidth())
                topObstacle.reposition(topObstacle.getPosTop().x + ((TopObstacle.OBSTACLE_WIDTH + OBSTACLE_SPACING) * OBSTACLE_COUNT));

            if(topObstacle.collides(bubble.getBounds())) {
                bubble.colliding = true;
                gameover = true;
            }

        }


        for(BottomObstacle bottomObstacle : bottomObstacles) {
            if(cam.position.x - (cam.viewportWidth / 2) > bottomObstacle.getPosBottom().x + bottomObstacle.getBottomObstacle().getWidth())
                bottomObstacle.reposition(bottomObstacle.getPosBottom().x + ((BottomObstacle.OBSTACLE_WIDTH) * OBSTACLE_COUNT));

            if(bottomObstacle.collides(bubble.getBounds())) {
                bubble.colliding = true;
                gameover = true;
            }

        }

        cam.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined); //draws in relation to what we're viewing in game world, where in the game world we are
        sb.begin();
        sb.draw(bg, cam.position.x - (cam.viewportWidth / 2), 0); //positions background picture to fit phone screen
        sb.draw(bubble.getTexture(), bubble.getPosition().x, bubble.getPosition().y);
        /*Rendering score output*/
        scoreFont.setColor(Color.GOLD);//can also input rgb values
        scoreFont.draw(sb, scoreString, cam.position.x - cam.viewportWidth/2, cam.viewportHeight);
        scoreFont.setUseIntegerPositions(false);//fixes shaking of score display

        for(Vegetable vegetable : vegetables)
            sb.draw(vegetable.getVegetable(), vegetable.getPosVegetable().x, vegetable.getPosVegetable().y);


        for(JunkFood junk : junkFoods) {
            sb.draw(junk.getJunk(), junk.getPosJunk().x, junk.getPosJunk().y);
        }

        for(TopObstacle topObstacle : topObstacles) {
            sb.draw(topObstacle.getTopObstacle(), topObstacle.getPosTop().x, topObstacle.getPosTop().y);
        }

        for(BottomObstacle bottomObstacle : bottomObstacles) {
            sb.draw(bottomObstacle.getBottomObstacle(), bottomObstacle.getPosBottom().x, bottomObstacle.getPosBottom().y);
        }

        if(gameover)
            sb.draw(gameoverImg, cam.position.x - gameoverImg.getWidth() / 2, cam.position.y);


        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        bubble.dispose();

    }
}
