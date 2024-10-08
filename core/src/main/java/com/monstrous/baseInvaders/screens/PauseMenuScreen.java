package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.baseInvaders.Settings;


// pause menu (called from game screen on Escape key)

public class PauseMenuScreen extends MenuScreen {

    private GameScreen gameScreen;


    public PauseMenuScreen(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;


    }


    @Override
   protected void rebuild() {
       stage.clear();


       Table screenTable = new Table();
       screenTable.setFillParent(true);


       TextButton resume = new TextButton("Resume", skin);
       TextButton keys = new TextButton("Keys", skin);
       TextButton controller = new TextButton("Controller", skin);
       TextButton options = new TextButton("Options", skin);
       TextButton stop = new TextButton("Stop", skin);

       float pad = 10f;

       screenTable.add(resume).pad(pad).row();
       screenTable.add(keys).pad(pad).row();
       screenTable.add(controller).pad(pad).row();
       screenTable.add(options).pad(pad).row();
       screenTable.add(stop).pad(pad).row();

       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in


       stage.addActor(screenTable);

       // set up for keyboard/controller navigation
        if(Settings.supportControllers) {
            stage.clearFocusableActors();
            stage.addFocusableActor(resume);
            stage.addFocusableActor(keys);
            stage.addFocusableActor(controller);
            stage.addFocusableActor(options);
            stage.addFocusableActor(stop);
            stage.setFocusedActor(resume);
            super.focusActor(stage, resume);    // highlight focused actor

            stage.setEscapeActor(resume);
        }


       options.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               game.setScreen(new OptionsScreen( game, gameScreen ));
           }
       });

        keys.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSelectNoise();
                game.setScreen(new KeysScreen( game, gameScreen ));
            }
        });

        controller.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSelectNoise();
                game.setScreen(new ControllerScreen( game, gameScreen ));
            }
        });

       resume.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               game.setScreen( gameScreen );
           }
       });



       stop.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
               gameScreen.dispose();
               game.setScreen(new MainMenuScreen( game ));
           }
       });

   }

}
