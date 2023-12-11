package com.monstrous.transamtest.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.monstrous.transamtest.Car;
import com.monstrous.transamtest.CarState;

//  captures key presses and updates car control variables

// cannot extend InputAdapter because we're already extending CarController
public class UserCarController extends CarState implements InputProcessor {

    public static float STEER_SPEED = 150;
    public static float MAX_STEER_ANGLE =  45;        // degrees
    public static float BRAKE_RPM_SCALE = 5f;

    private boolean leftPressed;
    private boolean rightPressed;
    private boolean forwardPressed;
    private boolean backwardPressed;
    private int gearShift;       // -1, 0, 1, to be reset to 0 on processing


    public UserCarController() {
        reset();
    }

    public void reset() {
        leftPressed = false;
        rightPressed = false;
        forwardPressed = false;
        backwardPressed = false;
        gearShift = 0;
        gear = 1;
        steerAngle = MAX_STEER_ANGLE;
        rpm = 0;
    }

    public void update(float deltaTime) {

        // Steering
        if(leftPressed &&steerAngle<MAX_STEER_ANGLE)
        {
            steerAngle += STEER_SPEED*deltaTime;
        }
        if(rightPressed &&steerAngle  >-MAX_STEER_ANGLE)
        {
            steerAngle -= STEER_SPEED*deltaTime;
        }
        // Accelerator
        braking = backwardPressed;
        if(forwardPressed && rpm < Car.MAX_RPM) {
            rpm += Car.RPM_REV * deltaTime;
        }
        else if(backwardPressed && rpm > 0) {   // braking
            rpm-=BRAKE_RPM_SCALE * Car.RPM_REV * deltaTime;
        }
        else if(rpm > 0) {  // coasting
            rpm-= Car.RPM_REV * 3f * deltaTime;
        }
        rpm = MathUtils.clamp(rpm, 0, Car.MAX_RPM);


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
}
