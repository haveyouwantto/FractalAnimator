package hywt.fractal.animator.keyframe;

import hywt.fractal.animator.Utils;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class FZImageLoader extends ImageLoader {
    List<FractalImage> frameList;

    public FZImageLoader(File dir) throws FileNotFoundException {
        File[] files = dir.listFiles((dir1, name) -> name.matches(".+ \\((\\d+)\\)\\.(png|jpg|bmp)$"));

        if (files == null || files.length == 0) throw new FileNotFoundException("Directory invalid.");

        frameList = new LinkedList<>();
        Arrays.stream(files).forEach(file -> {
            String name = Utils.removeExtension(file);
            File info = new File(file.getParent(), name + ".info.json");
            try {
                String source = new String(Files.readAllBytes(info.toPath()));
                JSONObject object = new JSONObject(source);
                frameList.add(new ImageFileFractalImage(file, FractalScale.fromSize(object.getString("size"))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        Collections.sort(frameList);
    }

    @Override
    public FractalImage get(int index) {
        return frameList.get(index);
    }

    @Override
    public int size() {
        return frameList.size();
    }

    @Override
    public Iterator<FractalImage> iterator() {
        return new Iterator<FractalImage>() {
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < frameList.size();
            }

            @Override
            public FractalImage next() {
                return frameList.get(index++);
            }
        };
    }

}
