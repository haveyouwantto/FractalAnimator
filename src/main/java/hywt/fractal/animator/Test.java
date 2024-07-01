package hywt.fractal.animator;

import hywt.fractal.animator.interp.AccelInterpolator;
import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.interp.RenderParams;
import hywt.fractal.animator.keyframe.KeyframeLoader;
import hywt.fractal.animator.keyframe.TestKeyframeLoader;

import java.io.File;
import java.math.BigDecimal;

public class Test {
    public static void main(String[] args) throws Exception {
        VideoRenderer renderer = new VideoRenderer();

        KeyframeLoader manager = new TestKeyframeLoader(
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

        renderer.ffmpegRender(manager, new RenderParams(
                1920,1080,60, 4,2,2,"ffmpeg",EncodingParam.NVENC
        ), new File("output/test3.mkv"));
    }
}
