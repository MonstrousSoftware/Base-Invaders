package com.monstrous.baseInvaders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.baseInvaders.behaviours.CarBehaviour;

public class InstrumentView implements Disposable {
    SpriteBatch batch;
    Texture dial;
    Texture dialHandTex;
    Sprite indicatorRPM;
    Sprite indicatorSpeed;
    private int width, height;

    public InstrumentView() {
        batch = new SpriteBatch();
        dial = new Texture("textures/dial.png");
        dialHandTex = new Texture("textures/dialHand.png");
        indicatorRPM = new Sprite(dialHandTex);
        indicatorRPM.setPosition(28, 110);
        indicatorRPM.setRotation(45);
        indicatorSpeed = new Sprite(dialHandTex);
        indicatorSpeed.setPosition(width-100, 110);
        indicatorSpeed.setRotation(45);
    }

    public void render(CarBehaviour car) {
        batch.begin();

        indicatorRPM.setRotation(45-270f*car.rpm/8000f);
        indicatorSpeed.setRotation(45-270f*car.speedMPH /100f);

        batch.draw(dial, 0,0);
        batch.draw(dial, width - 256,0);
        indicatorRPM.draw(batch);
        indicatorSpeed.draw(batch);
        batch.end();
    }

    public void resize(int width, int height){
        this.width = width;
        this.height = height;
        batch.getProjectionMatrix().setToOrtho2D(0,0,width, height);
        indicatorSpeed.setPosition(width-228, 110);
    }


    @Override
    public void dispose() {
        batch.dispose();
        dial.dispose();
        dialHandTex.dispose();
    }
}
