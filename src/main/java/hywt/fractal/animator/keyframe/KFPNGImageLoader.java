package hywt.fractal.animator.keyframe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KFPNGImageLoader extends ImageLoader {
    List<FractalImage> frameList;
    static String regex = "\\d\\d\\d\\d\\d_([0-9Ee.\\-+]+)\\.(png|jpg)$";

    public KFPNGImageLoader(File dir) throws FileNotFoundException {

        File[] files = dir.listFiles((dir1, name) -> name.matches(regex));

        if (files == null || files.length == 0) throw new FileNotFoundException("Directory invalid.");

        Pattern pattern = Pattern.compile(regex);

        frameList = new LinkedList<>();
        Arrays.stream(files).forEach(file -> {
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.find())
                frameList.add(new ImageFileFractalImage(file, FractalScale.fromMagnification(matcher.group(1))));
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
}
