package hywt.fractal.animator.interp;

import hywt.fractal.animator.EncodingParam;

public record RenderParams(int width, int height, double fps, int mergeFrames, double startTime, double endTime, String ffmpeg, EncodingParam param) {
}
