package hywt.fractal.animator;

import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.interp.LinearInterpolator;
import hywt.fractal.animator.keyframe.KeyframeManager;
import hywt.fractal.animator.keyframe.TestKeyframeManager;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;

public class Test {
    public static void main(String[] args) throws Exception {
        VideoRenderer renderer = new VideoRenderer(960,540,30);

        KeyframeManager manager = new TestKeyframeManager(
                new BigDecimal("-2"),
                new BigDecimal("0"),
                1e100,
                200
        );

        Interpolator interpolator = new LinearInterpolator(new double[]{0,100}, new double[]{0,200});

        renderer.setInterpolator(interpolator);

        renderer.addScaleIndicator(new KFScaleIndicator());

        renderer.ffmpegRender(manager, "output/test3.mkv");
    }
}
