package com.monstrous.baseInvaders;

import com.badlogic.gdx.Gdx;
import com.github.antzGames.gdx.ode4j.ode.DHinge2Joint;
import com.monstrous.baseInvaders.screens.Main;
import com.monstrous.baseInvaders.worlddata.GameObject;
//import org.ode4j.ode.DHinge2Joint;


// captures car state and behaviour
// assumes there is only one car type

public class Car {

    public static int MAX_GEAR = 5;
    public static int REVERSE_GEAR = -1;

    public static float MAX_RPM = 8000;
    public static float RPM_REV = 2000f;     // rpm increase per second
    public static float SHAFT_LATENCY = 10f;

    public static float[] gearRatios = { -1.5f, 0, 1f, 3f, 4f, 5.5f, 7f };      // for testing, to tune

    //public static float[] gearRatios = { -0.2f, 0, 0.2f, 1, 3, 4, 5 };      // for testing, to tune

    public float gearRatio;
    public float driveShaftRPM;
    private CarState carState;
    private float prevRPM = -1;
    private boolean brakeSound = false;
    private long engineId;

    public DHinge2Joint[] joints;      // 4 for 4 wheels
    public GameObject chassisObject;

    public Car(CarState carState) {
        this.carState = carState;
    }


    private void startStopSound(){
        if(carState.rpm > 0 && prevRPM == 0)
            engineId = Main.assets.sounds.ENGINE.loop();
        else if(carState.rpm == 0 && prevRPM > 0) {
            Main.assets.sounds.ENGINE.stop();
        }
        if(!brakeSound && carState.braking && carState.rpm > 0) {
            Main.assets.sounds.BRAKE.play();
            brakeSound = true;
        }
        if(carState.rpm == 0)
            brakeSound = false;
        prevRPM = carState.rpm;

        if(carState.rpm > 0) {
            Main.assets.sounds.ENGINE.setPitch(engineId, carState.rpm / 6000f  );
        }

    }

    // automatic gear shifts....
    private void checkForGearChange(){
        if(carState.rpm > 7000 && carState.gear < MAX_GEAR && carState.gear != REVERSE_GEAR) {
            carState.gear++;
            carState.rpm = 1000;
        }
        if(carState.rpm < 1000 && carState.gear > 1) {
            carState.gear--;
            carState.rpm = 7000;
        }

    }


    public void update(float deltaTime ){

        startStopSound();
        checkForGearChange();


        //carState.update(deltaTime);

        gearRatio = gearRatios[carState.gear+1];     // +1 because of the reverse gear

        // have drive shaft rotation lag behind gear shifts so that the car doesn't abruptly stop when shifting to neutral
        float targetDriveshaftRPM = carState.rpm * gearRatio;
        if(targetDriveshaftRPM > driveShaftRPM)
            driveShaftRPM += SHAFT_LATENCY;
        else if (targetDriveshaftRPM < driveShaftRPM)
            driveShaftRPM -= SHAFT_LATENCY;
        if(carState.braking)
            driveShaftRPM = targetDriveshaftRPM;

        float speed = chassisObject.body.getVelocity().len(); //?   is this local coord?
        float rollAngVel = 2*speed / ((float)Math.PI *  Settings.wheelRadius); //??

        float wav = 0.01f*driveShaftRPM;

        float steerAngle = -carState.steerAngle;
//        steerAngle *= (15f-speed)/15f;                      // reduce steer angle at high speeds
        //Gdx.app.log("speed", ""+speed);

        if(carState.braking)
            rollAngVel = 0;

        updateJoints(steerAngle, wav, rollAngVel);
    }

    private void updateJoints(float steerAngle, float wheelAngularVelocity, float rollAngVel) {
        // joints chassis-wheel
        for(int i = 0; i < 4; i ++ ) {
            DHinge2Joint j2 = joints[i];

            if( i < 2) {
                double curturn = j2.getAngle1();
                double delta = (Math.toRadians(steerAngle) - curturn);
//                double max = 20.8f;
//                if(delta > max)
//                    delta = max;
//                if(delta < -max)
//                    delta = -max;
               // Gdx.app.log("steer delta", ""+(float)curturn);
                j2.setParamVel(30f*delta);      // ignored for non-steering wheels which are locked

                // let front wheels roll and rear wheels slip
                // (doesnt provide enough traction)
                j2.setParamVel2(wheelAngularVelocity);

                //j2.setParamVel2(rollAngVel);
            }
            if(i >= 2) {

                j2.setParamVel2(wheelAngularVelocity);

            }
            j2.getBody(0).enable();
            j2.getBody(1).enable();
        }
    }




}
