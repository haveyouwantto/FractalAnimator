package hywt.fractal.animator.ui;

import hywt.fractal.animator.EncodingParam;
import hywt.fractal.animator.Exportable;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.Arrays;

public class GenOptionsPanel extends JScrollPane implements Exportable {
    private final JSpinner widthSpinner;
    private final JSpinner heightSpinner;
    private final JSpinner fpsSpinner;
    private final JSpinner mergeSpinner;
    private final JSpinner startTimeSpinner;
    private final JSpinner endTimeSpinner;
    private final JTextField ffmpegCmd;
    private final JComboBox<Object> paramJComboBox;

    public GenOptionsPanel(){
        super();

        JPanel genOptionsPanel = new JPanel();

        getViewport().add(genOptionsPanel);
        setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Video Options",
                TitledBorder.CENTER, TitledBorder.TOP, null, null));

        genOptionsPanel.setLayout(new BoxLayout(genOptionsPanel, BoxLayout.Y_AXIS));

        // Width configuration
        BinaryPanel widthPanel = new BinaryPanel();
        widthPanel.addLeft(new JLabel("Width: "));
        widthSpinner = new JSpinner();
        widthSpinner.setValue(1920);
        widthPanel.addRight(widthSpinner);
        genOptionsPanel.add(widthPanel);

        // Height configuration
        BinaryPanel heightPanel = new BinaryPanel();
        heightPanel.addLeft(new JLabel("Height: "));
        heightSpinner = new JSpinner();
        heightSpinner.setValue(1080);
        heightPanel.addRight(heightSpinner);
        genOptionsPanel.add(heightPanel);

        // FPS configuration
        BinaryPanel fpsPanel = new BinaryPanel();
        fpsPanel.addLeft(new JLabel("FPS: "));
        fpsSpinner = new JSpinner();
        fpsSpinner.setValue(60);
        fpsPanel.addRight(fpsSpinner);
        genOptionsPanel.add(fpsPanel);


        BinaryPanel mergePanel = new BinaryPanel();
        mergePanel.addLeft(new JLabel("Merge Frames:"));
        mergeSpinner = new JSpinner();
        mergeSpinner.setValue(4);
        mergePanel.addRight(mergeSpinner);
        genOptionsPanel.add(mergePanel);


        BinaryPanel startTimePanel = new BinaryPanel();
        startTimePanel.addLeft(new JLabel("Start Time:"));
        startTimeSpinner = new JSpinner();
        startTimeSpinner.setValue(2);
        startTimePanel.addRight(startTimeSpinner);
        genOptionsPanel.add(startTimePanel);

        BinaryPanel endTimePanel = new BinaryPanel();
        endTimePanel.addLeft(new JLabel("End Time:"));
        endTimeSpinner = new JSpinner();
        endTimeSpinner.setValue(2);
        endTimePanel.addRight(endTimeSpinner);
        genOptionsPanel.add(endTimePanel);

        // FFmpeg configuration
        BinaryPanel ffmpegPanel = new BinaryPanel();
        ffmpegPanel.addLeft(new JLabel("FFmpeg: "));
        ffmpegCmd = new JTextField("ffmpeg");
        ffmpegPanel.addRight(ffmpegCmd);
        genOptionsPanel.add(ffmpegPanel);

        // Encoder configuration
        BinaryPanel encoderPanel = new BinaryPanel();
        encoderPanel.addLeft(new JLabel("Encoder: "));
        paramJComboBox = new JComboBox<>();
        Arrays.stream(EncodingParam.values()).forEach(paramJComboBox::addItem);
        encoderPanel.addRight(paramJComboBox);
        genOptionsPanel.add(encoderPanel);

    }

    public int getWidth(){
        return (Integer) widthSpinner.getValue();
    }

    public int getHeight() {
        return (Integer) heightSpinner.getValue();
    }

    public int getFPS() {
        return (Integer) fpsSpinner.getValue();
    }

    public int getMergeFrames() {
        return (Integer) mergeSpinner.getValue();
    }

    public int getStartTime() {
        return (Integer) startTimeSpinner.getValue();
    }

    public int getEndTime() {
        return (Integer) endTimeSpinner.getValue();
    }

    // For JTextField
    public String getFFmpegCommand() {
        return ffmpegCmd.getText();
    }

    // For JComboBox
    public EncodingParam getSelectedParam() {
        return (EncodingParam) paramJComboBox.getSelectedItem();
    }

    @Override
    public JSONObject exportJSON() {
        JSONObject obj = new JSONObject();
        obj.put("width", getWidth());
        obj.put("height", getHeight());
        obj.put("fps", getFPS());
        obj.put("mergeFrames", getMergeFrames());
        obj.put("startTime", getStartTime());
        obj.put("endTime", getEndTime());
        obj.put("ffmpegCmd", getFFmpegCommand());
        obj.put("selectedParam", getSelectedParam());
        return obj;
    }

    @Override
    public void importJSON(JSONObject obj) {
        if (obj.has("width")) {
            widthSpinner.setValue(obj.getInt("width"));
        }
        if (obj.has("height")) {
            heightSpinner.setValue(obj.getInt("height"));
        }
        if (obj.has("fps")) {
            fpsSpinner.setValue(obj.getInt("fps"));
        }
        if (obj.has("mergeFrames")) {
            mergeSpinner.setValue(obj.getInt("mergeFrames"));
        }
        if (obj.has("startTime")) {
            startTimeSpinner.setValue(obj.getInt("startTime"));
        }
        if (obj.has("endTime")) {
            endTimeSpinner.setValue(obj.getInt("endTime"));
        }
        if (obj.has("ffmpegCmd")) {
            ffmpegCmd.setText(obj.getString("ffmpegCmd"));
        }
        if (obj.has("selectedParam")) {
            paramJComboBox.setSelectedItem(EncodingParam.valueOf(obj.getString("selectedParam")));
        }
    }
}
