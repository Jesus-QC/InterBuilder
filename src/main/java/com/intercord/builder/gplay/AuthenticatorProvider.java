package com.intercord.builder.gplay;

import com.aurora.gplayapi.data.models.AuthData;
import com.aurora.gplayapi.helpers.AuthHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Provides an authenticator for the Google Play Store.
 * It allows multiple devices of different architectures to connect to the Google Play Store.
 */
public final class AuthenticatorProvider {
    private static final String AUTH_GENERATOR = "https://auroraoss.com/api/auth";
    private static final String PLATFORMS_PROP_ID = "Platforms";

    private static final Map<DeviceArchitecture, AuthData> authenticators = new HashMap<>();

    private static boolean initialized = false;

    /**
     * Initializes the authenticator provider.
     */
    public static void init(){
        if (initialized){
            return;
        }

        authenticators.clear();

        while (!getAuthenticators()) {
            System.out.println("Error getting authenticators. Retrying...");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("???: " + e.getMessage());
            }
        }

        initialized = true;
    }

    /**
     * Gets the authenticator for the specified architecture.
     * @param arch The architecture of the device.
     * @return The authenticator.
     */
    public static AuthData getAuthData(DeviceArchitecture arch){
        return authenticators.get(arch);
    }

    private static boolean getAuthenticators() {
        try (InputStream in = URI.create(AUTH_GENERATOR).toURL().openStream()){
            System.out.println("Getting authenticators...");

            if (in == null) {
                System.out.println("Error: InputStream is null");
                return false;
            }

            try (InputStreamReader reader = new InputStreamReader(in))
            {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                String email = root.get("email").getAsString();
                String auth = root.get("auth").getAsString();

                for (DeviceArchitecture arch : DeviceArchitecture.values()) {
                    Properties deviceProps = DeviceProvider.getProperties();
                    deviceProps.put(PLATFORMS_PROP_ID, arch.toString());
                    AuthData data = AuthHelper.INSTANCE.build(email, auth, AuthHelper.Token.AUTH, true, deviceProps, Locale.UK);
                    authenticators.put(arch, data);
                }
            }

            System.out.println("Authenticators retrieved");
            return true;
        }
        catch (IOException e){
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
