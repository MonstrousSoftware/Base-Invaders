package com.monstrous.baseInvaders.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.monstrous.baseInvaders.screens.Main;


// to handle game controllers
// relays events to camera controller


public class MyControllerAdapter extends ControllerAdapter {
    private UserCarController carController;

    public MyControllerAdapter(UserCarController camController) {
        super();
        this.carController = camController;
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonIndex) {
        Gdx.app.log("controller", "button down: "+buttonIndex);

        // map Dpad to WASD
        if(buttonIndex == controller.getMapping().buttonDpadUp)
            carController.keyDown(Input.Keys.W);
        if(buttonIndex == controller.getMapping().buttonDpadDown)
            carController.keyDown(Input.Keys.S);
        if(buttonIndex == controller.getMapping().buttonDpadLeft)
            carController.keyDown(Input.Keys.A);
        if(buttonIndex == controller.getMapping().buttonDpadRight)
            carController.keyDown(Input.Keys.D);

        if(buttonIndex == controller.getMapping().buttonL1) // jump
            carController.keyDown(Input.Keys.UP);
        if(buttonIndex == controller.getMapping().buttonR1) // crouch
            carController.keyDown(Input.Keys.UP);
        return super.buttonDown(controller, buttonIndex);
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonIndex) {
        //Gdx.app.log("controller", "button up: "+buttonIndex);

        // map Dpad to WASD
        if(buttonIndex == controller.getMapping().buttonDpadUp)
            carController.keyUp(Input.Keys.W);
        if(buttonIndex == controller.getMapping().buttonDpadDown)
            carController.keyUp(Input.Keys.S);
        if(buttonIndex == controller.getMapping().buttonDpadLeft)
            carController.keyUp(Input.Keys.A);
        if(buttonIndex == controller.getMapping().buttonDpadRight)
            carController.keyUp(Input.Keys.D);

        if(buttonIndex == controller.getMapping().buttonL1)
            carController.keyUp(Input.Keys.UP);
        if(buttonIndex == controller.getMapping().buttonR1)
            carController.keyUp(Input.Keys.DOWN);
        return super.buttonUp(controller, buttonIndex);
    }

    @Override
    public boolean axisMoved(Controller controller, int axisIndex, float value) {
        //Gdx.app.log("controller", "axis moved: "+axisIndex+" : "+value);

        ControllerConfiguration config = Main.controllerConfiguration;

        if(axisIndex == config.axis[0])
            carController.steeringAxisMoved(value*config.sign[0]);
        if(axisIndex == config.axis[1])
            carController.throttleAxisMoved(value*config.sign[1]);
        if(axisIndex == config.axis[2])
            carController.brakingAxisMoved(value*config.sign[2]);
        if(axisIndex == config.axis[3])
            carController.reverseAxisMoved(value*config.sign[3]);

        return super.axisMoved(controller, axisIndex, value);
    }

    @Override
    public void connected(Controller controller) {
        Gdx.app.log("controller", "connected");
        super.connected(controller);
    }

    @Override
    public void disconnected(Controller controller) {
        Gdx.app.log("controller", "disconnected");
        super.disconnected(controller);
    }
}
