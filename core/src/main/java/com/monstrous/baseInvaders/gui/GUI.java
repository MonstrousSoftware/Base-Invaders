package com.monstrous.baseInvaders.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.baseInvaders.input.UserCarController;
import com.monstrous.baseInvaders.worlddata.World;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


public class GUI implements Disposable {

    private Skin skin;
    public Stage stage;
    private UserCarController carController;
    private Label rpmValue;
    private Label gearValue;
    private Label steerAngleValue;
    private Vector3 tmpVec = new Vector3();
    private float timer = 0;
    private CarSettingsWindow settingsWindow;
    private Table techTable;
    private int numTechItems = 0;


    public GUI(UserCarController carController, World world ) {
        Gdx.app.log("GUI constructor", "");
        this.carController = carController;
        skin = new Skin(Gdx.files.internal("Particle Park UI Skin/Particle Park UI.json"));
        stage = new Stage(new ScreenViewport());

        settingsWindow = new CarSettingsWindow("Car Settings", skin, world);
    }

    private void rebuild() {
        String style = "window";

        stage.clear();
        rpmValue = new Label("", skin, style);
        gearValue = new Label("", skin, style);
        steerAngleValue = new Label("", skin, style);

        Table table = new Table();
        table.top().left();               // make content move to top left
        table.setFillParent(true);        // size to match stage size


        Table stats = new Table();
        stats.setBackground(skin.getDrawable("black"));
        stats.add(new Label("RPM (W/S) : ", skin, style));
        stats.add(rpmValue);
        stats.row();
        stats.add(new Label("Gear (UP/DN) :", skin, style));
        stats.add(gearValue);
        stats.row();
        stats.add(new Label("Steer angle (A/D) : ", skin, style));
        stats.add(steerAngleValue);
        stats.row();

        stats.pack();

        table.add(stats);

        stage.addActor(table);

        techTable = new Table();
        stage.addActor(techTable);


        Table screenTable = new Table();
        screenTable.setFillParent(true);
        screenTable.add(settingsWindow).top().right().expand();
        stage.addActor(screenTable);
        Image dial = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures/dial.png")))));
        stage.addActor(dial);
    }

    public void addTechIcon() {
        Image techIcon = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures/alienTech-64.png")))));
        techTable.addActor(techIcon);
        techIcon.addAction( sequence(   moveTo(stage.getWidth()/2, stage.getHeight()/2, 0), scaleTo(5f,5f,0f),
                                        parallel(
                                                scaleTo(1f,1f,1f),
                                                moveTo(numTechItems*64f, stage.getHeight()-techIcon.getHeight(), 1))));
        numTechItems++;
    }

    private void update( float deltaTime ){
        timer -= deltaTime;
        if(timer <= 0) {
            rpmValue.setText((int)carController.rpm);
            steerAngleValue.setText((int) carController.steerAngle);
            //car.transform.getTranslation(tmpVec);
            if(carController.gear == -1)
                gearValue.setText("R");
            else if(carController.gear == 0)
                gearValue.setText("N");
            else
                gearValue.setText(carController.gear);
            timer = 0.25f;
        }
    }


    public void render(float deltaTime) {
        update(deltaTime);

        stage.act(deltaTime);
        stage.draw();
    }

    public void resize(int width, int height) {
        Gdx.app.log("GUI resize", "gui " + width + " x " + height);
        stage.getViewport().update(width, height, true);
        rebuild();
    }


    @Override
    public void dispose () {
        Gdx.app.log("GUI dispose()", "");
        stage.dispose();
        skin.dispose();
    }

}
