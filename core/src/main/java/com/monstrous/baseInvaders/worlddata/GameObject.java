package com.monstrous.baseInvaders.worlddata;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;
import com.github.antzGames.gdx.ode4j.ode.DContact;
import com.monstrous.baseInvaders.behaviours.Behaviour;
import com.monstrous.baseInvaders.physics.PhysicsBody;
import net.mgsx.gltf.scene3d.scene.Scene;

public class GameObject implements Disposable {

    public final GameObjectType type;
    public final Scene scene;
    public final PhysicsBody body;
    private final Vector3 position;
    public final Vector3 direction;
    public boolean visible;
    public float health;
    public Behaviour behaviour;
    private DContact.DSurfaceParameters surface;
    public final BoundingBox boundingBox = new BoundingBox();
    public final Vector3 dimensions;


    public GameObject(GameObjectType type, Scene scene, PhysicsBody body) {
        this.type = type;
        this.scene = scene;
        this.body = body;
        if(body != null)
            body.geom.setData(this);            // the geom has user data to link back to GameObject for collision handling
        visible = true;
        direction = new Vector3();
        position = new Vector3();
        health = 1f;
        behaviour = Behaviour.createBehaviour(this);
        surface = null;
        dimensions = new Vector3();
        if(scene != null) {
            scene.modelInstance.calculateBoundingBox(boundingBox);
            boundingBox.getDimensions(dimensions);
            scene.modelInstance.transform.getTranslation(position);
        }
    }

    public void update(World world, float deltaTime ){
        if(behaviour != null)
            behaviour.update(world, deltaTime);
    }

    public boolean isDead() {
        return health <= 0;
    }

    public Vector3 getPosition() {
        if(body == null)
            return position;
        return body.getPosition();
    }

    public void setPosition( Vector3 pos ) {
        if(body == null)
            scene.modelInstance.transform.setTranslation(pos);
        else
            body.setPosition(pos);
    }

    public Vector3 getDirection() {
        direction.set(0,0,1);
        direction.mul(body.getOrientation());
        return direction;
    }

    public void setSurface(DContact.DSurfaceParameters surface){
        this.surface = surface;
    }

    public DContact.DSurfaceParameters  getSurface(){
        return surface;
    }

    @Override
    public void dispose() {
        body.destroy();
    }
}
