package hywt.fractal.animator;

import java.io.File;

public class Utils {

    public static String removeExtension(File file) {
        String baseName = file.getName();
        if (baseName.isEmpty()) {
            return "";
        }

        int lastDotIndex = baseName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return baseName;
        }

        return baseName.substring(0, lastDotIndex);
    }
}
