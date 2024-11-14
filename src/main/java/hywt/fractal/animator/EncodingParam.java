package hywt.fractal.animator;

public enum EncodingParam {
    X264,
    X265,
    NVENC,
    QSV,
    AMF;

    public String[] getParam() {
        switch (this) {
            case X264:
                return new String[]{
                        "-c:v", "libx264",
                        "-crf", "21",
                        "-preset", "slow",
                        "-x264-params", "bframes=10",
                        "-pix_fmt", "yuv420p10le"
                };

            case X265 :
                return new String[]{
                        "-c:v", "libx265",
                        "-crf", "21",
                        "-preset", "slow",
                        "-x265-params", "no-sao=1:no-rect=1:no-amp=1",
                        "-pix_fmt", "yuv420p10le"
                };
            case NVENC:
                return new String[]{
                        "-c:v", "h264_nvenc",
                        "-qp", "21",
                        "-preset", "p7",
                        "-pix_fmt", "nv12"
                };
            case QSV:
                return new String[]{
                        "-c:v", "h264_qsv",
                        "-qp", "21",
                        "-pix_fmt", "nv12"
                };
            case AMF:
                return new String[]{
                        "-c:v", "h264_amf",
                        "-qp", "21",
                        "-pix_fmt", "nv12"
                };
        }
        return new String[0];
    }
}
