package hywt.fractal.animator.ui;

import hywt.fractal.animator.keyframe.KeyframeManager;
import hywt.fractal.animator.keyframe.TestKeyframeManager;

public class TestSequenceConfigure extends ManagerConfigure{
    private KeyframeManager manager;

    public TestSequenceConfigure() {
        manager = new TestKeyframeManager();
    }
    @Override
    public KeyframeManager get() {
        return manager;
    }

    @Override
    public void init() throws Exception {
        load();
    }
}
