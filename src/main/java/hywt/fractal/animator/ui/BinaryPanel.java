package hywt.fractal.animator.ui;

import javax.swing.*;
import java.awt.*;

public class BinaryPanel extends JPanel {
    public BinaryPanel() {
        super();
        setLayout(new BorderLayout());
    }

    public void addLeft(Component c) {
        add(c, BorderLayout.WEST);
    }

    public void addRight(Component c) {
        add(c, BorderLayout.EAST);
    }
}
