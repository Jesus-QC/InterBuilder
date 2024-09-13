package com.intercord.builder.gplay;

import java.util.Properties;

import com.aurora.gplayapi.DeviceManager;

/**
 * Provides devices for the Google Play Store.
 * It allows multiple devices of different architectures to connect to the Google Play Store.
 */
public final class DeviceProvider {
    private static final String DEVICE_PROPS_FILE = "px_7a.properties";

    /**
     * Gets the device properties for the specified architecture.
     * @return The device properties.
     */
    public static Properties getProperties(){
        return DeviceManager.INSTANCE.loadProperties(DEVICE_PROPS_FILE);
    }
}
