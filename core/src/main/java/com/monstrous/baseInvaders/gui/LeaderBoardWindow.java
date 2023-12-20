package com.monstrous.baseInvaders.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.monstrous.baseInvaders.leaderboard.LeaderBoardEntry;
import com.monstrous.baseInvaders.screens.Main;
import com.monstrous.baseInvaders.worlddata.World;


public class LeaderBoardWindow extends Window {

    private Array<LeaderBoardEntry> leaderBoard;
    private Skin skin;
    private Main game;
    private World world;
    private boolean[] newScore;

    public LeaderBoardWindow(String title, Skin skin, World world, Array<LeaderBoardEntry> leaderBoard, final Main game ) {
        super(title, skin);
        this.skin = skin;
        this.leaderBoard = leaderBoard;
        this.game = game;
        this.world = world;

        getTitleLabel().setAlignment(Align.center);


        newScore = new boolean[1];
    }

    public void refresh() {
        clear();

        // do we have a new score to upload?
        newScore[0] =  world != null && world.stats.gameCompleted && !world.stats.scoreSavedToServer;


        TextButton okButton = new TextButton("OKAY", skin);
        okButton.setName("OK");
        if(newScore[0])
            okButton.setText("SAVE");

        String style = "small";

        Table board = new Table();
        for(LeaderBoardEntry entry : leaderBoard ){ // we rely on leader board to have a sensible nr of entries
            board.add( new Label( entry.rank, skin, style) ).pad(10);
            board.add( new Label( entry.displayName, skin, style) ).width(120).pad(10);
            board.add( new Label( entry.score, skin, style) ).width(100).pad(10);
            board.row();
        }
        board.pack();

        TextField nameField = new TextField(game.userName, skin);
        nameField.setMaxLength(16);
        nameField.setOnlyFontChars(true);
        nameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.userName = nameField.getText();
            }
        });



        Table nameEntry = new Table();
        nameEntry.add( new Label("Your name: ", skin, style));
        nameEntry.add( nameField );
        nameEntry.pack();


        add( new Label("BEST TIMES", skin, style));
        row();
        add(board);
        row();
        if(newScore[0]) {
            add(nameEntry).pad(10);
            row();
        }
        add(okButton).width(100);
        pack();


        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if(newScore[0]) {
                    world.stats.scoreSavedToServer = true;
                    if(game.gameJolt != null ) {
                        Gdx.app.log("saving new score", game.userName + "  " + world.stats.getTimeString()+ "  score:"+ (int) world.stats.gameTime);
                        game.gameJolt.addScore(game.userName, world.stats.getTimeString(), (int) world.stats.gameTime); // send score to the server
                        game.gameJolt.getScores();          // this is asynchronous, so we cannot show the updated table now
                    }
                }
                setModal(false);
                setVisible(false);  // hide the window
                if(world != null)
                    Gdx.input.setCursorCatched(true);   // hide cursor in game screen
            }
        });

    }


}
