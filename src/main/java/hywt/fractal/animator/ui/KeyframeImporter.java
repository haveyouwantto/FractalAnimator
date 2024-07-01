package hywt.fractal.animator.ui;

import hywt.fractal.animator.keyframe.KeyframeLoader;

import javax.swing.*;

public abstract class KeyframeImporter extends JPanel {
    public abstract KeyframeLoader getKeyframes();
}
