package hywt.fractal.animator.ui;

import hywt.fractal.animator.Exportable;
import hywt.fractal.animator.indicator.ScaleIndicator;
import hywt.fractal.animator.Utils;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class IndicatorSelectorPanel extends JPanel implements Exportable {
    private final JLabel fontName;
    private final FontChooser fontChooser;
    Map<Class<? extends ScaleIndicator>, JCheckBox> checkBoxMap;

    public IndicatorSelectorPanel(Class<? extends ScaleIndicator>[] indicatorClasses) {
        checkBoxMap = new HashMap<>();
        fontChooser = new FontChooser();

        boolean set = false;
        for (Class<? extends ScaleIndicator> indicatorClass : indicatorClasses) {
            JPanel indItem = new JPanel();
            indItem.setLayout(new BorderLayout());

            JCheckBox checkBox = new JCheckBox();
            indItem.add(checkBox, BorderLayout.WEST);

            JLabel label = new JLabel(Utils.convertToUpperWords(indicatorClass.getSimpleName()));
            indItem.add(label);

            if (!set) {
                checkBox.setSelected(true);
                set = true;
            }

            add(indItem);
            checkBoxMap.put(indicatorClass, checkBox);
        }

        fontName = new JLabel("Internal font");
        JButton fontSelector = new JButton("Choose font");
        fontSelector.addActionListener(e -> {
            fontChooser.setVisible(true);
        });

        JPanel fontPanel = new JPanel();
        fontPanel.add(fontName);
        fontPanel.add(fontSelector);

        add(fontPanel, BorderLayout.SOUTH);
    }

    public List<Class<? extends ScaleIndicator>> getSelected() {
        List<Class<? extends ScaleIndicator>> list = new LinkedList<>();
        for (Map.Entry<Class<? extends ScaleIndicator>, JCheckBox> entry : checkBoxMap.entrySet()) {
            if (entry.getValue().isSelected()) list.add(entry.getKey());
        }
        return list;
    }

    @Override
    public JSONObject exportJSON() {
        JSONObject obj = new JSONObject();
        for (Class<? extends ScaleIndicator> selected : getSelected()) {
            obj.append("selected", selected.getCanonicalName());
        }
        obj.put("font", fontChooser.exportJSON());
        return obj;
    }

    @Override
    public void importJSON(JSONObject obj) {
        for (Map.Entry<Class<? extends ScaleIndicator>, JCheckBox> entry : checkBoxMap.entrySet()) {
            entry.getValue().setSelected(false);
        }

        for (Object o : obj.getJSONArray("selected")) {
            String className = (String) o;
            try {
                Class<?> item = Class.forName(className);
                checkBoxMap.get(item).setSelected(true);
            } catch (ClassNotFoundException ignored) {
            }
        }

        fontChooser.importJSON(obj.getJSONObject("font"));
    }

    public Font getSelectedFont() {
        return fontChooser.getSelectedFont();
    }
}
