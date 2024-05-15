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

    public static String convertToUpperWords(String input) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            // Add space before uppercase letters followed by lowercase letters
            if (i > 0 && Character.isUpperCase(currentChar) && Character.isLowerCase(input.charAt(i + 1))) {
                output.append(" ");
            }

            output.append(currentChar);
        }

        return output.toString();
    }
}
