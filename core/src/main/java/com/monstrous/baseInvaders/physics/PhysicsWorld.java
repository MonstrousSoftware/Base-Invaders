package com.monstrous.baseInvaders.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.github.antzGames.gdx.ode4j.ode.*;
import com.monstrous.baseInvaders.worlddata.GameObject;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.worlddata.GameObjectType;
import com.monstrous.baseInvaders.worlddata.World;


import static com.github.antzGames.gdx.ode4j.ode.OdeConstants.*;


// World of rigid body dynamics and collisions
//
public class PhysicsWorld implements Disposable {

    public DWorld world;
    public DSpace space;
    private final DJointGroup contactGroup;
    private final World gameWorld;
    private DContact.DSurfaceParameters defaultSurface;

    public PhysicsWorld(World gameWorld) {
        this.gameWorld = gameWorld;
        OdeHelper.initODE2(0);
        Gdx.app.log("ODE version", OdeHelper.getVersion());
        Gdx.app.log("ODE config", OdeHelper.getConfiguration());
        contactGroup = OdeHelper.createJointGroup();
        reset();

        defaultSurface = new DContact.DSurfaceParameters();
        defaultSurface.mode = dContactFDir1 |  dContactSoftERP | dContactSoftCFM ;
        defaultSurface.mu = Settings.mu;
        defaultSurface.soft_erp = 0.8;
        defaultSurface.soft_cfm = 0.01;
    }


    // reset world, note this invalidates (orphans) all rigid bodies and geoms so should be used in combination with deleting all game objects
    public void reset() {
        if(world != null)
            world.destroy();
        if(space != null)
            space.destroy();

        world = OdeHelper.createWorld();
        space = OdeHelper.createSapSpace( null, DSapSpace.AXES.XZY );

        world.setGravity (0,  Settings.gravity, 0);
        world.setCFM (1e-5);
        world.setERP (0.4);
        world.setQuickStepNumIterations (40);
        world.setAngularDamping(0.5f);

        // set auto disable parameters to make inactive objects go to sleep
        world.setAutoDisableFlag(true);
        world.setAutoDisableLinearThreshold(0.1);
        world.setAutoDisableAngularThreshold(0.1);
        world.setAutoDisableTime(2);
    }

    // update the physics with one (fixed) time step
    public void update() {
        space.collide(null, nearCallback);
        world.quickStep(0.025f);
        contactGroup.empty();
    }

    private Vector3 dir1 = new Vector3();

    private final DGeom.DNearCallback nearCallback = new DGeom.DNearCallback() {

        @Override
        public void call(Object data, DGeom o1, DGeom o2) {
            DBody b1 = o1.getBody();
            DBody b2 = o2.getBody();
            if (b1 != null && b2 != null && OdeHelper.areConnected(b1, b2))
                return;

            final int N = 8;
            DContactBuffer contacts = new DContactBuffer(N);

            int n = OdeHelper.collide(o1, o2, N, contacts.getGeomBuffer());
            if (n > 0) {
                GameObject go1, go2;
                go1 = (GameObject)o1.getData();
                go2 = (GameObject)o2.getData();


                GameObject wheel = null;
                if(go1.type == GameObjectType.TYPE_WHEEL)
                    wheel = go1;
                else if(go2.type == GameObjectType.TYPE_WHEEL)
                    wheel = go2;



                DContact.DSurfaceParameters surface = defaultSurface;
                if(wheel != null) {
                    //Gdx.app.log("cb", "collision with wheel, dir:"+ wheel.getDirection());
                    dir1.set( wheel.getDirection() );
                    surface = wheel.getSurface();
                    if (wheel.body.getVelocity().len2() < 1f) {
                        surface.slip1 = 0;
                        surface.slip2 = 0;
                        surface.mu = 1000;
                    }
                    else {
                        surface.slip1 = Settings.slip1;
                        surface.slip2 = Settings.slip2;
                        surface.mu = Settings.mu;
                    }
                }


                gameWorld.onCollision(go1, go2);        // callback to world

                for (int i = 0; i < n; i++) {
                    DContact contact = contacts.get(i);
                    contact.fdir1.set(dir1.x, dir1.y, dir1.z);
                    contact.surface.mode = surface.mode;
                    contact.surface.mu = surface.mu;
                    contact.surface.mu2 = surface.mu2;
                    contact.surface.slip1 = surface.slip1;
                    contact.surface.slip2 = surface.slip2;
                    contact.surface.soft_erp = surface.soft_erp;
                    contact.surface.soft_cfm = surface.soft_cfm;
                    //contact.fdir1.set(1,0,0);
                    //dContactSlip1 | dContactSlip2  |
//                    contact.surface.mode = dContactFDir1 |  dContactMu2 | dContactSlip1 | dContactSlip2 | dContactSoftERP | dContactSoftCFM | dContactApprox1;
//                    if (o1 instanceof DSphere || o2 instanceof DSphere || o1 instanceof DCapsule || o2 instanceof DCapsule)
//                        contact.surface.mu = 0.01;  // low friction for balls & capsules
//                    else
//                        contact.surface.mu = Settings.mu;
//                    contact.surface.mu2 = Settings.mu2;
//                    contact.surface.slip1 = Settings.slip1;
//                    contact.surface.slip2 = Settings.slip2;
//                    contact.surface.soft_erp = 0.8;
//                    contact.surface.soft_cfm = 0.01;

                    DJoint c = OdeHelper.createContactJoint(world, contactGroup, contact);
                    c.attach(o1.getBody(), o2.getBody());
                }
            }
        }
    };

    @Override
    public void dispose() {
        contactGroup.destroy();
        space.destroy();
        world.destroy();
        OdeHelper.closeODE();
    }

}
