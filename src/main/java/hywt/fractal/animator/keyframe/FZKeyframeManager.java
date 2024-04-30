package hywt.fractal.animator.keyframe;

import hywt.fractal.animator.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class FZKeyframeManager extends KeyframeManager {
    List<FractalFrame> frameList;

    public FZKeyframeManager(File dir) {
        File[] files = dir.listFiles((dir1, name) -> name.matches(".+ \\((\\d+)\\)\\.png$"));

        frameList = new LinkedList<>();
        Arrays.stream(files).forEach(file -> {
            String name = Utils.removeExtension(file);
            File info = new File(file.getParent(), name + ".info");

            try (Scanner sc = new Scanner(new FileInputStream(info))) {
                String size = sc.nextLine().split(":")[1].strip();

                frameList.add(new FZFractalFrame(file, FractalScale.fromSize(size)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        Collections.sort(frameList);
    }

    @Override
    public FractalFrame get(int index) {
        return frameList.get(index);
    }

    @Override
    public int size() {
        return frameList.size();
    }

    @Override
    public Iterator<FractalFrame> iterator() {
        return new Iterator<FractalFrame>() {
            int index =0;
            @Override
            public boolean hasNext() {
                return index < frameList.size();
            }

            @Override
            public FractalFrame next() {
                return frameList.get(index++);
            }
        };
    }

    static class FZFractalFrame extends FractalFrame {
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
}
