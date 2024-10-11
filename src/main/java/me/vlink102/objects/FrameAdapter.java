package me.vlink102.objects;

import me.vlink102.internal.ConstraintBuilder;

import javax.swing.*;
import java.awt.*;

public class FrameAdapter extends JFrame {
    public FrameAdapter(String title) {
        super(title);
    }

    public FrameAdapter() {
        super();
    }

    public static void addComponent(Container parent, Component component, GridBagLayout layout, GridBagConstraints constraints, int row, int col, int width, int height) {
        constraints = ConstraintBuilder.create()
                .setGridX(col)
                .setGridY(row)
                .setGridWidth(width)
                .setGridHeight(height)
                .setPadX(10)
                .setPadY(5)
                .setAnchor(GridBagConstraints.LINE_START)
                .build();
        //constraints.fill = GridBagConstraints.HORIZONTAL;

        layout.setConstraints(component, constraints);
        parent.add(component);
    }
}
