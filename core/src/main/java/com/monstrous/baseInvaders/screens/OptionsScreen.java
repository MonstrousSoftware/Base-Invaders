package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerPowerLevel;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.baseInvaders.Settings;


// todo handle F11 key presses on web by reflecting the correct state of the fullscreen checkbox


public class OptionsScreen extends MenuScreen {
    private GameScreen gameScreen;    // to keep track where we were called from
    private Controller controller;

    public OptionsScreen(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        super.show();

        if(!Settings.supportControllers)
            return;

        for (Controller controller : Controllers.getControllers()) {
            Gdx.app.log("Controllers", controller.getName());
        }


        controller = Controllers.getCurrent();
        if(controller != null ) {

            Gdx.app.log("current controller", controller.getName());
            Gdx.app.log("unique id", controller.getUniqueId());
            Gdx.app.log("is connected", "" + controller.isConnected());
            ControllerPowerLevel powerLevel = controller.getPowerLevel();
            Gdx.app.log("power level", "" + powerLevel.toString());
            Gdx.app.log("can vibrate", "" + controller.canVibrate());
            if (controller.canVibrate()) {
                controller.startVibration(500, 1f);
            }
        }
        else
            Gdx.app.log("current controller", "none");
   }

   private void checkControllerChanges() {
       Controller currentController = Controllers.getCurrent();
       if(currentController != controller ) {
           controller = currentController;
           if (controller != null) {

               Gdx.app.log("current controller", controller.getName());
               Gdx.app.log("unique id", controller.getUniqueId());
               Gdx.app.log("is connected", "" + controller.isConnected());
               ControllerPowerLevel powerLevel = controller.getPowerLevel();
               Gdx.app.log("power level", "" + powerLevel.toString());
               Gdx.app.log("can vibrate", "" + controller.canVibrate());
               if (controller.canVibrate()) {
                   controller.startVibration(500, 1f);
               }
           } else
               Gdx.app.log("current controller", "none");

       }
   }

   @Override
   protected void rebuild() {
       stage.clear();

       Table screenTable = new Table();
       screenTable.setFillParent(true);


       CheckBox fullScreen = new CheckBox("Full Screen", skin);
       fullScreen.setChecked(Settings.fullScreen);

       CheckBox fps = new CheckBox("Show FPS", skin);
       fps.setChecked(Settings.showFPS);

       CheckBox shadows = new CheckBox("Shadows", skin);
       shadows.setChecked(Settings.showShadows);

       CheckBox particles = new CheckBox("Particle effects", skin);
       particles.setChecked(Settings.particleFX);

       CheckBox scenery = new CheckBox("More Scenery", skin);
       scenery.setChecked(Settings.extraScenery);

       CheckBox music = new CheckBox("Music", skin);
       music.setChecked(Settings.musicOn);

       TextButton done = new TextButton("Done", skin);


       int pad = 10;

       screenTable.add(fullScreen).pad(pad).left().row();
       screenTable.add(fps).pad(pad).left().row();
       screenTable.add(shadows).pad(pad).left().row();
       screenTable.add(particles).pad(pad).left().row();
       screenTable.add(scenery).pad(pad).left().row();
       screenTable.add(music).pad(pad).left().row();

       screenTable.add(done).pad(20).row();

       screenTable.pack();
       screenTable.validate();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(1f));           // fade in

       stage.addActor(screenTable);

       // set up for keyboard/controller navigation
       if(Settings.supportControllers) {
           // add menu items in order
           stage.clearFocusableActors();
           stage.addFocusableActor(fullScreen);
           stage.addFocusableActor(fps);
           stage.addFocusableActor(shadows);
           stage.addFocusableActor(particles);
           stage.addFocusableActor(scenery);
           stage.addFocusableActor(music);
           stage.addFocusableActor(done);
           stage.setFocusedActor(done);
           stage.setEscapeActor(done);
           super.focusActor(stage, done);    // highlight focused actor
       }

       fullScreen.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.fullScreen = fullScreen.isChecked();
               Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
               if(Settings.fullScreen)
                   Gdx.graphics.setFullscreenMode(currentMode);
               else
                   Gdx.graphics.setWindowedMode(1200, 800);         // todo
           }
       });

       fps.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.showFPS = fps.isChecked();
           }
       });



       shadows.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.showShadows = shadows.isChecked();
           }
       });

       particles.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.particleFX = particles.isChecked();
           }
       });


       scenery.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.extraScenery = scenery.isChecked();
           }
       });

       music.addListener(new ChangeListener() {
           @Override
           public void changed(ChangeEvent event, Actor actor) {
               playSelectNoise();
               Settings.musicOn = music.isChecked();
           }
       });

       done.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               if(gameScreen != null)
                game.setScreen(new PauseMenuScreen( game, gameScreen ));
               else
                game.setScreen(new MainMenuScreen( game ));
           }
       });

   }


    @Override
    public void render(float delta) {
        if(Settings.supportControllers)
            checkControllerChanges();
        super.render(delta);
    }


}
