package hywt.fractal.animator.ui;

import hywt.fractal.animator.Exportable;

import javax.swing.*;

public abstract class OptionConfigure<T> extends JPanel implements Exportable {
    public abstract T get();
    public abstract void init() throws Exception;
}
