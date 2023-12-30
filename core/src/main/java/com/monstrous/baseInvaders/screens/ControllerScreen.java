package com.monstrous.baseInvaders.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.input.ControllerConfiguration;


public class ControllerScreen extends MenuScreen  {

    private ControllerConfiguration config;
    private TextButton[] axisButtons;
    private Label[] axisLabels;
    private Label prompt;
    private Label controllerLabel;

    private GameScreen gameScreen;
    private Controller currentController;
    private int controlIndex = -1;
    private Adapter testAdapter;

    class Adapter extends ControllerAdapter {
        @Override
        public boolean axisMoved(Controller controller, int axisIndex, float value) {
            if(controlIndex < 0)
                return false;
            Gdx.app.log("controller", "axis moved: " + axisIndex + " : " + value);
             if (Math.abs(value) > 0.5f) {
                 config.sign[controlIndex] = (int) Math.signum(value);
                 prompt.setVisible(false);
                 config.axis[controlIndex] = axisIndex;
                 axisLabels[controlIndex].setText("   axis: " + axisIndex + (config.sign[controlIndex]>0?"+":"-"));
                 controlIndex = -1;
                 Controllers.removeListener(testAdapter);
                 Controllers.addListener(game.controllerToInputAdapter);
            }

            return true;
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if(currentController != null)
            controllerLabel.setText(currentController.getName());
        else
            controllerLabel.setText("None");
    }

    public ControllerScreen(Main game, GameScreen gameScreen) {
        super(game);
        this.gameScreen = gameScreen;
        config = game.controllerConfiguration;
        testAdapter = new Adapter();


        // controller
        if (Settings.supportControllers) {
            currentController = Controllers.getCurrent();
            if (currentController != null) {
                Gdx.app.log("current controller", currentController.getName());
            } else {
                Gdx.app.log("current controller", "none");
            }
        }
    }


    @Override
    protected void rebuild() {
        stage.clear();

        controllerLabel = new Label("...", skin);
        if(currentController != null)
            controllerLabel.setText(currentController.getName());
        else
            controllerLabel.setText("None");

        Table screenTable = new Table();
        screenTable.setFillParent(true);

       // Label title = new Label("CONTROLLER BINDINGS:", skin);

        screenTable.add(controllerLabel).row();

        TextButton defaultButton = new TextButton("Default Configuration", skin);

        TextButton continueButton = new TextButton("Continue", skin);

        axisButtons = new TextButton[4];
        axisLabels = new Label[4];
        prompt = new Label("Move joystick to select", skin);
        prompt.setVisible(false);

        float pad = 3f;
        for (int i = 0; i < config.axis.length; i++) {
            axisButtons[i] = new TextButton(config.keyBindings[i], skin);
            axisLabels[i] = new  Label("   axis: " + config.axis[i]+(config.sign[i]>0?"+":"-"), skin);
            screenTable.add(axisButtons[i]).width(300).center().pad(pad);
            screenTable.add(axisLabels[i]).width(50).center().pad(pad);
            screenTable.row();
        }
        screenTable.add(prompt).row();

        screenTable.add(defaultButton).colspan(2).pad(20).row();
        screenTable.add(continueButton).colspan(2).pad(20).row();


        screenTable.pack();

        screenTable.setColor(1, 1, 1, 0);                   // set alpha to zero
        screenTable.addAction(Actions.fadeIn(3f));           // fade in


        stage.addActor(screenTable);

        // set up for keyboard/controller navigation
        if (Settings.supportControllers) {
            stage.clearFocusableActors();
            for (int i = 0; i < config.axis.length; i++) {
                stage.addFocusableActor(axisButtons[i]);
            }
            stage.addFocusableActor(defaultButton);
            stage.addFocusableActor(continueButton);
            stage.setFocusedActor(continueButton);
            super.focusActor(stage, continueButton);    // highlight focused actor

            stage.setEscapeActor(continueButton);
        }

        defaultButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                playSelectNoise();
                config.resetToDefault();
                for(int i = 0; i < axisLabels.length; i++)
                    axisLabels[i].setText("   axis: " + config.axis[i] + (config.sign[i]>0?"+":"-"));

            }
        });

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

        for (int i = 0; i < config.axis.length; i++) {
            int finalI = i;
            axisButtons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    playSelectNoise();
                    prompt.setVisible(true);
                    Controllers.removeListener(game.controllerToInputAdapter);
                    Controllers.addListener(testAdapter);
                    controlIndex = finalI;
                }
            });
        }
    }
}
