package hywt.fractal.animator.ui;

import hywt.fractal.animator.EncodingParam;
import hywt.fractal.animator.Exportable;
import hywt.fractal.animator.Localization;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.Arrays;

public class GenOptionsPanel extends JScrollPane implements Exportable {
    private final JSpinner widthSpinner;
    private final JSpinner heightSpinner;
    private final JSpinner fpsSpinner;
    private final JSpinner blendingSpinner;
    private final JSpinner startTimeSpinner;
    private final JSpinner endTimeSpinner;
    private final JTextField ffmpegCmd;
    private final JComboBox<Object> paramJComboBox;

    public GenOptionsPanel(){
        super();

        JPanel genOptionsPanel = new JPanel();

        getViewport().add(genOptionsPanel);
        setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), Localization.get("gen_options.title"),
                TitledBorder.CENTER, TitledBorder.TOP, null, null));

        genOptionsPanel.setLayout(new BoxLayout(genOptionsPanel, BoxLayout.Y_AXIS));

        // Width configuration
        BinaryPanel widthPanel = new BinaryPanel();
        widthPanel.addLeft(new JLabel(Localization.get("gen_options.width")));
        widthSpinner = new JSpinner();
        widthSpinner.setValue(1920);
        widthPanel.addRight(widthSpinner);
        genOptionsPanel.add(widthPanel);

        // Height configuration
        BinaryPanel heightPanel = new BinaryPanel();
        heightPanel.addLeft(new JLabel(Localization.get("gen_options.height")));
        heightSpinner = new JSpinner();
        heightSpinner.setValue(1080);
        heightPanel.addRight(heightSpinner);
        genOptionsPanel.add(heightPanel);

        // FPS configuration
        BinaryPanel fpsPanel = new BinaryPanel();
        fpsPanel.addLeft(new JLabel(Localization.get("gen_options.fps")));
        fpsSpinner = new JSpinner();
        fpsSpinner.setValue(60);
        fpsPanel.addRight(fpsSpinner);
        genOptionsPanel.add(fpsPanel);


        BinaryPanel blendingPanel = new BinaryPanel();
        blendingPanel.addLeft(new JLabel(Localization.get("gen_options.image_blending")));
        blendingSpinner = new JSpinner();
        blendingSpinner.setValue(4);
        blendingPanel.addRight(blendingSpinner);
        genOptionsPanel.add(blendingPanel);


        BinaryPanel startTimePanel = new BinaryPanel();
        startTimePanel.addLeft(new JLabel(Localization.get("gen_options.start_time")));
        startTimeSpinner = new JSpinner();
        startTimeSpinner.setValue(2);
        startTimePanel.addRight(startTimeSpinner);
        genOptionsPanel.add(startTimePanel);

        BinaryPanel endTimePanel = new BinaryPanel();
        endTimePanel.addLeft(new JLabel(Localization.get("gen_options.end_time")));
        endTimeSpinner = new JSpinner();
        endTimeSpinner.setValue(2);
        endTimePanel.addRight(endTimeSpinner);
        genOptionsPanel.add(endTimePanel);

        // FFmpeg configuration
        BinaryPanel ffmpegPanel = new BinaryPanel();
        ffmpegPanel.addLeft(new JLabel(Localization.get("gen_options.ffmpeg")));
        ffmpegCmd = new JTextField("ffmpeg");
        ffmpegPanel.addRight(ffmpegCmd);
        genOptionsPanel.add(ffmpegPanel);

        // Encoder configuration
        BinaryPanel encoderPanel = new BinaryPanel();
        encoderPanel.addLeft(new JLabel(Localization.get("gen_options.encoder")));
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

    public int getImageBlending() {
        return (Integer) blendingSpinner.getValue();
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
        obj.put("imageBlending", getImageBlending());
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
        if (obj.has("imageBlending")) {
            blendingSpinner.setValue(obj.getInt("imageBlending"));
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
