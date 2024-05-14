package hywt.fractal.animator.ui;

import javax.swing.*;

public abstract class OptionConfigure<T> extends JPanel {
    public abstract T get();
    public abstract void init() throws Exception;
}
