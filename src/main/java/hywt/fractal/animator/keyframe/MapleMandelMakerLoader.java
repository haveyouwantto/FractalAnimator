package hywt.fractal.animator.keyframe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class MapleMandelMakerLoader extends ImageLoader {
    List<FractalImage> frameList;

    public MapleMandelMakerLoader(File config) throws IOException {
        frameList = new ArrayList<>();

        Properties meta = new Properties();
        meta.load(new FileInputStream(config));

        FractalScale initial = FractalScale.fromMagnification(meta.getProperty("zoom"));

        String path = meta.getProperty("path");

        File imgDir = new File(config.getParent(), path);
        if (imgDir.exists()) {
            int i = 0;
            while (true) {
                File imgFile = new File(imgDir, String.format("%08d.png", i));
                if (!imgFile.exists()) break;
                frameList.add(new ImageFileFractalImage(imgFile, new FractalScale(initial.getZooms() - i)));
                i++;
            }
        }
        Collections.reverse(frameList);
    }

    @Override
    public FractalImage get(int index) {
        return frameList.get(index);
    }

    @Override
    public int size() {
        return frameList.size();
    }
}
