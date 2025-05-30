package hywt.fractal.animator.ui;

import hywt.fractal.animator.*;
import hywt.fractal.animator.indicator.*;
import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.interp.RenderParams;
import hywt.fractal.animator.keyframe.ImageLoader;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GUI extends JFrame implements Exportable {

    private final IndicatorSelectorPanel indiPanel;
    private final JButton browseBtn;
    private final JButton genBtn;
    private final ProgressPanel progressPanel;
    private final GenOptionsPanel genOptionsPanel;
    private final JPanel interpPanel;
    private final JComboBox<Class<? extends InterpolatorConfigure<?>>> interpSelect;
    private final JPanel loaderPanel;
    private final JComboBox<Class<? extends ImageLoaderConfigure>> loaderSelect;
    private final JLabel frameNum;
    private ImageLoaderConfigure loaderConfigure;
    private OptionConfigure<Interpolator> interpConfigure;

    private boolean rendering;
    private VideoRenderer renderer;

    public GUI() {

        setTitle(Localization.get("title"));
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

        loaderPanel = new JPanel();
        loaderPanel.setLayout(new CardLayout());
        loaderPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), Localization.get("panel.image"),
                TitledBorder.CENTER, TitledBorder.TOP, null, null));
        loaderPanel.setLayout(new BorderLayout());
        controls.add(loaderPanel);

        frameNum = new JLabel(Localization.get("label.empty"));
        loaderPanel.add(frameNum, BorderLayout.SOUTH);

        loaderSelect = new JComboBox<>();

        loaderSelect.addItem(FZImageLoaderConfigure.class);
        loaderSelect.addItem(KFImageLoaderConfigure.class);
        loaderSelect.addItem(KFBLoaderConfigure.class);
        loaderSelect.addItem(MapleMandelMakerLoaderConfigure.class);
        loaderSelect.addItem(SimpleMandelbrotLoaderConfigure.class);
        loaderSelect.setRenderer(new ClassNameListRenderer());
        loaderSelect.addActionListener(e -> {
            try {
                Component component = ((BorderLayout) loaderPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                if (component != null) {
                    loaderPanel.remove(component);
                }
                loaderConfigure = ((Class<? extends ImageLoaderConfigure>) Objects.requireNonNull(loaderSelect.getSelectedItem())).getDeclaredConstructor().newInstance();
                loaderConfigure.setOnLoadCallable(() -> {
                    frameNum.setText(String.format(Localization.get("image.amount"), loaderConfigure.get().size()));
                    return null;
                });
                frameNum.setText(Localization.get("label.empty"));
                loaderConfigure.init();

                loaderPanel.add(loaderConfigure, BorderLayout.CENTER);
                loaderPanel.revalidate();
                loaderPanel.repaint();
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException ex) {
                showError(ex);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        loaderPanel.add(loaderSelect, BorderLayout.NORTH);


        interpPanel = new JPanel();
        interpPanel.setLayout(new BorderLayout());

        interpSelect = new JComboBox<>();

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

        interpPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), Localization.get("panel.interpolator"),
                TitledBorder.CENTER, TitledBorder.TOP, null, null));
        interpPanel.add(interpSelect, BorderLayout.NORTH);
        controls.add(interpPanel);

        Class<? extends ScaleIndicator>[] indicatorClasses = new Class[]{
                KFScaleIndicator.class, FXScaleIndicator.class, OdometerIndicator.class, ExponentialScaleIndicator.class, GoogologyIndicator.class
        };

        indiPanel = new IndicatorSelectorPanel(indicatorClasses);
        indiPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), Localization.get("panel.indicator"),
                TitledBorder.CENTER, TitledBorder.TOP, null, null));
        indiPanel.setLayout(new BoxLayout(indiPanel, BoxLayout.Y_AXIS));
        controls.add(indiPanel);


        browseBtn = new JButton(Localization.get("label.browse"));
        browseBtn.addActionListener(e -> {
            try {
                ImageBrowser fb = new ImageBrowser(loaderConfigure.get());
                fb.setLocationRelativeTo(browseBtn);
                fb.setVisible(true);
            } catch (NullPointerException ex) {
                showError(Localization.get("message.missing_image"));
            } catch (Exception ex) {
                showError(ex);
            }
        });
        operationPanel.add(browseBtn);

        genBtn = new JButton(Localization.get("label.generate"));
        genBtn.addActionListener(e -> {
            try {
                if (loaderConfigure != null && loaderConfigure.get() != null) {
                    if (!this.rendering)
                        generate();
                    else
                        renderer.abort();
                } else {
                    showError(Localization.get("message.missing_image"));
                }
            } catch (Exception ex) {
                showError(ex);
            }
        });
        operationPanel.add(genBtn);

        genOptionsPanel = new GenOptionsPanel();
        controls.add(genOptionsPanel);

        JMenuBar toolBar = new JMenuBar();
        getContentPane().add(toolBar, BorderLayout.NORTH);


        // Create the Files button
        JMenu filesMenu = new JMenu(Localization.get("menu.files"));

        // Create the menu
        JMenuItem loadMenuItem = new JMenuItem(Localization.get("menu.files.load"));
        JMenuItem saveMenuItem = new JMenuItem(Localization.get("menu.files.save"));
        filesMenu.add(loadMenuItem);
        filesMenu.add(saveMenuItem);

        // Add action listeners to the menu items
        loadMenuItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            chooser.setCurrentDirectory(new File("."));
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter(Localization.get("file.fap"), "fap"));

            int result = chooser.showOpenDialog(filesMenu);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();

                try {
                    DataInputStream inputStream = new DataInputStream(new FileInputStream(selectedFile));
                    String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).strip();
                    importJSON(new JSONObject(content));
                    inputStream.close();
                } catch (IOException ex) {
                    showError(ex);
                }
            }
        });

        saveMenuItem.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            chooser.setCurrentDirectory(new File("."));
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.addChoosableFileFilter(new FileNameExtensionFilter(Localization.get("file.fap"), "fap"));

            int result = chooser.showSaveDialog(filesMenu);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                if (!selectedFile.getName().endsWith(".fap")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".fap");
                }

                try {
                    DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(selectedFile));
                    outputStream.write(exportJSON().toString().getBytes(StandardCharsets.UTF_8));
                    outputStream.close();
                } catch (IOException ex) {
                    showError(ex);
                }
            }
        });



        // Add the Files button to the toolbar
        toolBar.add(filesMenu);



        pack();
    }

    public void generate() throws Exception {
        renderer = new VideoRenderer();

        ImageLoader manager = loaderConfigure.get();
        Interpolator interpolator = interpConfigure.get();

        renderer.setInterpolator(interpolator);

        List<Class<? extends ScaleIndicator>> indicators = indiPanel.getSelected();
        if (indicators.isEmpty()) {
            String message = Localization.get("message.missing_indicator");
            int choice = JOptionPane.showConfirmDialog(this, message, Localization.get("message.warning"), JOptionPane.OK_CANCEL_OPTION);
            if (choice != JOptionPane.YES_OPTION) return;
        }
        for (Class<? extends ScaleIndicator> indicatorClass : indicators) {
            ScaleIndicator indicator = indicatorClass.getDeclaredConstructor().newInstance();
            System.out.println(indicator+" "+ indiPanel.getSelectedFont());
            indicator.setFont(indiPanel.getSelectedFont());
            renderer.addScaleIndicator(indicator);
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setCurrentDirectory(new File("."));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter(Localization.get("file.flv"), "flv"));

        int result = chooser.showSaveDialog(genBtn);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();

            if (!selectedFile.getName().endsWith(".flv")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".flv");
            }

            progressPanel.start(renderer);

            setRendering(true);
            try {
                File finalSelectedFile = selectedFile;
                new Thread(() -> {
                    try {
                        RenderParams params = new RenderParams(
                                genOptionsPanel.getWidth(),
                                genOptionsPanel.getHeight(),
                                genOptionsPanel.getFPS(),
                                genOptionsPanel.getImageBlending(),
                                genOptionsPanel.getStartTime(),
                                genOptionsPanel.getEndTime(),
                                genOptionsPanel.getFFmpegCommand(),
                                genOptionsPanel.getSelectedParam()
                        );
                        renderer.ffmpegRender(manager, params, finalSelectedFile);
                        showInfo(Localization.get("message.completed"));
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
        JOptionPane.showMessageDialog(this, message, Localization.get("message.error"), JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, Localization.get("message.warning"), JOptionPane.WARNING_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, Localization.get("message.info"), JOptionPane.INFORMATION_MESSAGE);
    }

    private void setRendering(boolean rendering) {
        this.rendering = rendering;
        browseBtn.setEnabled(!rendering);
        if (rendering) {
            genBtn.setText(Localization.get("label.abort"));
        } else {
            genBtn.setText(Localization.get("label.generate"));
        }
    }

    @Override
    public JSONObject exportJSON() {
        JSONObject object = new JSONObject();
        object.put("genOptions", genOptionsPanel.exportJSON());

        if (loaderConfigure != null) {
            JSONObject loader = new JSONObject();
            loader.put("type", loaderConfigure.getClass().getCanonicalName());
            loader.put("data", loaderConfigure.exportJSON());
            object.put("loader", loader);
        }

        if (interpConfigure != null) {
            JSONObject interp = new JSONObject();
            interp.put("type", interpConfigure.getClass().getCanonicalName());
            interp.put("data", interpConfigure.exportJSON());
            object.put("interpolator", interp);
        }

        object.put("indicators", indiPanel.exportJSON());
        return object;
    }

    @Override
    public void importJSON(JSONObject obj) {
        genOptionsPanel.importJSON(obj.getJSONObject("genOptions"));

        Component component = ((BorderLayout) loaderPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (component != null) {
            loaderPanel.remove(component);
        }
        try {
            JSONObject loader = obj.getJSONObject("loader");
            Class<?> c = Class.forName(loader.getString("type"));
            loaderSelect.setSelectedItem(c);
            loaderConfigure = (ImageLoaderConfigure) c.getDeclaredConstructor().newInstance();
            loaderConfigure.setOnLoadCallable(() -> {
                frameNum.setText(String.format(Localization.get("image.amount"), loaderConfigure.get().size()));
                return null;
            });
            loaderConfigure.init();
            loaderConfigure.importJSON(loader.getJSONObject("data"));

            loaderPanel.add(loaderConfigure, BorderLayout.CENTER);
            loaderPanel.revalidate();
            loaderPanel.repaint();
        } catch (JSONException ignored) {

        } catch (ClassNotFoundException e) {
            showError("Unknown class: " + e.getMessage());
        } catch (Exception e) {
            showError(e);
        }

        component = ((BorderLayout) interpPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (component != null) {
            interpPanel.remove(component);
        }
        try {
            JSONObject interp = obj.getJSONObject("interpolator");
            Class<?> c = Class.forName(interp.getString("type"));
            interpSelect.setSelectedItem(c);
            interpConfigure = (OptionConfigure<Interpolator>) c.getDeclaredConstructor().newInstance();
            interpConfigure.init();
            interpConfigure.importJSON(interp.getJSONObject("data"));


            interpPanel.add(interpConfigure, BorderLayout.CENTER);
            interpPanel.revalidate();
            interpPanel.repaint();


        } catch (JSONException ignored) {

        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 ClassNotFoundException e) {
            showError(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        indiPanel.importJSON(obj.getJSONObject("indicators"));
    }
}
