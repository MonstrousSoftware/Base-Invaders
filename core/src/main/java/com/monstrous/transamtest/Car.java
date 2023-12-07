package com.monstrous.transamtest;

import com.badlogic.gdx.Gdx;
import com.github.antzGames.gdx.ode4j.ode.DHinge2Joint;
import com.monstrous.transamtest.worlddata.GameObject;
//import org.ode4j.ode.DHinge2Joint;


// captures car state and behaviour
// assumes there is only one car type

public class Car {

    public static int MAX_GEAR = 5;

    public static float MAX_RPM = 26000;
    public static float RPM_REV = 2000f;     // rpm increase per second
    public static float SHAFT_LATENCY = 10f;


    public static float[] gearRatios = { -0.2f, 0, 0.2f, 1, 3, 4, 5 };      // for testing, to tune

    public float gearRatio;
    public float driveShaftRPM;
    private CarState carState;

    public DHinge2Joint[] joints;      // 4 for 4 wheels
    public GameObject chassisObject;

    public Car(CarState carState) {
        this.carState = carState;
    }

    public void update(float deltaTime ){

        // perhaps should add automatic gear shifts....
        carState.update(deltaTime);

        gearRatio = gearRatios[carState.gear+1];     // +1 because of the reverse gear

        // have drive shaft rotation lag behind gear shifts so that the car doesn't abruptly stop when shifting to neutral
        float targetDriveshaftRPM = carState.rpm * gearRatio;
        if(targetDriveshaftRPM > driveShaftRPM)
            driveShaftRPM += SHAFT_LATENCY;
        else if (targetDriveshaftRPM < driveShaftRPM)
            driveShaftRPM -= SHAFT_LATENCY;

        updateJoints(-carState.steerAngle, 0.01f* driveShaftRPM);
    }

    private void updateJoints(float steerAngle, float wheelAngularVelocity) {
        // joints chassis-wheel
        for(int i = 0; i < 4; i ++ ) {
            DHinge2Joint j2 = joints[i];

            if( i < 2) {
                double curturn = j2.getAngle1();
                double delta = (Math.toRadians(steerAngle) - curturn);
               // Gdx.app.log("steer delta", ""+(float)curturn);
                j2.setParamVel(delta);      // ignored for non-steering wheels which are locked

            }
            j2.setParamVel2(wheelAngularVelocity);

            j2.getBody(0).enable();
            j2.getBody(1).enable();
        }
    }




}
