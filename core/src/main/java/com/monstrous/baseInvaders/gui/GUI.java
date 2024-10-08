package com.monstrous.baseInvaders.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.monstrous.baseInvaders.MyControllerMenuStage;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.behaviours.CarBehaviour;
import com.monstrous.baseInvaders.screens.Main;
import com.monstrous.baseInvaders.screens.MenuScreen;
import com.monstrous.baseInvaders.worlddata.World;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


public class GUI implements Disposable {

    private Skin skin;
    public MyControllerMenuStage stage;
    //public Stage stage;
    private World world;
    private CarBehaviour car;
    private Label rpmValue;
    private Label gearValue;
    private Label steerAngleValue;
    private Vector3 tmpVec = new Vector3();
    private float timer = 0;
    private CarSettingsWindow settingsWindow;
    private LeaderBoardWindow leaderBoardWindow;
    private Table techTable;
    public int numTechItems = 0;
    private Label timeLabel;
    private Label fpsLabel;
    private Label speedLabel;
    private Label levelCompletedLabel;
    private final StringBuffer sb;


    public GUI(Main game, CarBehaviour car, World world ) {
        Gdx.app.log("GUI constructor", "");
        this.car = car;
        this.world = world;

        skin = Main.assets.skin;
        //stage = new Stage(new ScreenViewport());
        stage = new MyControllerMenuStage(new ScreenViewport());          // we can use this even without controllers
        if(Settings.supportControllers)
            game.controllerToInputAdapter.setInputProcessor(stage); // forward controller input to stage

        sb = new StringBuffer();

        settingsWindow = new CarSettingsWindow("Car Settings", skin, world);

        leaderBoardWindow = new LeaderBoardWindow("Leader Board", skin, world, game.leaderBoard, game);
        leaderBoardWindow.setVisible(false);
    }

    private void rebuild() {
        String style = "default";

        stage.clear();
        rpmValue = new Label("", skin);
        gearValue = new Label("",skin);
        gearValue.setWidth(200);
        steerAngleValue = new Label("",skin);
        levelCompletedLabel = new Label("LEVEL COMPLETED!", skin);
        levelCompletedLabel.setVisible(false);

        Table table = new Table();
        table.top().left();               // make content move to top left
        table.setFillParent(true);        // size to match stage size


        techTable = new Table();
        stage.addActor(techTable);

        Table screenTable2 = new Table();
        screenTable2.setFillParent(true);
        timeLabel = new Label("00:00", skin);

        fpsLabel = new Label("", skin, "small");
        fpsLabel.setWidth(200);
//        speedLabel = new Label("---", skin );
        screenTable2.add().top();
        screenTable2.add();
        screenTable2.add(timeLabel).top().right().expand().row();
        //screenTable2.add(fpsTagLabel).bottom().left();
        screenTable2.add(fpsLabel).bottom().left().expandX();
        screenTable2.add(gearValue).bottom().left();
//        screenTable2.add(speedLabel).bottom().pad(80).right();

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

        Table screenTable4 = new Table();
        screenTable4.setFillParent(true);
        screenTable4.add(leaderBoardWindow);
        screenTable4.pack();
        stage.addActor(screenTable4);

    }

    public void showLevelCompleted( boolean mode ){
        levelCompletedLabel.setText("GAME COMPLETED\nYOUR TIME: "+timeLabel.getText());
        if(mode == true && !levelCompletedLabel.isVisible()){
            Gdx.input.setCursorCatched(false);
            showLeaderBoard();
        }
        levelCompletedLabel.setVisible(mode);
    }

    public void showLeaderBoard() {
        leaderBoardWindow.setModal(true);
        leaderBoardWindow.refresh();  // refresh table (but without reloading from server)
        leaderBoardWindow.setVisible(true);

        Actor actor = leaderBoardWindow.findActor("OK");
        stage.addFocusableActor(actor);
        stage.setFocusedActor(actor);
        MenuScreen.focusActor(stage, actor);
    }


    public void showCarSettings( boolean mode ){
       settingsWindow.setVisible(mode);
    }

    public void addTechIcon(boolean animate) {
        Image techIcon = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures/alienTech-64.png")))));
        techTable.addActor(techIcon);
        if(animate) {
            techIcon.addAction(sequence(moveTo(stage.getWidth() / 2, stage.getHeight() / 2, 0), scaleTo(5f, 5f, 0f),
                parallel(
                    scaleTo(1f, 1f, 1f),
                    moveTo(numTechItems * 64f, stage.getHeight() - techIcon.getHeight(), 1))));
        }
        else
            techIcon.addAction( moveTo(numTechItems * 64f, stage.getHeight() - techIcon.getHeight(), 0.1f));
        numTechItems++;
    }


    private void updateLabels() {

        if(Settings.showFPS) {
            sb.setLength(0);
            sb.append("FPS : ");
            sb.append(Gdx.graphics.getFramesPerSecond());
//            sb.append("   frame time: ");
//            sb.append((int)(1000*Gdx.graphics.getDeltaTime()));
//            sb.append(" ms");
            fpsLabel.setText(sb.toString());
        }
        else
            fpsLabel.setText("");

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

//        sb.setLength(0);
//        sb.append(world.stats.speed);
//        speedLabel.setText(sb.toString());
    }



    private void update( float deltaTime ){


        timer -= deltaTime;     // we don't need to update this every frame
        if(timer <= 0) {
            timer = 0.25f;

            if( world.stats.techCollected != numTechItems) {
                if(world.stats.techCollected == numTechItems+1)
                    addTechIcon(true);
                else {// reset? or resume after pause menu?
                    techTable.clear();
                    numTechItems = 0;
                    for (int i = 0; i < world.stats.techCollected; i++)
                        addTechIcon(false);
                }
                numTechItems = world.stats.techCollected;
            }

            updateLabels();

            rpmValue.setText((int)car.rpm);
            steerAngleValue.setText((int) car.steerAngle);

            sb.setLength(0);
            sb.append("GEAR: ");
            if(car.gear == -1)
                sb.append("R");
            else if(car.gear == 0)
                sb.append("N");
            else
                sb.append(car.gear);
            gearValue.setText(sb.toString());

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
    }

}
