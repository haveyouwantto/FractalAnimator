package hywt.fractal.animator.ui;

import hywt.fractal.animator.ScaleIndicator;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IndicatorSelectorPanel extends JPanel {
    Map<Class<? extends ScaleIndicator>, JCheckBox> checkBoxMap;

    public IndicatorSelectorPanel(Class<? extends ScaleIndicator>[] indicatorClasses) {
        checkBoxMap = new HashMap<>();

        boolean set = false;
        for (Class<? extends ScaleIndicator> indicatorClass : indicatorClasses) {
            JPanel indItem = new JPanel();
            indItem.setLayout(new BorderLayout());

            JCheckBox checkBox = new JCheckBox();
            indItem.add(checkBox, BorderLayout.WEST);

            JLabel label = new JLabel(indicatorClass.getSimpleName());
            indItem.add(label);

            if (!set) {
                checkBox.setSelected(true);
                set = true;
            }

            add(indItem);
            checkBoxMap.put(indicatorClass, checkBox);
        }
    }

    public List<Class<? extends ScaleIndicator>> getSelected() {
        List<Class<? extends ScaleIndicator>> list = new LinkedList<>();
        for (Map.Entry<Class<? extends ScaleIndicator>, JCheckBox> entry : checkBoxMap.entrySet()) {
            if (entry.getValue().isSelected()) list.add(entry.getKey());
        }
        return list;
    }
}
