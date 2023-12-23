package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.baseInvaders.Settings;
import de.golfgl.gdx.controllers.ControllerMenuStage;


public class KeysScreen extends MenuScreen {

    private String[][] keyBindings = {
        { "W", "accelerate" }, {"A", "left"},
        { "S", "brake"}, {"D", "right"},
        { "Esc", "return to menu" },
        { "Home", "restart" },
        { "L", "leaderboard" },
        { "F4", "debug view" },
        { "F9", "show fps" },
        { "F10", "toggle camera control" },
        { "F11", "toggle full screen" },
    };

    private GameScreen gameScreen;


    public KeysScreen(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
    }


    @Override
   protected void rebuild() {
       stage.clear();


       Table screenTable = new Table();
       screenTable.setFillParent(true);

       Label title = new Label("KEYS:", skin);

       TextButton continueButton = new TextButton("Continue", skin);

       float pad = 3f;
       for(int i = 0; i < keyBindings.length; i++) {
           screenTable.add(new Label(keyBindings[i][0], skin)).width(100).center().pad(pad);
           screenTable.add(new Label(keyBindings[i][1], skin)).pad(pad).left().row();
       }

       screenTable.add(continueButton).colspan(2).pad(20).row();


       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in


       stage.addActor(screenTable);

       // set up for keyboard/controller navigation
        if(Settings.supportControllers) {
            stage.clearFocusableActors();
            stage.addFocusableActor(continueButton);
            stage.setFocusedActor(continueButton);
            super.focusActor(stage, continueButton);    // highlight focused actor

            stage.setEscapeActor(continueButton);
        }

        continueButton.addListener(new ClickListener() {
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

}
