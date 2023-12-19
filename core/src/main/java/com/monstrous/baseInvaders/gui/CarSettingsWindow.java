package com.monstrous.baseInvaders.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.worlddata.Populator;
import com.monstrous.baseInvaders.worlddata.World;

public class CarSettingsWindow extends Window {


    public CarSettingsWindow(String title, Skin skin, World world) {
        super(title, skin);
        String style= "small";

        Gdx.input.setCursorCatched(false);

        final Label labelMu = new Label(String.valueOf(Settings.mu), skin, style);
        final Slider sliderMu = new Slider(0, 3, 0.1f, false, skin);
        sliderMu.setAnimateDuration(0.1f);
        sliderMu.setValue(Settings.mu);
        sliderMu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.mu = sliderMu.getValue();
                labelMu.setText(String.valueOf(Settings.mu));
                //world.rebuild();
            }
        });
        add(new Label("Mu: ", skin, style)).pad(5);
        add(sliderMu);
        add(labelMu).width(50);
        row();

        final Label labelMu2 = new Label(String.valueOf(Settings.mu2), skin, style);
        final Slider sliderMu2 = new Slider(0, 3, 0.1f, false, skin);
        sliderMu2.setAnimateDuration(0.1f);
        sliderMu2.setValue(Settings.mu);
        sliderMu2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.mu2 = sliderMu2.getValue();
                labelMu2.setText(String.valueOf(Settings.mu2));
                //world.rebuild();
            }
        });
        add(new Label("Mu2: ", skin, style)).pad(5);
        add(sliderMu2);
        add(labelMu2).width(50);
        row();

        final Label labelSlip1 = new Label(String.valueOf(Settings.slip1), skin, style);
        final Slider sliderSlip1 = new Slider(0, 3, 0.1f, false, skin);
        sliderSlip1.setAnimateDuration(0.1f);
        sliderSlip1.setValue(Settings.slip1);
        sliderSlip1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.slip1 = sliderSlip1.getValue();
                labelSlip1.setText(String.valueOf(Settings.slip1));
                //world.rebuild();
            }
        });
        add(new Label("Slip1: ", skin, style)).pad(5);
        add(sliderSlip1);
        add(labelSlip1).width(50);
        row();

        final Label labelSlip2 = new Label(String.valueOf(Settings.slip2), skin, style);
        final Slider sliderSlip2 = new Slider(0, 3, 0.1f, false, skin);
        sliderSlip2.setAnimateDuration(0.1f);
        sliderSlip2.setValue(Settings.slip2);
        sliderSlip2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.slip2 = sliderSlip2.getValue();
                labelSlip2.setText(String.valueOf(Settings.slip2));
                //world.rebuild();
            }
        });
        add(new Label("Slip2: ", skin, style)).pad(5);
        add(sliderSlip2);
        add(labelSlip2).width(50);
        row();
//
//        final Label labelWheelDownValue = new Label(String.valueOf(Settings.wheelDown), skin);
//        final Slider sliderWD = new Slider(-3f, 0f, 0.1f, false, skin);
//        sliderWD.setAnimateDuration(0.1f);
//        sliderWD.setValue(Settings.wheelDown);
//        sliderWD.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Settings.wheelDown = sliderWD.getValue();
//                labelWheelDownValue.setText(String.valueOf(Settings.wheelDown));
//                world.rebuild();
//            }
//        });
//        add(new Label("Suspension length: ", skin)).pad(5);
//        add(sliderWD);
//        add(labelWheelDownValue).width(50);
//        row();
//
//
//        final Label labelCarMassValue = new Label(String.valueOf(Settings.chassisMass), skin);
//        final Slider sliderCarMass = new Slider(1, 1500, 1, false, skin);
//        sliderCarMass.setAnimateDuration(0.1f);
//        sliderCarMass.setValue(Settings.chassisMass);
//        sliderCarMass.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Settings.chassisMass = sliderCarMass.getValue();
//                labelCarMassValue.setText(String.valueOf(Settings.chassisMass));
//                //world.rebuild();
//            }
//        });
//
//
//        add(new Label("Mass: ", skin)).pad(5);
//        add(sliderCarMass);
//        add(labelCarMassValue).width(50);
//        row();
//
        final Label labelStiffnessValue = new Label(String.valueOf(Settings.suspensionCFM), skin, style);
        final Slider sliderStiffness = new Slider(0, 1, 0.01f, false, skin);
        sliderStiffness.setAnimateDuration(0.1f);
        sliderStiffness.setValue(Settings.suspensionCFM);
        sliderStiffness.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.suspensionCFM = sliderStiffness.getValue();
                labelStiffnessValue.setText(String.valueOf(Settings.suspensionCFM));
                //world.rebuild();
            }
        });


        add(new Label("Suspension CFM: ", skin, style)).pad(5);
        add(sliderStiffness);
        add(labelStiffnessValue).width(50);
        row();

        final Label labelDampingValue = new Label(String.valueOf(Settings.suspensionERP), skin, style);
        final Slider sliderDamping = new Slider(0, 1, 0.01f, false, skin);
        sliderDamping.setAnimateDuration(0.1f);
        sliderDamping.setValue(Settings.suspensionERP);
        sliderDamping.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.suspensionERP = sliderDamping.getValue();
                labelDampingValue.setText(String.valueOf(Settings.suspensionERP));
                //world.rebuild();
            }
        });


        add(new Label("Suspension ERP: ", skin, style)).pad(5);
        add(sliderDamping);
        add(labelDampingValue).width(50);
        row();

        final Label labelCD = new Label(String.valueOf(Settings.chassisDensity), skin, style);
        final Slider sliderCD = new Slider(0, 3, 0.1f, false, skin);
        sliderCD.setAnimateDuration(0.1f);
        sliderCD.setValue(Settings.chassisDensity);
        sliderCD.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.chassisDensity = sliderCD.getValue();
                labelCD.setText(String.valueOf(Settings.chassisDensity));
            }
        });
        add(new Label("Chassis density: ", skin, style)).pad(5);
        add(sliderCD);
        add(labelCD).width(50);
        row();

        final Label labelWD = new Label(String.valueOf(Settings.wheelDensity), skin, style);
        final Slider sliderWD = new Slider(0, 3, 0.1f, false, skin);
        sliderWD.setAnimateDuration(0.1f);
        sliderWD.setValue(Settings.wheelDensity);
        sliderWD.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Settings.wheelDensity = sliderWD.getValue();
                labelWD.setText(String.valueOf(Settings.wheelDensity));
            }
        });
        add(new Label("Wheel density: ", skin, style)).pad(5);
        add(sliderWD);
        add(labelWD).width(50);
        row();

//        final Label labelCompressionValue = new Label(String.valueOf(Settings.suspensionCompression), skin);
//        final Slider sliderCompression = new Slider(0, 50, 1, false, skin);
//        sliderCompression.setAnimateDuration(0.1f);
//        sliderCompression.setValue(Settings.suspensionCompression);
//        sliderCompression.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Settings.suspensionCompression = sliderCompression.getValue();
//                labelCompressionValue.setText(String.valueOf(Settings.suspensionCompression));
//                world.rebuild();
//            }
//        });
//
//
//        add(new Label("Compression: ", skin)).pad(5);
//        add(sliderCompression);
//        add(labelCompressionValue).width(50);
//        row();
//
//        final Label labelRestValue = new Label(String.valueOf(Settings.suspensionRestLength), skin);
//        final Slider sliderRest = new Slider(0.1f, 2.0f, 0.1f, false, skin);
//        sliderRest.setAnimateDuration(0.1f);
//        sliderRest.setValue(Settings.suspensionRestLength);
//        sliderRest.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Settings.suspensionRestLength = sliderRest.getValue();
//                labelRestValue.setText(String.valueOf(Settings.suspensionRestLength));
//                world.rebuild();
//            }
//        });
//
//
//        add(new Label("Rest Length: ", skin)).pad(5);
//        add(sliderRest);
//        add(labelRestValue).width(50);
//        row();

//        final Label labelEngineForceValue = new Label(String.valueOf(Settings.engineForce), skin);
//        final Slider sliderEngineForce = new Slider(1, 100, 1, false, skin);
//        sliderEngineForce.setAnimateDuration(0.1f);
//        sliderEngineForce.setValue(Settings.engineForce);
//        sliderEngineForce.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Settings.engineForce = sliderEngineForce.getValue();
//                labelEngineForceValue.setText(String.valueOf(Settings.engineForce));
//            }
//        });


//        add(new Label("Engine Force: ", skin)).pad(5);
//        add(sliderEngineForce);
//        add(labelEngineForceValue).width(50);
//        row();
//
//
//        CheckBox cb = new CheckBox("Debug view", skin);
//        cb.setChecked(world.showDebug);
//        cb.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                world.showDebug = cb.isChecked();
//            }
//        });
//
//
//        add(cb).colspan(3).pad(5);
//        row();

        Button reset = new TextButton("Reset", skin);
        reset.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
               // world.rebuild();
                Populator.populate(world);
                Gdx.app.log("Reset", "");
            }
        });
        add(reset).colspan(3).pad(5);
        row();

        pack();

    }
}
