package hywt.fractal.animator.ui;

import hywt.fractal.animator.keyframe.KeyframeManager;

import java.util.concurrent.Callable;

public abstract class ManagerConfigure extends OptionConfigure<KeyframeManager> {
    private Callable<Void> callable;

    public void setOnLoadCallable(Callable<Void> callable) {
        this.callable = callable;
    }

    protected void load() throws Exception {
        if (callable != null) callable.call();
    }
}
