package hywt.fractal.animator;

import hywt.fractal.animator.interp.AccelInterpolator;
import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.interp.LinearInterpolator;
import hywt.fractal.animator.keyframe.FZKeyframeManager;
import hywt.fractal.animator.keyframe.KeyframeManager;
import hywt.fractal.animator.keyframe.TestKeyframeManager;
import hywt.fractal.animator.ui.FrameBrowser;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        KeyframeManager manager = new FZKeyframeManager(new File("E:/fractal/high order"));

        VideoRenderer renderer = new VideoRenderer(3840, 2160, 120);

        double[] x = {0, 10, 20, 50};
        double[] y = {0, 30, 41, manager.size()};

        double[][] speedDef = {
                {0, 0, 5, 5},
                {2*60+2, 290, 30, 30},
                {2*60+2+(4*60+25), manager.size() - 1, 30, 30}
        };

        Interpolator interpolator = new AccelInterpolator(speedDef);
        renderer.setInterpolator(interpolator);

        renderer.addScaleIndicator(new KFScaleIndicator());
        renderer.addScaleIndicator(new FXScaleIndicator());

//        FrameBrowser browser = new FrameBrowser(manager);
//        browser.setVisible(true);
        renderer.ffmpegRender(manager);
    }
}
