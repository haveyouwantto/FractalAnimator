package hywt.fractal.animator;

import com.formdev.flatlaf.FlatDarculaLaf;
import hywt.fractal.animator.ui.GUI;

public class Main {
    public static void main(String[] args) {
        FlatDarculaLaf.setup();
        GUI gui = new GUI();
        gui.setVisible(true);
    }
}
