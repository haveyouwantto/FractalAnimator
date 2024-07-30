package hywt.fractal.animator;

import hywt.fractal.animator.indicator.KFScaleIndicator;
import hywt.fractal.animator.interp.AccelInterpolator;
import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.interp.RenderParams;
import hywt.fractal.animator.keyframe.ImageLoader;
import hywt.fractal.animator.keyframe.KFBLoader;
import hywt.fractal.animator.keyframe.SimpleMandelbrotLoader;

import java.io.File;
import java.math.BigDecimal;

public class Test {
    public static void main(String[] args) throws Exception {
        VideoRenderer renderer = new VideoRenderer();

        ImageLoader manager = new KFBLoader(new File("Z:\\kf-2.15.1.6\\kfb"));

        Interpolator interpolator = new AccelInterpolator(new double[][]{
                {0,0,5,5},
                {30, manager.size()-1,5,5}
        });

        renderer.setInterpolator(interpolator);

        renderer.addScaleIndicator(new KFScaleIndicator());

        renderer.ffmpegRender(manager, new RenderParams(
                1920,1080,60, 4,2,2,"ffmpeg",EncodingParam.NVENC
        ), new File("output/test3.mkv"));
    }
}
