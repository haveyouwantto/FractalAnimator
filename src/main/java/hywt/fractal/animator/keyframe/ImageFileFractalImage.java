package hywt.fractal.animator.keyframe;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageFileFractalImage extends FractalImage {
    public File imageFile;
    private BufferedImage image;

    ImageFileFractalImage(File file, FractalScale scale) {
        this.imageFile = file;
        this.scale = scale;
    }

    @Override
    public BufferedImage getImage() throws IOException {
        if(image == null) image = ImageIO.read(imageFile);
        return image;
    }

    @Override
    public void close() {
        image = null;
    }
}
