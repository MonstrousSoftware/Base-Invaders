package com.monstrous.baseInvaders.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.monstrous.baseInvaders.Settings;

public class ControllerConfiguration {
    private final Preferences preferences;

    public final static String[] keyBindings = {
        "steer left", "throttle", "brake", "reverse"
    };

    public int[] axis = { 2, 5, 4, 1};
    public int[] sign = { -1, 1, 1, 1 };

    public ControllerConfiguration() {
        preferences = Gdx.app.getPreferences(Settings.preferencesName);
        load();
    }

    public void resetToDefault() {
        axis[0] = 2; sign[0] = -1;
        axis[1] = 5; sign[1] = 1;
        axis[2] = 4; sign[2] = 1;
        axis[3] = 1; sign[3] = 1;

    }

    public void save() {
        // save settings for next time
        preferences.putInteger("steer", axis[0]);
        preferences.putInteger("throttle", axis[1]);
        preferences.putInteger("brake", axis[2]);
        preferences.putInteger("reverse", axis[3]);
        preferences.putInteger("steerSign", sign[0]);
        preferences.putInteger("throttleSign",sign[1]);
        preferences.putInteger("brakeSign", sign[2]);
        preferences.putInteger("reverseSign", sign[3]);

        preferences.flush();
    }

    public void load() {

        axis[0] = preferences.getInteger("steer", 2);
        axis[1] = preferences.getInteger("throttle", 5);
        axis[2] = preferences.getInteger("brake", 4);
        axis[3] = preferences.getInteger("reverse", 1);

        sign[0] = preferences.getInteger("steerSign", -1);
        sign[1] = preferences.getInteger("throttleSign", 1);
        sign[2] = preferences.getInteger("brakeSign", 1);
        sign[3] = preferences.getInteger("reverseSign", 1);

    }
}
