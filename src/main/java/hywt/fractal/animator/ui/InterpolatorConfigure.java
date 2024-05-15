package hywt.fractal.animator.ui;

import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.interp.KeyPoint;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class InterpolatorConfigure<T extends Interpolator> extends OptionConfigure<Interpolator> {

    protected List<KeyPoint> pointList;
    protected JList<KeyPoint> pointJList;
    protected JPanel inputPanel;
    private Map<String, LabeledField> extraFields;

    @Override
    public void init() throws Exception {

        setLayout(new BorderLayout());
        extraFields = new HashMap<>();

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
        add(editPanel, BorderLayout.NORTH);

        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        editPanel.add(inputPanel);

        addField("time", 0);
        addField("zoom", 0);

        pointList = new ArrayList<>();

        JScrollPane pane = new JScrollPane();

        pointJList = new JList<>();
        add(pane, BorderLayout.CENTER);
        pane.getViewport().add(pointJList);
        updateList();

        JPanel btnPanel = new JPanel();
        editPanel.add(btnPanel);
        JButton addBtn = new JButton("+");
        btnPanel.add(addBtn);
        addBtn.addActionListener(e -> {
            try {
                KeyPoint point = new KeyPoint(getKeyMap());
                pointList.add(point);
                updateList();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton removeBtn = new JButton("-");
        removeBtn.addActionListener(e -> {
            int i = pointJList.getSelectedIndex();
            if (i >= 0) {
                pointList.remove(i);
                updateList();
            }

        });
        btnPanel.add(removeBtn);

        JButton modBtn = new JButton("M");
        modBtn.addActionListener(e -> {
            int i = pointJList.getSelectedIndex();
            if (i >= 0) {
                pointList.remove(i);
                KeyPoint point = new KeyPoint(getKeyMap());
                pointList.add(i, point);
                updateList();
            }

        });
        btnPanel.add(modBtn);
    }

    protected Map<String, Double> getKeyMap() {
        Map<String, Double> map = new HashMap<>();
        extraFields.forEach((key, value) -> map.put(key, Double.parseDouble(value.field.getText())));
        return map;
    }

    protected void updateList() {
        Collections.sort(pointList);
        DefaultListModel<KeyPoint> lm = new DefaultListModel<>();
        pointList.forEach(lm::addElement);

        pointJList.setModel(lm);
    }

    protected void addField(String key, double defaultValue) {
        LabeledField field = new LabeledField(key);
        field.field.setText(String.valueOf(defaultValue));
        extraFields.put(key, field);
        inputPanel.add(field);
    }

    protected String getField(String key) {
        return extraFields.get(key).field.getText();
    }

    protected void addDefault() {
        pointList.add(new KeyPoint(getKeyMap()));
        updateList();
    }

    public abstract T get();

    static class LabeledField extends JPanel {

        private JLabel label;
        private JTextField field;

        public LabeledField(String key) {
            label = new JLabel(key + ": ");
            field = new JTextField();
            field.setPreferredSize(new Dimension(60, 30));
            add(label);
            add(field);
        }
    }
}
