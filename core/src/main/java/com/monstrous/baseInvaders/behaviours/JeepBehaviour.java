package com.monstrous.baseInvaders.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.github.antzGames.gdx.ode4j.math.DVector3C;
import com.github.antzGames.gdx.ode4j.ode.*;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.input.UserCarController;
import com.monstrous.baseInvaders.physics.PhysicsBody;
import com.monstrous.baseInvaders.physics.PhysicsWorld;
import com.monstrous.baseInvaders.screens.Main;
import com.monstrous.baseInvaders.worlddata.GameObject;
import com.monstrous.baseInvaders.worlddata.World;

import static com.github.antzGames.gdx.ode4j.ode.OdeConstants.*;

// enemy car behaviour
// (lots of overlap with CarBehaviour)

public class JeepBehaviour extends Behaviour {

    public final static float SPOTTING_DISTANCE = 100f;
    public final static float LOSE_DISTANCE = 200f;

    public static int MAX_GEAR = 5;
    public static int REVERSE_GEAR = -1;
    public static int NEUTRAL_GEAR = 0;

    public static float MAX_RPM = 8000;
    public static float RPM_REV = 2000f;     // rpm increase per second
    public static float SHAFT_LATENCY = 100f;

    public static float[] gearRatios = { -3f, 0, 3f, 2f, 1.5f, 1f, 0.5f };      // for testing, to tune

    public int gear; // -1, 0, 1, 2, 3, ... MAX_GEAR
    public int nextGear;
    public float steerAngle;
    public float rpm;
    public boolean braking;

    public float gearRatio;
    public float driveShaftRPM;
    public float speedMPH;
    //private CarState carState;
    private float prevRPM = -1;
    private boolean brakeSound = false;
    private long engineId;
    private boolean shiftingUp = false;
    private boolean shiftingDown = false;
    private float shiftRPM;

    public DHinge2Joint[] joints;      // 4 for 4 wheels


    public JeepBehaviour(GameObject go) {
        super(go);
    }


    // automatic gear shifts....
    private void checkForGearChange( float deltaTime ){
        if(shiftingUp){
            if(rpm <= shiftRPM){
                //gear = nextGear;
                shiftingUp = false;
            }
            else
                rpm -= 8000*deltaTime;
        }
        else if(shiftingDown){
            if(rpm >= shiftRPM){
                //gear = nextGear;
                shiftingDown = false;
            }
            else
                rpm += 8000*deltaTime;
        }
        else if(rpm > 7000 && gear < MAX_GEAR && gear != REVERSE_GEAR && !shiftingUp) {
            shiftingUp = true;
            nextGear = gear+1;
            shiftRPM = rpm * gearRatios[nextGear+1] / gearRatios[gear+1];
            gear = nextGear;//NEUTRAL_GEAR;
        }
        else if(rpm < 1000 && gear > 1 && !shiftingDown) {
            shiftingDown = true;
            nextGear = gear-1;
            shiftRPM = rpm * gearRatios[nextGear+1] / gearRatios[gear+1];
            gear = nextGear;
        }

    }

    private Vector3 v = new Vector3();

    private int mode = WAITING;
    private float timer = 5f;
    private float previousDistance;
    private Vector3 target = new Vector3();
    private Vector3 dir = new Vector3();
    private Vector3 targetDirection = new Vector3();
    private Vector3 vTmp = new Vector3();

    private final static int WAITING = 0;
    private final static int MOVING = 1;
    private final static int TARGETING = 2;

    public void update(World world, float deltaTime ) {

        if((mode== WAITING || mode == MOVING) && go.getPosition().dst(world.getPlayer().getPosition()) < SPOTTING_DISTANCE){
            mode = TARGETING;
            braking = false;
            gear = 1;
            Gdx.app.log("jeep state", "-> following player");
        }

        if(mode == WAITING){
            timer -= deltaTime;
            if(timer < 0){
                float x = (float) (Math.random())*0.5f*Settings.worldSize;    // not too close to the edge
                float z = (float) (Math.random())*0.5f*Settings.worldSize;
                target.set(x, 0, z);    // ignore Y
                mode = MOVING;
                braking = false;
                gear = 1;
                previousDistance = 9999f;
                //Gdx.app.log("jeep state", "-> moving to target "+target);
            }
        }
        if(mode == MOVING) {

            if(rpm < 4000)
                rpm+= deltaTime * 1000f;
            Vector3 pos = go.getPosition();
            target.y = pos.y;
            float distance = target.dst(pos);
            targetDirection.set(target).sub(pos).nor();
            dir.set(go.getDirection());
            vTmp.x = dir.z;     // rotate by 90 degrees
            vTmp.z = -dir.x;
            float dot = targetDirection.dot(vTmp);
            steerAngle = dot*45f;
            //Gdx.app.log("jeep dot", ""+dot+"position: "+pos+" distance: "+distance);
            if(distance < 20f ) {// || distance > previousDistance){
                rpm = 0;
                braking = true;
                mode = WAITING;
                //Gdx.app.log("jeep state", "-> waiting");
                timer = 8f;
            }
            previousDistance = distance;
        }
        if(mode == TARGETING) {

            if(rpm < 6000)
                rpm+= deltaTime * 1000f;
            Vector3 pos = go.getPosition();
            target.set(world.getPlayer().getPosition());
            target.y = pos.y;
            float distance = target.dst(pos);

            targetDirection.set(target).sub(pos).nor();
            dir.set(go.getDirection());
            vTmp.x = dir.z;     // rotate by 90 degrees
            vTmp.z = -dir.x;
            float dot = targetDirection.dot(vTmp);
            steerAngle = dot*45f;
            //Gdx.app.log("jeep dot", ""+dot+"position: "+pos+" distance: "+distance);
            if (distance > LOSE_DISTANCE){
                rpm = 0;
                braking = true;
                mode = WAITING;
                Gdx.app.log("jeep state", "lost track of player -> waiting");
                timer = 8f;
            }
            previousDistance = distance;
        }
        updateCarStuff(deltaTime);
    }

    private void updateCarStuff(float deltaTime) {

        checkForGearChange(deltaTime);

        gearRatio = gearRatios[gear+1];     // +1 because of the reverse gear

        // have drive shaft rotation lag behind gear shifts so that the car doesn't abruptly stop when shifting to neutral
        float targetDriveshaftRPM = rpm/gearRatio;


        if(targetDriveshaftRPM > driveShaftRPM)
            driveShaftRPM += SHAFT_LATENCY;
        else if (targetDriveshaftRPM < driveShaftRPM)
            driveShaftRPM -= SHAFT_LATENCY;
        if(braking)
            driveShaftRPM = targetDriveshaftRPM;

        v.set(go.body.getVelocity());

        float speed = v.dot(go.direction);

        float rollAngVel = 2*speed / ((float)Math.PI *  Settings.wheelRadius); //??

        float wav = 0.01f*driveShaftRPM;

        if(braking)
            wav = 0;

        updateJoints(-steerAngle, wav, rollAngVel);

        speedMPH = speed*2.23f;  // m/s to miles/h
    }

    private void updateJoints(float steerAngle, float wheelAngularVelocity, float rollAngVel) {
        // joints chassis-wheel
        for(int i = 0; i < 4; i ++ ) {
            DHinge2Joint j2 = joints[i];

            if( i < 2) {
                double curturn = j2.getAngle1();
                double delta = (Math.toRadians(steerAngle) - curturn);
                j2.setParamVel(30f*delta);      // ignored for non-steering wheels which are locked

                // let front wheels roll and rear wheels slip
                // (doesnt provide enough traction)
                j2.setParamVel2(wheelAngularVelocity);

                //j2.setParamVel2(rollAngVel);
            }
            if(i >= 2) {
                j2.setParamVel(0);
                j2.setParamVel2(wheelAngularVelocity);

            }
        }
    }


    private void addCounterWeight(PhysicsWorld physicsWorld, GameObject chassis) {
        DMass massInfo = OdeHelper.createMass();
        DBody weightBody = OdeHelper.createBody(physicsWorld.world);
        massInfo.setSphere(100,0.2f);
        massInfo.adjust(10);
        weightBody.setMass(massInfo);
        weightBody.enable();
        Vector3 pos = chassis.getPosition();
        weightBody.setPosition(pos.x, pos.y-.5f, pos.z+1f);   // put weight below the chassis
        weightBody.setAutoDisableFlag(false);
        weightBody.setGravityMode(true);
        weightBody.setDamping(0.01, 0.1);


        DFixedJoint joint = OdeHelper.createFixedJoint(physicsWorld.world);    // add joint to the world
        joint.attach(chassis.body.geom.getBody(), weightBody);


        //Gdx.app.log("car mass", "chassis: "+chassis.body.geom.getBody().getMass()+" antirollmass:"+weightBody.getMass());
    }

    public void connectWheels(PhysicsWorld physicsWorld, GameObject chassis, GameObject w0, GameObject w1, GameObject w2, GameObject w3 ) {

        addCounterWeight(physicsWorld, chassis);

        joints =new DHinge2Joint[4];
        joints[0]=makeWheelJoint(physicsWorld, chassis.body, w0.body,true);
        joints[1]=makeWheelJoint(physicsWorld, chassis.body, w1.body,true);
        joints[2]=makeWheelJoint(physicsWorld, chassis.body, w2.body,false);
        joints[3]=makeWheelJoint(physicsWorld, chassis.body, w3.body,false);
        //chassisObject =chassis;
        chassis.body.geom.getBody().setAutoDisableFlag(false);

        // define surface properties for front and rear tyres
        // mu2 is "grip" in forward direction
        // mu in sideways direction
        DContact.DSurfaceParameters frontSurface = new DContact.DSurfaceParameters();
        frontSurface.mode = dContactFDir1 |  dContactMu2 | dContactSlip1 | dContactSlip2 | dContactSoftERP | dContactSoftCFM;// | dContactApprox1;
        frontSurface.mu = Settings.mu;
        frontSurface.mu2 = Settings.mu2;
        frontSurface.slip1 = Settings.slip1;
        frontSurface.slip2 = Settings.slip2;
        frontSurface.soft_erp = 0.8;
        frontSurface.soft_cfm = 0.01;

        w0.setSurface(frontSurface);
        w1.setSurface(frontSurface);

        DContact.DSurfaceParameters backSurface = new DContact.DSurfaceParameters();
        backSurface.mode = dContactFDir1 |  dContactMu2 | dContactSlip1 | dContactSlip2 | dContactSoftERP | dContactSoftCFM;// | dContactApprox1;
        backSurface.mu = Settings.mu;
        backSurface.mu2 = Settings.mu2;
        backSurface.slip1 = Settings.slip1;
        backSurface.slip2 = Settings.slip2;
        backSurface.soft_erp = 0.8;
        backSurface.soft_cfm = 0.01;

        w2.setSurface(backSurface);
        w3.setSurface(backSurface);
    }


    public DHinge2Joint makeWheelJoint(PhysicsWorld physicsWorld, PhysicsBody chassis, PhysicsBody wheel, boolean steering ){

        // hinge2joints for wheels
        DHinge2Joint joint = OdeHelper.createHinge2Joint(physicsWorld.world);    // add joint to the world
        DVector3C anchor = wheel.geom.getBody().getPosition();
        joint.attach(chassis.geom.getBody(), wheel.geom.getBody());


        //Gdx.app.log("anchor", anchor.toString());
        joint.setAnchor(anchor);

        joint.setAxis1(0, 1, 0);      // up axis for steering
        joint.setAxis2(-1, 0, 0);    // roll axis for rolling


        joint.setParamVel2(0);
        joint.setParamFMax2(15000f);
        joint.setParamFMax(150000f);
        joint.setParamFudgeFactor(0.1f);
        joint.setParamSuspensionERP(Settings.suspensionERP);
        joint.setParamSuspensionCFM(Settings.suspensionCFM);

        if(!steering) { // rear wheel?

            joint.setParam(DJoint.PARAM_N.dParamLoStop1, 0);            // put a stop at max steering angle
            joint.setParam(DJoint.PARAM_N.dParamHiStop1, 0);             // idem
        } // don't put stops on steering wheels but rely on the car controller input for this

        wheel.geom.getBody().setAutoDisableFlag(false);
        return joint;
    }



}
