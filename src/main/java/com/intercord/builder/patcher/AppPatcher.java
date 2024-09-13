package com.intercord.builder.patcher;

import com.reandroid.apk.ApkModule;
import com.reandroid.arsc.chunk.xml.AndroidManifestBlock;
import com.reandroid.arsc.chunk.xml.ResXmlAttribute;
import com.reandroid.arsc.chunk.xml.ResXmlElement;
import com.reandroid.arsc.value.ValueType;
import org.lsposed.patch.LSPatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

/**
 * Patches an apk with a LSPosed module.
 */
public final class AppPatcher {
    private static final String OriginalPackageName = "com.discord";
    private static final String ApplicationLabel = "Intercord";
    private static final String PackageName = "com.intercord";

    private static final String[] AdditionalPermissions = {
        "android.permission.REQUEST_INSTALL_PACKAGES",
        "android.permission.MANAGE_EXTERNAL_STORAGE"
    };

    /**
     * Patches the specified apk with the specified patch.
     * @param apk The apk to patch.
     * @param patch The patch to apply.
     * @param output The output directory.
     */
    public static boolean patchApk(Path apk, Path patch, Path output) {
        try {
            if (!setupManifest(apk.toFile()))
                return false;

            LSPatch.main(
                apk.toAbsolutePath().toString(),
                "-r", // Allow downgrade
                "-f", // Force
                "-m", patch.toAbsolutePath().toString(), // Embed module
                "-o", output.toAbsolutePath().toString() // Output directory
            );

            return true;
        } catch (IOException e) {
            System.out.println("Error patching apk: " + e.getMessage());
            return false;
        }
    }

    // Basically, adds some permissions, changes the package name and application label, and patches the manifest.
    private static boolean setupManifest(File apk){
        try (ApkModule module = ApkModule.loadApkFile(apk)){
            AndroidManifestBlock manifest = module.getAndroidManifest();

            // We first add the additional permissions.
            for (String permission : AdditionalPermissions)
                manifest.addUsesPermission(permission);

            // We then set the application label and package name.
            manifest.setApplicationLabel(ApplicationLabel);
            manifest.setPackageName(PackageName);

            // Finally, we patch the manifest.
            patchManifest(manifest.getManifestElement());

            // And write the apk.
            module.writeApk(apk);
            return true;
        } catch (IOException e) {
            System.out.println("Error patching manifest: " + e.getMessage());
            return false;
        }
    }

    // I know, I know, this is a bit of a mess. I wrote this drunk at 3am, it works though.
    // It basically just patches the manifest to replace the original package name with the new one.
    private static void patchManifest(ResXmlElement element) throws IOException {
        if (element.getName().equals("permission") || element.getName().equals("uses-permission")) {
            Iterator<ResXmlAttribute> attributes = element.getAttributes();
            while (attributes.hasNext()) {
                ResXmlAttribute attr = attributes.next();
                if (attr.getName().equals("name"))
                    attr.setValueAsString(attr.getValueString().replace(OriginalPackageName, PackageName));
            }
        }

        if (element.getName().equals("provider") || element.getName().equals("activity")) {
            Iterator<ResXmlAttribute> attributes = element.getAttributes();
            while (attributes.hasNext()) {
                ResXmlAttribute attr = attributes.next();

                if (attr.getName().equals("label"))
                    attr.setValueAsString(ApplicationLabel);

                else if (attr.getValueType() == ValueType.STRING) {
                    if (attr.getName().equals("authorities"))
                        attr.setValueAsString(attr.getValueString().replace(OriginalPackageName, PackageName));
                }
            }
        }

        Iterator<ResXmlElement> iterator = element.getElements();
        while (iterator.hasNext()) {
            ResXmlElement el = iterator.next();
            patchManifest(el);
        }
    }
}
