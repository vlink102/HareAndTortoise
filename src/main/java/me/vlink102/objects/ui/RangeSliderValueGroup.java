package me.vlink102.objects.ui;

import me.vlink102.objects.FrameAdapter;
import me.vlink102.objects.Speed;

import javax.swing.*;
import java.awt.*;

public class RangeSliderValueGroup extends JPanel {

    public Speed parse() {
        return new Speed((int) minimum.getValue(), (int) maximum.getValue());
    }

    private final JSpinner minimum;
    private final JSpinner maximum;

    public RangeSliderValueGroup(RangeSlider slider) {
        super();
        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        minimum = ContestantFrame.spinner(slider.getValue(), 1, slider.getMinimum(), slider.getMaximum());
        maximum = ContestantFrame.spinner(slider.getUpperValue(), 1, slider.getMinimum(), slider.getMaximum());

        minimum.addChangeListener(_ -> {
            if ((int) minimum.getValue() > (int) maximum.getValue()) {
                minimum.setValue(maximum.getValue());
            }
            slider.setValue((int) minimum.getValue());
        });
        maximum.addChangeListener(_ -> {
            if ((int) maximum.getValue() < (int) minimum.getValue()) {
                maximum.setValue(minimum.getValue());
            }
            slider.setUpperValue((int) maximum.getValue());
        });

        slider.addChangeListener(_ -> {
            minimum.setValue(slider.getValue());
            maximum.setValue(slider.getUpperValue());
        });

        FrameAdapter.addComponent(this, slider, gridBag, constraints, 0, 0, 1, 1);
        FrameAdapter.addComponent(this, minimum, gridBag, constraints, 0, 1, 1, 1);
        FrameAdapter.addComponent(this, maximum, gridBag, constraints, 0, 2, 1, 1);
    }

    public static RangeSliderValueGroup of(RangeSlider slider) {
        return new RangeSliderValueGroup(slider);
    }
}
