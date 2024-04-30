package hywt.fractal.animator;

import hywt.fractal.animator.keyframe.FZKeyframeManager;
import hywt.fractal.animator.keyframe.KeyframeManager;
import hywt.fractal.animator.ui.FrameBrowser;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        KeyframeManager manager = new FZKeyframeManager(new File("E:/fractal/New Folder"));

        VideoRenderer renderer = new VideoRenderer(1920, 1080, 60);

        double[] x = {0, 10, 20, 50};
        double[] y = {0, 30, 41, manager.size()};

        Interpolator interpolator = new LinearInterpolator(x, y);
        renderer.setInterpolator(interpolator);

        ScaleIndicator indicator = new KFScaleIndicator();
        renderer.addScaleIndicator(indicator);

        FrameBrowser browser = new FrameBrowser(manager);
        browser.setVisible(true);
        renderer.ffmpegRender(manager);
    }
}
