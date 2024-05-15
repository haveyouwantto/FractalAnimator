package hywt.fractal.animator.ui;

import hywt.fractal.animator.*;
import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.keyframe.FZKeyframeManager;
import hywt.fractal.animator.keyframe.KeyframeManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class GUI extends JFrame {

    private final IndicatorSelectorPanel indiPanel;
    private final JButton browseBtn;
    private final JButton genBtn;
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

        JPanel genOptionPanel = new JPanel();
        getContentPane().add(genOptionPanel, BorderLayout.SOUTH);

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
                JOptionPane.showMessageDialog(importerSelect, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        inputPanel.add(importerSelect, BorderLayout.NORTH);


        JPanel interpPanel = new JPanel();
        interpPanel.setLayout(new BorderLayout());

        JComboBox<Class<? extends InterpolatorConfigure>> interpSelect = new JComboBox<>();

        interpSelect.addItem(LinearInterpolatorConfigure.class);
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
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        genOptionPanel.add(browseBtn);

        genBtn = new JButton("Generate");
        genBtn.addActionListener(e -> {
            try {
                generate();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        genOptionPanel.add(genBtn);

        pack();
    }

    public void setGenerateEnabled(boolean b) {
        browseBtn.setEnabled(b);
        genBtn.setEnabled(b);
    }

    public void generate() throws Exception {
        VideoRenderer renderer = new VideoRenderer(1920, 1080, 30);

        KeyframeManager manager = managerConfigure.get();
        Interpolator interpolator = interpConfigure.get();

        renderer.setInterpolator(interpolator);

        for (Class<? extends ScaleIndicator> indicator : indiPanel.getSelected()) {
            renderer.addScaleIndicator(indicator.getDeclaredConstructor().newInstance());
        }


        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setCurrentDirectory(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("MKV files","mkv"));

        int result = chooser.showSaveDialog(genBtn);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();

            if (!selectedFile.getName().endsWith(".mkv")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".mkv");
            }

            ProgressDialog dialog = new ProgressDialog(renderer);
            dialog.setLocationRelativeTo(genBtn);
            dialog.start();

            setGenerateEnabled(false);
            try {
                File finalSelectedFile = selectedFile;
                new Thread(() -> {
                    try {
                        renderer.ffmpegRender(manager, finalSelectedFile.getAbsolutePath());
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        setGenerateEnabled(true);
                    }
                }).start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

    }
}
