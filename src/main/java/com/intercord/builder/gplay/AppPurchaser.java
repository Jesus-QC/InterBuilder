package com.intercord.builder.gplay;

import com.aurora.gplayapi.Constants;
import com.aurora.gplayapi.data.models.App;
import com.aurora.gplayapi.data.models.AuthData;
import com.aurora.gplayapi.data.models.File;
import com.aurora.gplayapi.helpers.PurchaseHelper;

import java.util.List;

/**
 * Provides a purchaser for the Google Play Store.
 */
public final class AppPurchaser {

    /**
     * Gets the download URLs for the specified app.
     * @param app The app to download.
     * @param authData The authentication data.
     * @return The download URLs.
     * @throws Exception If an error occurs.
     */
    public static List<File> getUrls(App app, AuthData authData) throws Exception {
        PurchaseHelper purchaseHelper = new PurchaseHelper(authData);
        String packageName = app.getPackageName();
        int versionCode = app.getVersionCode();
        int offerType = app.getOfferType();
        return purchaseHelper.purchase(packageName, versionCode, offerType, null, null, null, Constants.PatchFormat.GZIPPED_BSDIFF);
    }
}
