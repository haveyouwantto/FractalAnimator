package hywt.fractal.animator;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class Localization {
    public static Properties locale;
    public static Properties defaultLocale;

    public static void initialize() throws IOException {
        Locale loc = Locale.getDefault();
        String name = loc.getLanguage() + "_" + loc.getCountry();

        defaultLocale = new Properties();
        defaultLocale.load(ClassLoader.getSystemResourceAsStream("assets/lang/en_US.lang"));
        if (name.equals("en_US")) {
            locale = defaultLocale;
        } else {
            locale = new Properties();
            try {
                locale.load(ClassLoader.getSystemResourceAsStream("assets/lang/" + name + ".lang"));
            } catch (NullPointerException ignored) {
            }
        }

        System.out.println(name);
    }

    public static String get(String key) {
        String value = locale.getProperty(key);
        if (value != null) return value;
        else {
            value = defaultLocale.getProperty(key);
            if (value != null) return value;
            else return key;
        }
    }
}
