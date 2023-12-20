package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.gui.LeaderBoardWindow;
import de.golfgl.gdx.controllers.ControllerMenuStage;


// main menu

public class MainMenuScreen extends MenuScreen {


    public MainMenuScreen(Main game) {
        super(game);
        //game.musicManager.startMusic("music/spooky-music-theme-121127.mp3", true);
    }


    @Override
    protected void rebuild() {
       stage.clear();


       Table screenTable = new Table();
       screenTable.setFillParent(true);

       Image title = new Image( (Texture)game.assets.get("images/title.png")); //new Texture( Gdx.files.internal("images/title.png")));

       TextButton play = new TextButton("Play Game", skin);
       TextButton keys = new TextButton("Keys", skin);
       TextButton options = new TextButton("Options", skin);
       TextButton scores = new TextButton("High Scores", skin);
       //TextButton credits = new TextButton("Credits", skin);
       TextButton quit = new TextButton("Quit", skin);

       float pad = 10f;
       screenTable.add(title).pad(50).row();
       screenTable.add(play).pad(pad).row();
       screenTable.add(keys).pad(pad).row();
       screenTable.add(scores).pad(pad).row();
       screenTable.add(options).pad(pad).row();
       //screenTable.add(credits).pad(pad).row();
       // hide quit on web unless we have an outro screen
       if(!(Gdx.app.getType() == Application.ApplicationType.WebGL) )
            screenTable.add(quit).pad(pad).row();

       screenTable.pack();

       screenTable.setColor(1,1,1,0);                   // set alpha to zero
       screenTable.addAction(Actions.fadeIn(3f));           // fade in
       stage.addActor(screenTable);



       play.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
             //  game.musicManager.stopMusic();
               game.setScreen(new PreGameScreen( game ));
           }
       });

       options.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSelectNoise();
                game.setScreen(new OptionsScreen( game, null ));
            }
        });

        keys.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSelectNoise();
                game.setScreen(new KeysScreen( game, null ));
            }
        });

        scores.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSelectNoise();

                // check we don't have a leader board window open yet
                for(Actor actor : stage.getActors()) {
                    if(actor.getClass().equals(LeaderBoardWindow.class)) {
                        actor.setVisible(true);             // if so make it visible
                        return;
                    }
                }
                //Skin windowSkin = game.assets.get("Particle Park UI Skin/Particle Park UI.json");
                LeaderBoardWindow leaderBoardWindow = new LeaderBoardWindow("Leader Board", skin, null, game.leaderBoard, game);
                float wx = (stage.getWidth() - leaderBoardWindow.getWidth())/2;
                float wy = (stage.getHeight() - leaderBoardWindow.getHeight())/2;
                // animate that the window drops from the top of the screen
                leaderBoardWindow.refresh();
                leaderBoardWindow.setPosition(wx,stage.getHeight());
                leaderBoardWindow.addAction(Actions.moveTo(wx, wy, .6f, Interpolation.swingOut));
                stage.addActor(leaderBoardWindow);
            }
        });


//        credits.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                super.clicked(event, x, y);
//                playSelectNoise();
//                //game.setScreen(new CreditsScreen( game ));
//            }
//        });

       quit.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               super.clicked(event, x, y);
               playSelectNoise();
              // game.musicManager.stopMusic();
//               if(!Settings.skipExitScreen)
//                    game.setScreen(new ExitScreen( game ));
//               else
                    Gdx.app.exit();
           }
       });

       // set up for keyboard/controller navigation
        if(Settings.supportControllers) {
            ControllerMenuStage cStage = (ControllerMenuStage)stage;
            cStage.clearFocusableActors();
            cStage.addFocusableActor(play);
            cStage.addFocusableActor(keys);
            cStage.addFocusableActor(options);
//            cStage.addFocusableActor(credits);
            cStage.addFocusableActor(quit);
            cStage.setFocusedActor(play);
        }
   }

}
