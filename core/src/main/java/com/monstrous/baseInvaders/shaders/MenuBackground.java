package com.monstrous.baseInvaders.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;


// shader effect as background for menu screens

public class MenuBackground implements Disposable {

    private SpriteBatch batch;
    private float width, height;
    Array<Mover> movers;
    Array<Disposable> disposables;
    private Texture grade;

    class Mover {
        Sprite sprite;
        float vx;
        float vy;

        public Mover(Texture tex, float x, float y, float vx, float vy) {
            this.sprite = new Sprite(tex);
            this.sprite.setPosition(x,y);
            this.vx = vx;
            this.vy = vy;
        }

        public void move(float delta){
            float x = sprite.getX()+vx*delta;
            float y = sprite.getY()+vy*delta;
            if(x < 0 )
                x += width;
            if(x  > width)
                x -= width;
            if(y < 0)
                y += height;
            if(y > height)
                y-=height;
            sprite.setPosition(x, y);
        }
    }

    public MenuBackground() {

        disposables = new Array<>();
        movers = new Array<>();
        batch = new SpriteBatch();
        disposables.add(batch);
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        grade = new Texture("textures/grade.png");

        Texture tex1 = new Texture("textures/redStar.png");
        disposables.add(tex1);
        Texture tex2 = new Texture("textures/blueStar.png");
        disposables.add(tex2);
        Texture tex3 = new Texture("textures/redChevron.png");
        disposables.add(tex3);
        Texture tex4= new Texture("textures/blueBar.png");
        disposables.add(tex4);
        Texture tex5= new Texture("textures/redBar.png");
        disposables.add(tex5);
        Texture tex6= new Texture("textures/greyUFO.png");
        disposables.add(tex6);


        for(int i = 0; i < height; i+=40) {
            Mover mover = new Mover(tex4, 100, i, 0, 40);
            movers.add(mover);
        }
        for(int i = 0; i < height; i+=40) {
            Mover mover = new Mover(tex5, width-i/2, i+20, -50, 40);
            movers.add(mover);
        }

        for(int i = 0; i < width; i+=100) {
            Mover mover = new Mover(tex1, i, height / 2, 10, 0);
            movers.add(mover);
        }
        for(int i = 0; i < width; i+=100) {
            Mover mover = new Mover(tex2, i, height / 3, -10, 0);
            movers.add(mover);
        }
        for(int i = 0; i < width; i+=100) {
            Mover mover = new Mover(tex3, i, 50, 5, 0);
            movers.add(mover);
        }
        for(int i = 0; i < width; i+=width/5) {
            Mover mover = new Mover(tex6, i, height-100, 15, 0);
            movers.add(mover);
        }

    }

    public void resize (int width, int height) {
        this.width = width;
        this.height = height;
        batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);  // to ensure the fbo is rendered to the full window after a resize

    }

    private void update(float delta){
        for(Mover mover : movers)
            mover.move(delta);
    }

    public void render( float delta ) {
        update(delta);
        //ScreenUtils.clear(Color.LIGHT_GRAY);
        batch.begin();
        batch.draw(grade, 0,0, width, height);
        for(Mover mover : movers)
            mover.sprite.draw(batch);
        batch.end();

    }


    @Override
    public void dispose() {
        for(Disposable disposable : disposables)
            disposable.dispose();
    }
}
