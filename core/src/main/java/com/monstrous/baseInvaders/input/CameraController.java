package com.monstrous.baseInvaders.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.worlddata.GameObject;

public class CameraController extends InputAdapter {

    private final Camera camera;
    private final Vector3 offset = new Vector3();
    private final Vector3 actualOffset = new Vector3();
    private float distance = 15f;
    private final Vector3 viewingDirection;   // look direction, is forwardDirection plus Y component
    private float mouseDeltaX;
    private float mouseDeltaY;
    public boolean autoCam = false;

    public CameraController(Camera camera ) {
        this.camera = camera;
        viewingDirection = new Vector3(0,0,1);
        reset();
    }

    public void reset() {
        offset.set(0, 1, -3);
        actualOffset.set(0, 10, -30);
        viewingDirection.set(0,0,1);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
       // Gdx.app.log("mouse moved","");
        // ignore big delta jump on start up or resize
        if(Math.abs(Gdx.input.getDeltaX()) >=100 && Math.abs(Gdx.input.getDeltaY()) >= 100)
            return true;
        mouseDeltaX = -Gdx.input.getDeltaX() * Settings.degreesPerPixel;
        mouseDeltaY = -Gdx.input.getDeltaY() * Settings.degreesPerPixel;
        return true;
    }

    private Vector3 tmp = new Vector3();
    private Vector3 tmp2 = new Vector3();
    private Vector3 tmp3 = new Vector3();

    private void rotateView( float deltaX, float deltaY ) {
        viewingDirection.rotate(Vector3.Y, deltaX);

        if (!Settings.freeLook) {    // keep camera movement in the horizontal plane
            viewingDirection.y = 0;
            return;
        }
        if (Settings.invertLook)
            deltaY = -deltaY;

        // avoid gimbal lock when looking straight up or down
        Vector3 oldPitchAxis = tmp.set(viewingDirection).crs(Vector3.Y).nor();
        Vector3 newDirection = tmp2.set(viewingDirection).rotate(tmp, deltaY);
        Vector3 newPitchAxis = tmp3.set(tmp2).crs(Vector3.Y);
        if (!newPitchAxis.hasOppositeDirection(oldPitchAxis))
            viewingDirection.set(newDirection);
    }

    public void update ( float deltaTime, GameObject player ) {
        //Gdx.app.log("view Dir",""+viewingDirection.toString());
        camera.position.set(player.getPosition());

        if(autoCam) {
            viewingDirection.set(player.getDirection());
            viewingDirection.y -= 0.4f;
            float v = player.body.getVelocity().len();
            distance = 5f + v;  // speed dependent view distance
        }



        // mouse to move view direction
        rotateView(mouseDeltaX*deltaTime*Settings.turnSpeed, mouseDeltaY*deltaTime*Settings.turnSpeed );
        mouseDeltaX = 0;
        mouseDeltaY = 0;

        // offset of camera from player position
        offset.set(viewingDirection).scl(-1);      // invert view direction
        offset.y = Math.max(0, offset.y);             // but don't go below player
        offset.nor().scl(distance);                   // scale for camera distance

        if(autoCam)
            actualOffset.slerp(offset,deltaTime);   // let camera lag behind a bit
        else
            actualOffset.set(offset);
        camera.position.add(actualOffset);

        camera.lookAt(player.getPosition());
        camera.up.set(Vector3.Y);

        camera.update(true);
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        return zoom(amountY );
    }

    private boolean zoom (float amount) {
        if(amount < 0 && distance < 5f)
            return false;
        if(amount > 0 && distance > 1500f)
            return false;
        distance += amount;
        return true;
    }
}
