package hywt.fractal.animator.ui;

import hywt.fractal.animator.Localization;
import hywt.fractal.animator.Utils;

import javax.swing.*;
import java.awt.*;

public class ClassNameListRenderer  extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        // Get the default renderer component
        JLabel label = (JLabel) super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);

        // Customize the display text for each item
        if (value instanceof Class) {
            label.setText(Localization.get("class."+((Class<?>) value).getName()));
        }

        return label;
    }
}
