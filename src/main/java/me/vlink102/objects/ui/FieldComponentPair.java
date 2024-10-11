package me.vlink102.objects.ui;

import me.vlink102.objects.FrameAdapter;
import me.vlink102.objects.Speed;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class FieldComponentPair extends JPanel {

    public Component getRight() {
        return right;
    }

    public Speed getRange() {
        RangeSliderValueGroup pair = (RangeSliderValueGroup) right;
        return pair.parse();
    }

    public int getEndurance() {
        JSpinner pair = (JSpinner) right;
        return (int) pair.getValue();
    }

    private final Component right;

    public FieldComponentPair(String label, Component right) {
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        this.right = right;
        FrameAdapter.addComponent(this, new JLabel(label), gridBag, constraints, 0, 0, 1, 1);
        FrameAdapter.addComponent(this, right, gridBag, constraints, 0, 1, 1, 1);
    }

    public static FieldComponentPair of(String label, Component right) {
        return new FieldComponentPair(label, right);
    }
}
