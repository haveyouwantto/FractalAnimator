package hywt.fractal.animator.ui;

import hywt.fractal.animator.Exportable;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class FontChooser extends JFrame implements Exportable {
    private final JButton cancel;
    private final JButton confirm;
    private final JList<String> fontList;
    private final JCheckBox checkBox;
    private ActionListener cancelListener;
    private ActionListener confirmListener;
    private Font font;

    public FontChooser() {
        super();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        setPreferredSize(new Dimension(640, 360));
        setTitle("Choose a font");

        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JScrollPane scrollPane = new JScrollPane();
        fontList = new JList<>();

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String fontName : fontNames) model.addElement(fontName);
        fontList.setModel(model);

        scrollPane.getViewport().add(fontList);
        add(scrollPane);

        JTextField searchBar = new JTextField();
        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                String query = searchBar.getText().toLowerCase();
                DefaultListModel<String> model = new DefaultListModel<>();
                for (String fontName : fontNames) {
                    if (fontName.toLowerCase().contains(query)) model.addElement(fontName);
                }
                fontList.setModel(model);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        add(searchBar, BorderLayout.NORTH);

        Component previewPanel = new JTextArea("The quick brown fox jumps over the lazy dog.\n0123456789");
        previewPanel.setPreferredSize(new Dimension(getWidth(), 80));

        fontList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String fontName = fontList.getSelectedValue();
                previewPanel.setFont(new Font(fontName, Font.PLAIN, 24));
                previewPanel.repaint();
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        bottomPanel.add(previewPanel, BorderLayout.NORTH);

        JPanel confirmPanel = new JPanel();

        checkBox = new JCheckBox();
        JLabel label = new JLabel("Internal");
        checkBox.addActionListener(e -> {
            boolean useInternal = checkBox.isSelected();
            fontList.setEnabled(!useInternal);
            searchBar.setEnabled(!useInternal);

            if (useInternal) {
                loadDefaultFont();
            } else {
                String fontName = fontList.getSelectedValue();
                font = new Font(fontName, Font.PLAIN, 24);
            }
            previewPanel.setFont(font);
            previewPanel.repaint();
        });
        confirmPanel.add(checkBox);
        confirmPanel.add(label);

        cancel = new JButton("Cancel");
        confirmPanel.add(cancel);

        confirm = new JButton("Confirm");
        confirmPanel.add(confirm);
        bottomPanel.add(confirmPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        cancelListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (this) {
                    setVisible(false);
                }
            }
        };

        confirmListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (this) {
                    String fontName = fontList.getSelectedValue();
                    font = new Font(fontName, Font.PLAIN, 24);
                    setVisible(false);
                }
            }
        };

        cancel.addActionListener(cancelListener);
        confirm.addActionListener(confirmListener);

        checkBox.doClick();
        pack();
    }

    public Font getSelectedFont() {
        return font;
    }

    @Override
    public JSONObject exportJSON() {
        JSONObject obj = new JSONObject();
        obj.put("fontName", font.getFontName());
        obj.put("internal", checkBox.isSelected());
        return obj;
    }

    @Override
    public void importJSON(JSONObject obj) {
        if (obj.getBoolean("internal")) {
            loadDefaultFont();
        } else {
            font = new Font(obj.getString("fontName"), Font.PLAIN, 24);
            fontList.setSelectedValue(obj.getString("fontName"), true);
        }
    }

    private void loadDefaultFont() {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResource("assets/fonts/Inconsolata.ttf").openStream()).deriveFont(24f);
        } catch (FontFormatException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
