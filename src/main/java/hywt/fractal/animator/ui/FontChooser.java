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
    private final JButton confirm;
    private final JList<String> fontList;
    private final JCheckBox checkBox;
    private final Component previewPanel;
    private final JTextField searchBar;
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

        searchBar = new JTextField();
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

        previewPanel = new JTextArea("The quick brown fox jumps over the lazy dog.\n0123456789");
        previewPanel.setPreferredSize(new Dimension(getWidth(), 80));

        fontList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !fontList.isSelectionEmpty()) {
                String fontName = fontList.getSelectedValue();
                font = new Font(fontName, Font.PLAIN, 20);
                previewPanel.setFont(font);
                previewPanel.repaint();
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        bottomPanel.add(previewPanel, BorderLayout.NORTH);

        JPanel confirmPanel = new JPanel();

        checkBox = new JCheckBox();
        JLabel label = new JLabel("Internal");
        checkBox.addActionListener(e -> updateSelection(checkBox.isSelected()));
        confirmPanel.add(checkBox);
        confirmPanel.add(label);

        confirm = new JButton("Close");
        confirmPanel.add(confirm);
        bottomPanel.add(confirmPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        confirmListener = e -> setVisible(false);

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
        boolean useInternal = obj.getBoolean("internal");
        checkBox.setSelected(useInternal);
        if (useInternal) {
            loadDefaultFont();
        } else {
            fontList.setEnabled(true);
            fontList.setSelectedValue(obj.getString("fontName"), true);
        }
        updateSelection(useInternal);
    }

    private void updateSelection(boolean useInternal){
        fontList.setEnabled(!useInternal);
        searchBar.setEnabled(!useInternal);

        if (useInternal) {
            loadDefaultFont();
        } else {
            String fontName = fontList.getSelectedValue();
            font = new Font(fontName, Font.PLAIN, 20);
        }
        previewPanel.setFont(font);
        previewPanel.repaint();
    }

    private void loadDefaultFont() {
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemResource("assets/fonts/Inconsolata.ttf").openStream()).deriveFont(20f);
        } catch (FontFormatException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
