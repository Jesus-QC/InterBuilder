package com.intercord.builder.gplay;

/**
 * Represents the architecture of a device.
 */
public enum DeviceArchitecture {

    /**
     * Represents an ARM64-v8a device.
     */
    ARM64_v8a,

    /**
     * Represents an ARMv7 device.
     */
    ARMEABI_v7a,

    /**
     * Represents an x86 device.
     */
    x86,

    /**
     * Represents an x86_64 device.
     */
    x86_64;

    public String toString() {
        return switch (this) {
            case ARM64_v8a -> "arm64-v8a";
            case ARMEABI_v7a -> "armeabi-v7a";
            case x86 -> "x86";
            case x86_64 -> "x86_64";
        };
    }
}

// Hello there! If you are reading this you owe me 5 dollars :)