package hywt.fractal.animator;

import hywt.fractal.animator.keyframe.FZKeyframeManager;
import hywt.fractal.animator.keyframe.KeyframeManager;
import hywt.fractal.animator.ui.FrameBrowser;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        KeyframeManager manager = new FZKeyframeManager(new File("E:/fractal/New Folder"));

        double[] x = {0, 10, 20, 50};
        double[] y = {0, 30, 41, manager.size()};

        Interpolator interpolator = new LinearInterpolator(x, y);

        System.out.println(manager.getFrameAtZoom(0));

        VideoRenderer renderer = new VideoRenderer(1920, 1080, 60);
        renderer.setInterpolator(interpolator);

        FrameBrowser browser = new FrameBrowser(manager);
        browser.setVisible(true);
        renderer.ffmpegRender(manager);
    }
}
