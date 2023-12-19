package com.monstrous.baseInvaders.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.baseInvaders.Car;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.behaviours.CarBehaviour;
import com.monstrous.baseInvaders.input.UserCarController;
import com.monstrous.baseInvaders.screens.Main;
import com.monstrous.baseInvaders.worlddata.World;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


public class GUI implements Disposable {

    private Skin skin;
    public Stage stage;
    private World world;
    private CarBehaviour car;
    //private UserCarController carController;
    private Label rpmValue;
    private Label gearValue;
    private Label steerAngleValue;
    private Vector3 tmpVec = new Vector3();
    private float timer = 0;
    private CarSettingsWindow settingsWindow;
    private Table techTable;
    private int numTechItems = 0;
    private Label timeLabel;
    private Label fpsLabel;
    private Label speedLabel;
    private Label levelCompletedLabel;
    private final StringBuffer sb;


    public GUI(CarBehaviour car, World world ) {
        Gdx.app.log("GUI constructor", "");
        this.car = car;
        this.world = world;
        skin = Main.assets.skin; //Assets.new Skin(Gdx.files.internal("Particle Park UI Skin/Particle Park UI.json"));
        stage = new Stage(new ScreenViewport());
        sb = new StringBuffer();

        settingsWindow = new CarSettingsWindow("Car Settings", skin, world);
    }

    private void rebuild() {
//        String style = "window";

//        BitmapFont bitmapFont= Main.assets.uiFont;
//        Label.LabelStyle labelStyle = new Label.LabelStyle(bitmapFont, Color.BLUE);

        stage.clear();
        rpmValue = new Label("", skin);
        gearValue = new Label("",skin);
        steerAngleValue = new Label("",skin);
        levelCompletedLabel = new Label("LEVEL COMPLETED!", skin);
        levelCompletedLabel.setVisible(false);

        Table table = new Table();
        table.top().left();               // make content move to top left
        table.setFillParent(true);        // size to match stage size


//        Table stats = new Table();
//        stats.setBackground(skin.getDrawable("black"));
//        stats.add(new Label("RPM (W/S) : ", labelStyle));
//        stats.add(rpmValue);
//        stats.row();
//        stats.add(new Label("Gear (UP/DN) :", labelStyle));
//        stats.add(gearValue);
//        stats.row();
//        stats.add(new Label("Steer angle (A/D) : ", labelStyle));
//        stats.add(steerAngleValue);
//        stats.row();
//
//        stats.pack();
//
//        table.add(stats);
//        stage.addActor(table);

        techTable = new Table();
        stage.addActor(techTable);

        Table screenTable2 = new Table();
        screenTable2.setFillParent(true);
        timeLabel = new Label("00:00", skin);
        Label fpsTagLabel;
        if(Settings.showFPS)
            fpsTagLabel = new Label("FPS : ", skin, "small");
        else
            fpsTagLabel = new Label("", skin, "small");

        fpsLabel = new Label("", skin, "small");
        speedLabel = new Label("---", skin );
        screenTable2.add().top();
        screenTable2.add();
        screenTable2.add(timeLabel).top().right().expand().row();
        screenTable2.add(fpsTagLabel).bottom().left();
        screenTable2.add(fpsLabel).bottom().left().expandX();
        screenTable2.add(gearValue).bottom().left();
        screenTable2.add(speedLabel).bottom().pad(80).right();

        stage.addActor(screenTable2);

        Table screenTable3 = new Table();
        screenTable3.setFillParent(true);
        screenTable3.add(levelCompletedLabel).pad(100).bottom().expand();
        stage.addActor(screenTable3);

        Table screenTable = new Table();
        screenTable.setFillParent(true);
        screenTable.add(settingsWindow).top().right().expand();
        settingsWindow.setVisible(false);
        stage.addActor(screenTable);

    }

    public void showLevelCompleted( boolean mode ){
        levelCompletedLabel.setText("GAME COMPLETED\nYOUR TIME: "+timeLabel.getText());
        levelCompletedLabel.setVisible(mode);
    }


    public void showCarSettings( boolean mode ){
       settingsWindow.setVisible(mode);
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


    private void updateLabels() {

        if(Settings.showFPS) {
            sb.setLength(0);
            sb.append(Gdx.graphics.getFramesPerSecond());
            sb.append(" ");
            sb.append(world.stats.itemsRendered);
            fpsLabel.setText(sb.toString());
        }

        sb.setLength(0);
        int mm = (int) (world.stats.gameTime/60);
        int ss = (int)( world.stats.gameTime - 60*mm);
        if(mm <10)
            sb.append("0");
        sb.append(mm);
        sb.append(":");
        if(ss <10)
            sb.append("0");
        sb.append(ss);
        timeLabel.setText(sb.toString());

        sb.setLength(0);
        sb.append(world.stats.speed);
        speedLabel.setText(sb.toString());
    }



    private void update( float deltaTime ){
        updateLabels();

        timer -= deltaTime;
        if(timer <= 0) {
            rpmValue.setText((int)car.rpm);
            steerAngleValue.setText((int) car.steerAngle);
            //car.transform.getTranslation(tmpVec);
            if(car.gear == -1)
                gearValue.setText("R");
            else if(car.gear == 0)
                gearValue.setText("N");
            else
                gearValue.setText(car.gear);
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
        //skin.dispose();
    }

}
