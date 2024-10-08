package com.monstrous.baseInvaders.worlddata;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.monstrous.baseInvaders.ParticleEffects;
import com.monstrous.baseInvaders.behaviours.CarBehaviour;
import com.monstrous.baseInvaders.behaviours.JeepBehaviour;
import com.monstrous.baseInvaders.screens.Main;
import com.monstrous.baseInvaders.Settings;
import com.monstrous.baseInvaders.input.UserCarController;
import com.monstrous.baseInvaders.physics.*;
//import com.monstrous.transamtest.worlddata.Terrain;
import com.monstrous.baseInvaders.terrain.Terrain;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

public class World implements Disposable {

    private final Array<GameObject> gameObjects;
    private GameObject player;
    public GameStats stats;
    private final SceneAsset sceneAsset;
    private final PhysicsWorld physicsWorld;
    private final PhysicsBodyFactory factory;
    private UserCarController userCarController;
    public final PhysicsRayCaster rayCaster;
    private CarBehaviour playerCar;
    public Terrain terrain;
    public Scenery scenery;
    private float ufoSpawnTimer;


    public World() {
        Gdx.app.log("world", "constructor");

        gameObjects = new Array<>();

        stats = new GameStats();
        sceneAsset = Main.assets.sceneAsset;

        physicsWorld = new PhysicsWorld(this);
        factory = new PhysicsBodyFactory(physicsWorld);
        rayCaster = new PhysicsRayCaster(physicsWorld);
        terrain = Main.terrain; //new Terrain();
        scenery = new Scenery(this);
        scenery.populate();
        userCarController = new UserCarController();
    }



    public void clear() {
        Gdx.app.log("World.clear()", "");
        physicsWorld.reset();
        userCarController.reset();

        stats.reset();
        gameObjects.clear();
        player = null;
        ufoSpawnTimer = 3f;

        // build invisible boxes outside the world boundary
        Vector3 pos = new Vector3(0, -5, Settings.worldSize/2);
        spawnBoundary(pos, 5, 40f, Settings.worldSize);
        pos.x = Settings.worldSize;
        spawnBoundary(pos, 5, 40f, Settings.worldSize);
        pos.set(Settings.worldSize/2, -5, 0);
        spawnBoundary(pos, Settings.worldSize, 40f, 5f );
        pos.z = Settings.worldSize;
        spawnBoundary(pos, Settings.worldSize, 40f, 5f);
    }

    public int getNumGameObjects() {
        return gameObjects.size;
    }

    public GameObject getGameObject(int index) {
        return gameObjects.get(index);
    }

    public GameObject getPlayer() {
        return player;
    }

    public void setPlayer(GameObject player) {
        this.player = player;
    }

    public CarBehaviour getPlayerCar() {
        return playerCar;
    }

    public UserCarController getUserCarController() {

        return userCarController;
    }


    public GameObject spawnObject(GameObjectType type, String name, String proxyName, CollisionShapeType shapeType, boolean resetPosition, Vector3 position, float density) {
        if (type == GameObjectType.TYPE_TERRAIN)
            return spawnTerrain();

        // a negative position Y means place it at terrain height plus ABS( y )
        if(position.y < -1000){
            position.y = terrain.getHeight(position.x, position.z)+1;
        }

        Scene scene = loadNode(name, resetPosition, position);
        ModelInstance collisionInstance = scene.modelInstance;
        if (proxyName != null) {
            Scene proxyScene = loadNode(proxyName, resetPosition, position);
            collisionInstance = proxyScene.modelInstance;
        }

        PhysicsBody body = null;
        if (type != GameObjectType.TYPE_SCENERY && type != GameObjectType.TYPE_UFO)
            body = factory.createBody(collisionInstance, shapeType, type.isStatic, density);
        GameObject go = new GameObject(type, scene, body);
        gameObjects.add(go);
        if (type.isCar)
            addWheels(go);

        return go;
    }

    // create invisible collision boundary to block off world edges
    public GameObject spawnBoundary(Vector3 pos, float w, float h, float d) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshBuilder;
        meshBuilder = modelBuilder.part("box", GL20.GL_LINES, VertexAttributes.Usage.Position, new Material(ColorAttribute.createDiffuse(Color.WHITE)));
        BoxShapeBuilder.build(meshBuilder, w, h, d);
        Model modelShape = modelBuilder.end();
        ModelInstance instance = new ModelInstance(modelShape, pos);

        PhysicsBody body = factory.createBody(instance, CollisionShapeType.BOX, true, 1f);
        GameObject go = new GameObject(GameObjectType.TYPE_STATIC, null, body);
        gameObjects.add(go);
        return go;
    }

    private GameObject spawnTerrain() {
        for(ModelInstance instance : terrain.instances) {

            Scene scene = new Scene(instance);
            PhysicsBody body = factory.createBody(scene.modelInstance,
                CollisionShapeType.MESH, true, 1f);
            GameObject go = new GameObject(GameObjectType.TYPE_TERRAIN, scene, body);
            gameObjects.add(go);
        }
        return null;
    }

    public ModelInstance spawnScenery(String name, Vector3 position) {
        Scene scene = loadNode(name, true, position);
        return scene.modelInstance;
    }




    private Vector3 tmpPosition = new Vector3();

    // spawn an item at terrain heightScenery

    public GameObject dropItem(String name, float x, float z, float angle) {
        float y = terrain.getHeight(x, z);
        tmpPosition.set(x, y, z);
        GameObject go = spawnObject(GameObjectType.TYPE_SCENERY, name, null, CollisionShapeType.CYLINDER, true, tmpPosition, 1f);
        go.scene.modelInstance.transform.rotate(Vector3.Y, angle);
        return go;
    }

    private void addWheels(GameObject chassis) {

        GameObject w0 = makeWheel(chassis, 0);
        GameObject w1 = makeWheel(chassis, 1);
        GameObject w2 = makeWheel(chassis, 2);
        GameObject w3 = makeWheel(chassis, 3);
        gameObjects.add(w0);
        gameObjects.add(w1);
        gameObjects.add(w2);
        gameObjects.add(w3);

        if(chassis.type == GameObjectType.TYPE_PLAYER) {
            playerCar = (CarBehaviour) chassis.behaviour;
            playerCar.connectWheels(physicsWorld, chassis, w0, w1, w2, w3);
        }
        else {
            JeepBehaviour jeep = (JeepBehaviour)chassis.behaviour;
            jeep.connectWheels(physicsWorld, chassis, w0, w1, w2, w3);
        }
    }

    // index: 0=front left, 1=front right, 2 =rear left, 3 = rear right
    public GameObject makeWheel(GameObject chassis, int index) {
        Vector3 chassisPos = new Vector3();
        chassis.scene.modelInstance.transform.getTranslation(chassisPos);
        float dx = Settings.wheelSide;    // side
        float dy = Settings.wheelDown;   // down
        float dz;

        if (index == 1 || index == 3) // right
            dx = -dx;
        if (index == 2 || index == 3) // rear
            dz = -Settings.wheelBack;
        else
            dz = Settings.wheelForward;

        Vector3 wheelPos = new Vector3();
        wheelPos.set(chassisPos.x + dx, chassisPos.y + dy, chassisPos.z + dz);

        //GameObject go = spawnObject(GameObjectType.TYPE_WHEEL, "wheel", null, CollisionShapeType.SPHERE, true, wheelPos, Settings.wheelDensity);

        GameObject go = spawnObject(GameObjectType.TYPE_WHEEL, "wheel", null, CollisionShapeType.CYLINDER, true, wheelPos, Settings.wheelDensity);

        // turn cylinder axis from Z to X axis, as the car is oriented towards Z, and cylinder by default points to Z
        Quaternion Q = new Quaternion();
        if (index == 1 || index == 3) // right
            Q.setEulerAngles(90, 90, 0);          // BUGFIX!!!
        else
            Q.setEulerAngles(90, 90, 0);
        go.body.setOrientation(Q);


        return go;
    }

    private Scene loadNode(String nodeName, boolean resetPosition, Vector3 position) {
        Scene scene = new Scene(sceneAsset.scene, nodeName);
        if (scene.modelInstance.nodes.size == 0)
            throw new RuntimeException("Cannot find node in GLTF file: " + nodeName);
        applyNodeTransform(resetPosition, scene.modelInstance, scene.modelInstance.nodes.first());         // incorporate nodes' transform into model instance transform
        scene.modelInstance.transform.translate(position);
        return scene;
    }

    private void applyNodeTransform(boolean resetPosition, ModelInstance modelInstance, Node node) {
        if (!resetPosition)
            modelInstance.transform.mul(node.globalTransform);
        node.translation.set(0, 0, 0);
        node.scale.set(1, 1, 1);
        node.rotation.idt();
        modelInstance.calculateTransforms();
    }

    public void removeObject(GameObject gameObject) {
        gameObject.health = 0;
        gameObjects.removeValue(gameObject, true);
        gameObject.dispose();
    }

    private void ufoSpawner(float deltaTime) {
        if(stats.ufosSpawned >= 7)  // max nr of items
            return;
        ufoSpawnTimer -= deltaTime;
        if(ufoSpawnTimer<=0) {

            float x = (float) (Math.random())*0.7f*Settings.worldSize;    // not too close to the edge
            float z = (float) (Math.random())*0.7f*Settings.worldSize;
            float y = terrain.getHeight(x, z);
            spawnObject(GameObjectType.TYPE_UFO, "ufo", null, CollisionShapeType.SPHERE, true, new Vector3(x, y+5f, z), 1f);
            stats.ufosSpawned++;
            ufoSpawnTimer = 15f;
        }
    }


    public void update( float deltaTime ) {

        if(!stats.gameCompleted)
            stats.gameTime += deltaTime;
        if(stats.techCollected == 7)
            stats.gameCompleted = true;
        if(playerCar != null)
            stats.speed = (int)playerCar.speedMPH;
        ufoSpawner(deltaTime);
        userCarController.update(deltaTime);

        for(GameObject go : gameObjects) {
            go.update(this, deltaTime);
            if(go.getPosition().y < -50)    // fallen off the edge
                go.health = 0;
        }
        physicsWorld.update(deltaTime);
        syncToPhysics();

        if(player != null && player.health <= 0)    // fallen off the edge of the map
            Populator.populate(this);   // reset to start

    }

    private void syncToPhysics() {
        for(GameObject go : gameObjects){
            if( go.body != null && go.body.geom.getBody() != null) {
                go.scene.modelInstance.transform.set(go.body.getPosition(), go.body.getBodyOrientation());
            }
        }
    }





    public void onCollision(GameObject go1, GameObject go2){
        // try either order
        if(go1.type.isStatic || go2.type.isStatic)
            return;

        handleCollision(go1, go2);
        handleCollision(go2, go1);
    }

    private void handleCollision(GameObject go1, GameObject go2) {
        if (go1.type.isPlayer && go2.type.canPickup) {
            pickup(go1, go2);
        }
    }

    private void pickup(GameObject character, GameObject pickup){

        Main.assets.sounds.PICK_UP.play();
        removeObject(pickup);
        if(pickup.type == GameObjectType.TYPE_PICKUP_ITEM) {
            stats.techCollected++;
        }
    }


    @Override
    public void dispose() {
        physicsWorld.dispose();
        rayCaster.dispose();
    }
}
