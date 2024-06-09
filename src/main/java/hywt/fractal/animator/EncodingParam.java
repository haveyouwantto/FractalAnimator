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
                        "-preset", "medium"
                };

            case X265 :
                return new String[]{
                        "-c:v", "libx265",
                        "-crf", "21",
                        "-preset", "medium"
                };
            case NVENC:
                return new String[]{
                        "-c:v", "h264_nvenc",
                        "-qp", "21",
                        "-preset", "p7"
                };
            case QSV:
                return new String[]{
                        "-c:v", "h264_qsv",
                        "-qp", "21"
                };
            case AMF:
                return new String[]{
                        "-c:v", "h264_amf",
                        "-qp", "21"
                };
        }
        return new String[0];
    }
}
