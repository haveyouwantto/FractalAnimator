package hywt.fractal.animator.ui;

import hywt.fractal.animator.*;
import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.keyframe.KeyframeManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
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

        JPanel genOptionsPanel = new JPanel();

        JLabel mergeLabel = new JLabel("Merge Frames:");
        mergeSpinner = new JSpinner();
        mergeSpinner.setValue(4);
        genOptionsPanel.add(mergeLabel);
        genOptionsPanel.add(mergeSpinner);

        JLabel widthLabel = new JLabel("Width: ");
        widthSpinner = new JSpinner();
        widthSpinner.setValue(1920);

        genOptionsPanel.add(widthLabel);
        genOptionsPanel.add(widthSpinner);

        JLabel heightLabel = new JLabel("Height: ");
        heightSpinner = new JSpinner();
        heightSpinner.setValue(1080);

        genOptionsPanel.add(heightLabel);
        genOptionsPanel.add(heightSpinner);

        JLabel fpsLabel = new JLabel("FPS: ");
        fpsSpinner = new JSpinner();
        fpsSpinner.setValue(60);

        genOptionsPanel.add(fpsLabel);
        genOptionsPanel.add(fpsSpinner);

        JLabel ffmpegLabel = new JLabel("FFmpeg: ");
        ffmpegCmd = new JTextField("ffmpeg");

        genOptionsPanel.add(ffmpegLabel);
        genOptionsPanel.add(ffmpegCmd);

        JLabel paramLabel = new JLabel("Encoder: ");

        paramJComboBox = new JComboBox<>();
        Arrays.stream(EncodingParam.values()).forEach(paramJComboBox::addItem);

        genOptionsPanel.add(paramLabel);
        genOptionsPanel.add(paramJComboBox);


        bottomPanel.add(genOptionsPanel);

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

        JLabel frameNum = new JLabel("-");
        inputPanel.add(frameNum, BorderLayout.SOUTH);

        JComboBox<Class<? extends ManagerConfigure>> importerSelect = new JComboBox<>();

        importerSelect.addItem(FZSequenceConfigure.class);
        importerSelect.addItem(TestSequenceConfigure.class);
        importerSelect.addItem(KFPNGSequenceConfigure.class);
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
                frameNum.setText("-");
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
//        interpSelect.addItem(QuadraticInterpolatorConfigure.class);
        interpSelect.addItem(AccelInterpolatorConfigure.class);

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
            } catch (Exception ex) {
                showError(ex);
            }
        });
        operationPanel.add(browseBtn);

        genBtn = new JButton("Generate");
        genBtn.addActionListener(e -> {
            try {
                if (managerConfigure.get() != null) {
                    generate();
                } else {
                    showError("Missing image sequence");
                }
            } catch (Exception ex) {
                showError(ex);
            }
        });
        operationPanel.add(genBtn);

        pack();
    }

    public void setGenerateEnabled(boolean b) {
        browseBtn.setEnabled(b);
        genBtn.setEnabled(b);
    }

    public void generate() throws Exception {
        VideoRenderer renderer = new VideoRenderer((Integer) widthSpinner.getValue(), (Integer) heightSpinner.getValue(), (Integer) fpsSpinner.getValue());

        KeyframeManager manager = managerConfigure.get();
        Interpolator interpolator = interpConfigure.get();

        renderer.setInterpolator(interpolator);

        for (Class<? extends ScaleIndicator> indicator : indiPanel.getSelected()) {
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

//            dialog.setLocationRelativeTo(genBtn);
            progressPanel.start(renderer);

            setGenerateEnabled(false);
            try {
                File finalSelectedFile = selectedFile;
                new Thread(() -> {
                    try {
                        renderer.ffmpegRender(manager, finalSelectedFile.getAbsolutePath(), ffmpegCmd.getText(), ((EncodingParam)paramJComboBox.getSelectedItem()).getParam());
                    } catch (Exception e) {
                        showError(e);
                    } finally {
                        setGenerateEnabled(true);
//                        dialog.setCloseable(true);
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
}
