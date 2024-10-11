package me.vlink102.objects.ui;

import me.vlink102.objects.FrameAdapter;
import me.vlink102.objects.Speed;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ContestantFrame extends JPanel {
    public FieldInputPair pair;
    public FieldComponentPair speed;
    public FieldComponentPair endurance;

    public ContestantFrame() {
        super();
        this.setBorder(new EmptyBorder(3, 3, 3, 3));
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        this.setLayout(layout);
        pair = FieldInputPair.of("Name", "Tortoise", false);
        FrameAdapter.addComponent(this, pair, layout, constraints, 0, 0, 1, 1);

        speed = FieldComponentPair.of("Speed", RangeSliderValueGroup.of(RangeSlider.of(0, 100, 20)));
        FrameAdapter.addComponent(this, speed, layout, constraints, 1, 0, 1, 1);
        endurance = FieldComponentPair.of("Endurance", spinner(20, 1, 0, 100));
        FrameAdapter.addComponent(this, endurance, layout, constraints, 2, 0, 1, 1);
    }

    public static JSpinner spinner(int value, int step, int min, int max) {
        SpinnerModel model = new SpinnerNumberModel(value, min, max, step);
        return new JSpinner(model);
    }

    public String getName() {
        return pair.getResult();
    }

    public Speed getSpeed() {
        return speed.getRange();
    }

    public int getEndurance() {
        return endurance.getEndurance();
    }
}
