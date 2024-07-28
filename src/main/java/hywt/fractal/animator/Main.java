package hywt.fractal.animator;

import com.formdev.flatlaf.FlatDarculaLaf;
import hywt.fractal.animator.ui.GUI;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws IOException {
        FlatDarculaLaf.setup();
        Localization.initialize();

        GUI gui = new GUI();

        File defaults = new File("default.fap");
        if (defaults.exists()) {
            try {
                DataInputStream is = new DataInputStream(new FileInputStream(defaults));
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                gui.importJSON(new JSONObject(content));
            } catch (IOException ignored) {}
        }

        gui.setVisible(true);
    }
}
