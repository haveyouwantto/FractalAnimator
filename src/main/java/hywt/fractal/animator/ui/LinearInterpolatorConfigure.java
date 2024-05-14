package hywt.fractal.animator.ui;

import hywt.fractal.animator.interp.Interpolator;
import hywt.fractal.animator.interp.LinearInterpolator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LinearInterpolatorConfigure extends InterpolatorConfigure {

    private List<LinearPoint> pointList;
    private JList<LinearPoint> pointJList;

    @Override
    public Interpolator get() {
        double[] x = new double[pointList.size()];
        double[] y = new double[pointList.size()];
        for (int i = 0; i < x.length; i++) {
            LinearPoint point = pointList.get(i);
            x[i] = point.x;
            y[i] = point.y;
        }
        return new LinearInterpolator(x, y);
    }

    @Override
    public void init() throws Exception {
        setLayout(new BorderLayout());

        JPanel editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
        add(editPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        editPanel.add(inputPanel);

        JLabel xl = new JLabel("time: ");
        JLabel yl = new JLabel("zoom: ");
        JTextField xSpinner = new JTextField();
        JTextField ySpinner = new JTextField();
        xSpinner.setPreferredSize(new Dimension(60, 30));
        ySpinner.setPreferredSize(new Dimension(60, 30));
        inputPanel.add(xl);
        inputPanel.add(xSpinner);
        inputPanel.add(yl);
        inputPanel.add(ySpinner);

        pointList = new ArrayList<>();
        pointList.add(new LinearPoint(0,0));
        pointJList = new JList<>();
        add(pointJList, BorderLayout.CENTER);
        updateList();

        JPanel btnPanel = new JPanel();
        editPanel.add(btnPanel);
        JButton addBtn = new JButton("+");
        btnPanel.add(addBtn);
        addBtn.addActionListener(e -> {
            try {
                LinearPoint point = new LinearPoint(Double.parseDouble(xSpinner.getText()), Double.parseDouble(ySpinner.getText()));
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
                LinearPoint point = new LinearPoint(Double.parseDouble(xSpinner.getText()), Double.parseDouble(ySpinner.getText()));
                pointList.add(i, point);
                updateList();
            }

        });
        btnPanel.add(modBtn);

        JButton upBtn = new JButton("^");
        upBtn.addActionListener(e -> {
            int i = pointJList.getSelectedIndex();
            if (i >= 1) {
                LinearPoint point = pointList.get(i);
                pointList.remove(i);
                pointList.add(i - 1, point);
                updateList();
                pointJList.setSelectedIndex(i - 1);
            }

        });
        btnPanel.add(upBtn);

        JButton downBtn = new JButton("V");
        downBtn.addActionListener(e -> {
            int i = pointJList.getSelectedIndex();
            if (i <= pointList.size() - 2) {
                LinearPoint point = pointList.get(i);
                pointList.remove(i);
                pointList.add(i + 1, point);
                updateList();
                pointJList.setSelectedIndex(i + 1);
            }
        });
        btnPanel.add(downBtn);
    }

    private void updateList() {
        DefaultListModel<LinearPoint> lm = new DefaultListModel<>();
        pointList.forEach(lm::addElement);

        pointJList.setModel(lm);
    }

    static class LinearPoint {
        final double x;
        final double y;

        LinearPoint(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "t: " + x + " z:" + y;
        }
    }
}
