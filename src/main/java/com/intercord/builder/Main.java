package com.intercord.builder;

import com.aurora.gplayapi.data.models.App;
import com.intercord.builder.bundler.AppBundler;
import com.intercord.builder.gplay.AppDownloader;
import com.intercord.builder.patcher.AppPatcher;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Entry point for the InterBuilder.
 */
public final class Main {
    /**
     * Main method for the InterBuilder.
     * @param args The arguments.
     */
    public static void main(String[] args) {
        if (args.length < 2){
            showHelp();
            return;
        }

        argumentHandler(args);
    }

    private static void showHelp(){
        System.out.println("# InterBuilder (by jesusqc)");
        System.out.println("Available Arguments:");
        System.out.println("--help, -h: Displays this help message.");
        System.out.println("--all <module>, -a <module>: Downloads, bundles and patches discord.");
        System.out.println("--download <output>, -d <output>: Only downloads the files, does not merge or patch.");
        System.out.println("--bundle <directory>, -b <directory>: Only bundles and patches the files, does not download or ls patch.");
        System.out.println("--patch <apk> <module>, -p <apk> <module>: Only patches the APK, does not download or bundle anything.");
    }

    private static void argumentHandler(String[] args){
        final String arg = args[0];

        final StringBuilder input = new StringBuilder();
        for (int i = 1; i < args.length; i++){
            input.append(args[i]).append(" ");
        }

        String inputString = input.toString().trim();

        switch (arg) {
            case "--all", "-a" -> handleAll(inputString);
            case "--download", "-d" -> handleCustomDownload(inputString);
            case "--bundle", "-b" -> handleBundle(inputString);
            case "--patch", "-p" -> handlePatch(inputString);
        }
    }

    private static void handleAll(String module){
        // We are not using the args, so we do everything.
        // 1st: Download the app.
        Path path = handleDownload();

        // 2nd: Merge the app.
        Path bundled = AppBundler.bundleApks(path);
        assert bundled != null;

        // 3rd: Patch the app.
        if (!AppPatcher.patchApk(bundled, Path.of(module), Path.of(System.getProperty("user.dir"), "output"))){
            System.out.println("Error patching APK.");
        }
    }

    private static Path handleDownload(){
        App app = AppDownloader.getAppDetails("com.discord");
        return AppDownloader.tryDownloadAll(app);
    }

    private static void handleCustomDownload(String newPath) {
        App app = AppDownloader.getAppDetails("com.discord");

        Path path = AppDownloader.tryDownloadAll(app);
        assert path != null;

        Path nPath = Path.of(newPath.replace("\"", ""));

        try {
            if (Files.exists(nPath)) {
                FileUtils.deleteDirectory(nPath.toFile());
            }

            FileUtils.moveDirectory(path.toFile(), nPath.toFile());

            System.out.println("Downloaded files to: " + newPath);
        } catch (IOException e) {
            System.out.println("Error moving files to: " + newPath + " - " + e.getMessage());
        }
    }

    private static void handleBundle(String directory){
        Path path = Path.of(directory.replace("\"", ""));
        Path bundledApk = AppBundler.bundleApks(path);

        if (bundledApk != null) {
            System.out.println("Bundled APK created at: " + bundledApk);
        } else {
            System.out.println("Error bundling APKs.");
        }
    }

    private static void handlePatch(String input) {
        // Regular expression to match quoted strings or non-whitespace sequences
        String[] parts = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        if (parts.length < 2) {
            System.out.println("Invalid arguments for patching. Usage: --patch <apk> <module>");
            return;
        }

        // Remove quotes from paths if present
        Path apk = Path.of(parts[0].replace("\"", ""));
        Path module = Path.of(parts[1].replace("\"", ""));
        boolean success = AppPatcher.patchApk(apk, module, Path.of(System.getProperty("user.dir"), "output"));
        if (success) {
            System.out.println("APK patched successfully.");
        } else {
            System.out.println("Error patching APK.");
        }
    }
}
