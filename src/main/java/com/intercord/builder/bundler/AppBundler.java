package com.intercord.builder.bundler;

import com.reandroid.apkeditor.merge.Merger;

import java.io.File;
import java.nio.file.Path;

/**
 * Bundles different apks into a single apk.
 */
public final class AppBundler {

    /**
     * Bundles the apks in the specified directory.
     *
     * @param directory The directory containing the apks.
     * @return The path to the bundle apk.
     */
    public static Path bundleApks(Path directory) {
        try {
            Merger.execute(new String[]{"-i", directory.toString()});
            File mergedApk = new File(directory + "_merged.apk");
            return mergedApk.toPath();
        } catch (Exception e) {
            System.out.println("Error bundling apks: " + e.getMessage());
            return null;
        }
    }
}
