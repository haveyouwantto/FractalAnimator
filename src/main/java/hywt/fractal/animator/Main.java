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
        KeyframeManager manager = new TestKeyframeManager(); // new FZKeyframeManager(new File("F:/fractal/New Folder"));

        VideoRenderer renderer = new VideoRenderer(1920, 1080, 60);

        double[] x = {0, 10, 20, 50};
        double[] y = {0, 30, 41, manager.size()};

        double[][] speedDef = {
                {0, manager.getFirst().getScale().getZooms(), 3, 3},
                {20, 18, 10, 10},
                {40, manager.getLast().getScale().getZooms(), 30, 30}
        };

        Interpolator interpolator = new AccelInterpolator(speedDef);
        renderer.setInterpolator(interpolator);

        ScaleIndicator indicator = new KFScaleIndicator();
        renderer.addScaleIndicator(indicator);

        FrameBrowser browser = new FrameBrowser(manager);
        browser.setVisible(true);
        renderer.ffmpegRender(manager);
    }
}
