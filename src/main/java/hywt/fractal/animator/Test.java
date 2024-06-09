package hywt.fractal.animator;

import hywt.fractal.animator.interp.AccelInterpolator;
import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.interp.LinearInterpolator;
import hywt.fractal.animator.keyframe.KeyframeManager;
import hywt.fractal.animator.keyframe.TestKeyframeManager;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;

public class Test {
    public static void main(String[] args) throws Exception {
        VideoRenderer renderer = new VideoRenderer(1920,1080,60);

        KeyframeManager manager = new TestKeyframeManager(
                new BigDecimal("-1.94196980154299690815525472716174165222414625907467332984989545269342718319952508338905818143993751340701073847451358931131821605478083733447"),
                new BigDecimal("-0.00042257891239947640172284454699012468080251849999355952457656888213899193157724884839134450710219309085565635990443273636409351372679376826"),
                1e131,
                16384
        );

        Interpolator interpolator = new AccelInterpolator(new double[][]{
                {0,0,5,5},
                {120,435,5,5}
        });

        renderer.setInterpolator(interpolator);

        renderer.addScaleIndicator(new KFScaleIndicator());

        renderer.ffmpegRender(manager, "output/test3.mkv");
    }
}
