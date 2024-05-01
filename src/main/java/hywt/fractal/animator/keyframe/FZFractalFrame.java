package hywt.fractal.animator.keyframe;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FZFractalFrame extends FractalFrame {
    public File imageFile;

    FZFractalFrame(File file, FractalScale scale) {
        this.imageFile = file;
        this.scale = scale;
    }

    @Override
    public BufferedImage getImage() throws IOException {
        return ImageIO.read(imageFile);
    }
}
