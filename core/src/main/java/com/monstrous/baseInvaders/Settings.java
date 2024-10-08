package com.monstrous.baseInvaders;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Settings {
    static public boolean release = true;
    static public String title = "Base Invaders";

    static public String version = "v1.0.8 Dec 2023";       // keep in synch with build.gradle
    static public String preferencesName = "base-invaders";

    static public boolean musicOn = true;

    static public boolean supportControllers = true;       // disable in case it causes issues
    static public boolean fullScreen = false;

    // performance
    static public boolean showShadows = true;
    static public boolean extraScenery = true;     // big performance impact
    static public boolean particleFX = true;

    static public float verticalReadjustSpeed = 4f;


    static public float eyeHeight = 2.5f;   // meters

    static public float walkSpeed = 10f;    // m/s
    static public float runFactor = 2f;     // multiplier for walk speed
    static public float turnSpeed = 120f;   // degrees/s
    static public float jumpForce = 5.0f;
    static public float groundRayLength = 1.2f;


    static public boolean invertLook = false;
    static public boolean freeLook = true;
    static public float degreesPerPixel = 0.1f; // mouse sensitivity
    static public boolean showFPS = !release;

    // suppress shadows from scenery items for WebGL because it crashes the depth sorter
    static public boolean sceneryShadows = (Gdx.app.getType() != Application.ApplicationType.WebGL);

    static public float gravity = -9.8f; // meters / s^2

    static public final int shadowMapSize = 2048; //4096;

    static public float playerLinearDamping = 0.05f;
    static public float playerAngularDamping = 0.5f;


    public static float chassisMass = 10f;
    public static float wheelMass = 1f;

    // geometry
    public static float chassisWidth = 2.49f;
    public static float chassisHeight = 1.36f;
    public static float chassisLength = 6f;
    // positioning of wheels
    public static float wheelSide = 1.0f;           // offset from centre of chassis
    public static float wheelForward = 1.98f;
    public static float wheelBack = 1.70f;
    public static float wheelDown = -0.7f;  // -0.7
    // wheel dimensions
    public static float wheelWidth = 0.2f;
    public static float wheelRadius = 0.37f; // as measured in Blender


    public static float mu = 1.5f;  // 1.5
    public static float mu2 = 30.5f;//30
    public static float slip1 = 0.8f;
    public static float slip2 = 0.1f;


    public static float suspensionCFM = 0.0025f;
    public static float suspensionERP = 0.7f;

    public static float chassisDensity = 1f;
    public static float wheelDensity = 1f;

    public static float maxSteerAngle = (float) (0.2f*Math.PI);

    static public final String GLTF_FILE = "models/baseinvaders.gltf";

    static public float chunkSize = 256;
    static public float worldSize = 5*256;
}
