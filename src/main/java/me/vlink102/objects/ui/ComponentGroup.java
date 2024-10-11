package me.vlink102.objects.ui;

import me.vlink102.objects.FrameAdapter;

import javax.swing.*;
import java.awt.*;

public class ComponentGroup extends JPanel {
    public enum Axis {
        HORIZONTAL,
        VERTICAL
    }

    public static ComponentGroup ofVertical(GridBagLayout layout, GridBagConstraints constraints, int row, JComponent... components) {
        return new ComponentGroup(Axis.VERTICAL, layout, constraints, row, components);
    }

    public static ComponentGroup ofHorizontal(GridBagLayout layout, GridBagConstraints constraints, int row, JComponent... components) {
        return new ComponentGroup(Axis.HORIZONTAL, layout, constraints, row, components);
    }

    public ComponentGroup(Axis axis, GridBagLayout gridBag, GridBagConstraints constraints, int row, JComponent... components) {
        super();

        for (int i = 0; i < components.length; i++) {
            switch (axis) {
                case VERTICAL -> FrameAdapter.addComponent(this, components[i], gridBag, constraints, row, i, 1, 1);
                case HORIZONTAL -> FrameAdapter.addComponent(this, components[i], gridBag, constraints, i, row, 1, 1);
            }
        }
    }
}
