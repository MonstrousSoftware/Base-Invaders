package com.monstrous.baseInvaders.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.monstrous.baseInvaders.Car;
import com.monstrous.baseInvaders.Settings;

//  captures key presses and updates car control variables

// cannot extend InputAdapter because we're already extending CarController
public class UserCarController implements InputProcessor {

    public static float STEER_SPEED = 150;
    public static float MAX_STEER_ANGLE =  45;        // degrees
    public static float BRAKE_RPM_SCALE = 5f;

    // variables for export to car
    public float steerAngle;
    public float rpm;
    public boolean braking;
    public int gear;
    public boolean reversing;

    private boolean leftPressed;
    private boolean rightPressed;
    private boolean forwardPressed;
    private boolean backwardPressed;
    private int gearShift;       // -1, 0, 1, to be reset to 0 on processing

    private float stickHorizontal = 0;
    private float stickVertical = 0;
    private float stickReverse = 0;


    public UserCarController()
    {
        reset();
    }

    public void reset() {
        leftPressed = false;
        rightPressed = false;
        forwardPressed = false;
        backwardPressed = false;
        gearShift = 0;
        gear = 1;
        steerAngle = 0;
        rpm = 0;
        reversing = false;
    }

    public void update(float deltaTime) {



        // Steering
        if(leftPressed) {
            if(steerAngle<MAX_STEER_ANGLE)
                steerAngle += STEER_SPEED*deltaTime;
        }
        else if(rightPressed) {
            if (steerAngle > -MAX_STEER_ANGLE)
                steerAngle -= STEER_SPEED * deltaTime;
        }
        else //if (Math.abs(steerAngle) > 0.1f)
            steerAngle = stickHorizontal*MAX_STEER_ANGLE;
//        else
//            steerAngle -= Math.signum(steerAngle)*deltaTime*50f;

        // Accelerator
        braking = (backwardPressed && !reversing) || (forwardPressed && reversing);
        if(forwardPressed) {
            if(!reversing){
                if(rpm < Car.MAX_RPM)
                    rpm += Car.RPM_REV * deltaTime;
            }else {
                if( rpm > 0) {
                    rpm-=BRAKE_RPM_SCALE * Car.RPM_REV * deltaTime;
                }
                else {
                    gear = 1;
                    reversing = false;
                }
            }
        }
        else {
            if (backwardPressed) {
                if (reversing) {
                    if (rpm < Car.MAX_RPM)
                        rpm += Car.RPM_REV * deltaTime;
                } else {
                    if (rpm > 0)    // braking
                        rpm -= BRAKE_RPM_SCALE * Car.RPM_REV * deltaTime;
                    else {
                        gear = -1;
                        reversing = true;
                    }
                }
            } else {
                if(Math.abs(stickVertical) > 0.1f) {
                    braking = (stickVertical < 0);
                    rpm = stickVertical * Car.MAX_RPM;
                    reversing = false;
                } else if (rpm > 0) {  // coasting
                    rpm -= Car.RPM_REV * 3f * deltaTime;
                }
            }
        }
        rpm = MathUtils.clamp(rpm, 0, Car.MAX_RPM);

        if(stickReverse > 0){
            rpm = stickReverse * 3000f;
            reversing = true;
            gear = -1;
        }


        int gearShift = getGearShift();
        if(gearShift > 0  && gear < Car.MAX_GEAR)
            gear++;
        else if(gearShift < 0 && gear > -1)
            gear--;
    }



    // -1, 0, 1 : shift down, do nothing, shift up
    private int getGearShift() {
        int ret = gearShift;
        gearShift = 0;              // make sure each shift change is only reported once
        return ret;
    }

    @Override
    public boolean keyDown(int keycode) {
        return setKeyState(keycode, true);
    }

    @Override
    public boolean keyUp(int keycode) {
        return setKeyState(keycode, false);
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    private boolean setKeyState(int keycode, boolean state) {

        boolean handled = true;
        switch (keycode) {
            case Input.Keys.W:
                forwardPressed = state;
                break;
            case Input.Keys.A:
                leftPressed = state;
                break;
            case Input.Keys.S:
                backwardPressed = state;
                break;
            case Input.Keys.D:
                rightPressed = state;
                break;
            case Input.Keys.UP:
                if(state)
                    gearShift = 1;
                break;
            case Input.Keys.DOWN:
                if(state)
                    gearShift = -1;
                break;
            default:
                handled = false;    // if none of the above cases, the key press is not handled
                break;
        }
       //Gdx.app.log("key state", "WASD: "+forwardPressed+leftPressed+backwardPressed+rightPressed+gearShift);
        return handled;    // did we process the key event?
    }

    // Game controller interface
    //
    //

    // rotate view left/right
    // we only get events when the stick angle changes so once it is fully left or fully right we don't get events anymore until the stick is released.
    public void horizontalAxisMoved(float value) {       // -1 to 1

        stickHorizontal = value;
    }

    public void verticalAxisMoved(float value) {       // -1 to 1
        stickVertical = value;
        if(Settings.invertLook)
            stickVertical *= -1;
    }

    public void reverseAxisMoved(float value) {       // 0 to 1
        stickReverse = value;
    }

}
