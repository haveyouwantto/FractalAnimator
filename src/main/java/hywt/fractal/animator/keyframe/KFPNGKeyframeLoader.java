package hywt.fractal.animator.keyframe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KFPNGKeyframeLoader extends KeyframeLoader {
    List<FractalFrame> frameList;

    public KFPNGKeyframeLoader(File dir) throws FileNotFoundException {

        File[] files = dir.listFiles((dir1, name) -> name.matches("\\d\\d\\d\\d\\d_([0-9e.]+)\\.png$"));

        if (files == null || files.length == 0) throw new FileNotFoundException("Directory invalid.");

        Pattern pattern = Pattern.compile("\\d\\d\\d\\d\\d_([0-9e.]+)\\.png$");

        frameList = new LinkedList<>();
        Arrays.stream(files).forEach(file -> {
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.find())
                frameList.add(new ImageFileFractalFrame(file, FractalScale.fromMagnification(matcher.group(1))));
        });

        Collections.sort(frameList);
    }

    @Override
    public FractalFrame get(int index) {
        try {
            return frameList.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return frameList.size();
    }
}
