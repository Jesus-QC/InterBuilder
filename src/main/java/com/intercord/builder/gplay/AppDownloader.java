package com.intercord.builder.gplay;

import com.aurora.gplayapi.data.models.App;
import com.aurora.gplayapi.data.models.File;
import com.aurora.gplayapi.helpers.AppDetailsHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Provides a downloader for the Google Play Store.
 */
public class AppDownloader {

    /**
     * Gets the app details for the specified package name.
     * @param packageName The package name.
     * @return The app details.
     */
    public static App getAppDetails(String packageName) {
        AuthenticatorProvider.init();

        AppDetailsHelper appDetailsHelper = new AppDetailsHelper(AuthenticatorProvider.getAuthData(DeviceArchitecture.x86));

        try {
            return appDetailsHelper.getAppByPackageName(packageName);
        } catch (Exception e) {
            System.out.println("Error getting app details: " + e.getMessage());
            return null;
        }
    }

    /**
     * Tries to download all files for the specified app.
     * @param app The app to download.
     * @return The path to the downloaded files.
     */
    public static Path tryDownloadAll(App app){
        AuthenticatorProvider.init();

        List<File> filesToDownload = new ArrayList<>();

        try {
            for (DeviceArchitecture arch : DeviceArchitecture.values()) {
                System.out.println("Getting download URLs for " + arch + "...");
                filesToDownload.addAll(AppPurchaser.getUrls(app, AuthenticatorProvider.getAuthData(arch)));
            }
        } catch (Exception e) {
            System.out.println("Error getting download URLs: " + e.getMessage());
            return null;
        }

        try {
            return downloadAllFiles(filesToDownload);
        } catch (IOException e) {
            System.out.println("Error downloading files: " + e.getMessage());
            return null;
        }
    }

    private static Path downloadAllFiles(List<File> filesToDownload) throws IOException {
        // Temp hashset to avoid downloading the same file multiple times
        HashSet<String> downloadedFiles = new HashSet<>();

        Path temp = Files.createTempDirectory("IBuilder");
        Path download = Files.createDirectory(Path.of(temp.toAbsolutePath().toString(), "com.intercord"));

        for (File file : filesToDownload) {
            String name = file.getName();
            if (downloadedFiles.contains(name)) {
                continue;
            }

            downloadFile(file, download);
            downloadedFiles.add(name);
        }

        return download;
    }

    private static void downloadFile(File file, Path directory) throws IOException {
        try (InputStream in = URI.create(file.getUrl()).toURL().openStream()){
            System.out.println("Downloading " + file.getName() + "...");
            Files.copy(in, Path.of(directory.toAbsolutePath().toString(), file.getName()));
        }
    }
}
