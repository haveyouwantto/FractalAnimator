package hywt.fractal.animator.ui;

import hywt.fractal.animator.*;
import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.interp.RenderParams;
import hywt.fractal.animator.keyframe.KeyframeManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GUI extends JFrame {

    private final IndicatorSelectorPanel indiPanel;
    private final JButton browseBtn;
    private final JButton genBtn;
    private final JSpinner widthSpinner;
    private final JSpinner heightSpinner;
    private final JSpinner fpsSpinner;
    private final JTextField ffmpegCmd;
    private final JSpinner mergeSpinner;
    private final JComboBox<EncodingParam> paramJComboBox;
    private final ProgressPanel progressPanel;
    private ManagerConfigure managerConfigure;
    private OptionConfigure<Interpolator> interpConfigure;

    private boolean rendering;
    private VideoRenderer renderer;

    public GUI() {

        setTitle("Fractal Animator");
        setPreferredSize(new Dimension(854, 480));
        setLocation(100, 100);
        setIconImage(Toolkit.getDefaultToolkit()
                .getImage(ClassLoader.getSystemResource("assets/mandelbrot1.png")));
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(1, 4));
        getContentPane().add(controls, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        progressPanel = new ProgressPanel();
        bottomPanel.add(progressPanel);

        JPanel operationPanel = new JPanel();
        bottomPanel.add(operationPanel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new CardLayout());
        inputPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Image Sequence",
                TitledBorder.CENTER, TitledBorder.TOP, null, null));
        inputPanel.setLayout(new BorderLayout());
        controls.add(inputPanel);

        JLabel frameNum = new JLabel("Empty");
        inputPanel.add(frameNum, BorderLayout.SOUTH);

        JComboBox<Class<? extends ManagerConfigure>> importerSelect = new JComboBox<>();

        importerSelect.addItem(FZSequenceConfigure.class);
        importerSelect.addItem(TestSequenceConfigure.class);
        importerSelect.addItem(KFPNGSequenceConfigure.class);
        importerSelect.setRenderer(new ClassNameListRenderer());
        importerSelect.addActionListener(e -> {
            try {
                Component component = ((BorderLayout) inputPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                if (component != null) {
                    inputPanel.remove(component);
                }
                managerConfigure = ((Class<? extends ManagerConfigure>) Objects.requireNonNull(importerSelect.getSelectedItem())).getDeclaredConstructor().newInstance();
                managerConfigure.setOnLoadCallable(() -> {
                    frameNum.setText("Found " + managerConfigure.get().size() + " frames");
                    return null;
                });
                frameNum.setText("Empty");
                managerConfigure.init();

                inputPanel.add(managerConfigure, BorderLayout.CENTER);
                inputPanel.revalidate();
                inputPanel.repaint();
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException ex) {
                showError(ex);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        inputPanel.add(importerSelect, BorderLayout.NORTH);


        JPanel interpPanel = new JPanel();
        interpPanel.setLayout(new BorderLayout());

        JComboBox<Class<? extends InterpolatorConfigure<?>>> interpSelect = new JComboBox<>();

        interpSelect.addItem(LinearInterpolatorConfigure.class);
        interpSelect.addItem(SlopeAccelInterpolatorConfigure.class);
        interpSelect.addItem(AccelInterpolatorConfigure.class);

        interpSelect.setRenderer(new ClassNameListRenderer());
        interpSelect.addActionListener(e -> {
            try {
                Component component = ((BorderLayout) interpPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                if (component != null) {
                    interpPanel.remove(component);
                }
                interpConfigure = ((Class<? extends InterpolatorConfigure>) Objects.requireNonNull(interpSelect.getSelectedItem())).getDeclaredConstructor().newInstance();
                interpConfigure.init();

                interpPanel.add(interpConfigure, BorderLayout.CENTER);
                interpPanel.revalidate();
                interpPanel.repaint();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        interpPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Speed Interpolator",
                TitledBorder.CENTER, TitledBorder.TOP, null, null));
        interpPanel.add(interpSelect, BorderLayout.NORTH);
        controls.add(interpPanel);

        Class<? extends ScaleIndicator>[] indicatorClasses = new Class[]{
                KFScaleIndicator.class, FXScaleIndicator.class, OdometerIndicator.class
        };

        indiPanel = new IndicatorSelectorPanel(indicatorClasses);
        indiPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Zoom Indicator",
                TitledBorder.CENTER, TitledBorder.TOP, null, null));
        indiPanel.setLayout(new BoxLayout(indiPanel, BoxLayout.Y_AXIS));
        controls.add(indiPanel);


        browseBtn = new JButton("Browse");
        browseBtn.addActionListener(e -> {
            try {
                FrameBrowser fb = new FrameBrowser(managerConfigure.get());
                fb.setLocationRelativeTo(browseBtn);
                fb.setVisible(true);
            } catch (NullPointerException ex) {
                showError("No keyframes selected.");
            } catch (Exception ex) {
                showError(ex);
            }
        });
        operationPanel.add(browseBtn);

        genBtn = new JButton("Generate");
        genBtn.addActionListener(e -> {
            try {
                if (managerConfigure != null && managerConfigure.get() != null) {
                    if (!this.rendering)
                        generate();
                    else
                        renderer.abort();
                } else {
                    showError("Missing image sequence");
                }
            } catch (Exception ex) {
                showError(ex);
            }
        });
        operationPanel.add(genBtn);

        JScrollPane genOptionsScrollPane = new JScrollPane();

        JPanel genOptionsPanel = new JPanel();

        genOptionsScrollPane.getViewport().add(genOptionsPanel);
        genOptionsScrollPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Video Options",
                TitledBorder.CENTER, TitledBorder.TOP, null, null));

        genOptionsPanel.setLayout(new BoxLayout(genOptionsPanel, BoxLayout.Y_AXIS));

        BinaryPanel mergePanel = new BinaryPanel();
        mergePanel.addLeft(new JLabel("Merge Frames:"));
        mergeSpinner = new JSpinner();
        mergeSpinner.setValue(4);
        mergePanel.addRight(mergeSpinner);
        genOptionsPanel.add(mergePanel);

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


        controls.add(genOptionsScrollPane);

        pack();
    }

    public void generate() throws Exception {
        renderer = new VideoRenderer();

        KeyframeManager manager = managerConfigure.get();
        Interpolator interpolator = interpConfigure.get();

        renderer.setInterpolator(interpolator);

        List<Class<? extends ScaleIndicator>> indicators = indiPanel.getSelected();
        if (indicators.isEmpty()) {
            String message = "In this program, refrain from disabling all scale indicators. \n" +
                    "Due to the highly repetitive nature of fractals, the absence of indicators may confuse the audience. \n" +
                    "It is recommended to enable at least one scale indicator to aid viewer comprehension, although it is not mandatory. \n" +
                    "Thank you!";
            int choice = JOptionPane.showConfirmDialog(this, message, "Warning", JOptionPane.OK_CANCEL_OPTION);
            if (choice != JOptionPane.YES_OPTION) return;
        }
        for (Class<? extends ScaleIndicator> indicator : indicators) {
            renderer.addScaleIndicator(indicator.getDeclaredConstructor().newInstance());
        }

        renderer.setMergeFrames((Integer) mergeSpinner.getValue());


        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setCurrentDirectory(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("MKV files", "mkv"));

        int result = chooser.showSaveDialog(genBtn);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();

            if (!selectedFile.getName().endsWith(".mkv")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".mkv");
            }

            progressPanel.start(renderer);

            setRendering(true);
            try {
                File finalSelectedFile = selectedFile;
                new Thread(() -> {
                    try {
                        RenderParams params = new RenderParams(
                                (Integer) widthSpinner.getValue(),
                                (Integer) heightSpinner.getValue(),
                                (Integer) fpsSpinner.getValue(),
                                (Integer) mergeSpinner.getValue(),
                                2,2,
                                ffmpegCmd.getText(),
                                ((EncodingParam) paramJComboBox.getSelectedItem())
                        );
                        renderer.ffmpegRender(manager, params, finalSelectedFile);
                        showInfo("Render completed.");
                    } catch (Exception e) {
                        showError(e);
                    } finally {
                        setRendering(false);
                        progressPanel.stop();
                    }
                }).start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    private void showError(Exception e) {
        e.printStackTrace();
        showError(e.getLocalizedMessage() + "\n" + Arrays.stream(e.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n")));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setRendering(boolean rendering) {
        this.rendering = rendering;
        browseBtn.setEnabled(!rendering);
        if (rendering) {
            genBtn.setText("Abort");
        } else {
            genBtn.setText("Generate");
        }
    }
}
