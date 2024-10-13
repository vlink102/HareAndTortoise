package me.vlink102.internal;

import java.awt.*;

@SuppressWarnings("unused")
public class ConstraintBuilder {
    private final GridBagConstraints constraints;

    public ConstraintBuilder() {
        this.constraints = new GridBagConstraints();
    }

    public static ConstraintBuilder builder() {
        return new ConstraintBuilder();
    }

    public ConstraintBuilder setFill(int fill) {
        constraints.fill = fill;
        return this;
    }

    public ConstraintBuilder setWeightX(float weight) {
        constraints.weightx = weight;
        return this;
    }

    public ConstraintBuilder setWeightY(float weight) {
        constraints.weighty = weight;
        return this;
    }

    public ConstraintBuilder setAnchor(int anchor) {
        constraints.anchor = anchor;
        return this;
    }

    public ConstraintBuilder setInsets(int left, int top, int right, int bottom) {
        constraints.insets = new Insets(left, top, right, bottom);
        return this;
    }

    public ConstraintBuilder setGridX(int gridX) {
        constraints.gridx = gridX;
        return this;
    }

    public ConstraintBuilder setGridY(int gridY) {
        constraints.gridy = gridY;
        return this;
    }

    public ConstraintBuilder setGridWidth(int gridWidth) {
        constraints.gridwidth = gridWidth;
        return this;
    }

    public ConstraintBuilder setGridHeight(int gridHeight) {
        constraints.gridheight = gridHeight;
        return this;
    }

    public ConstraintBuilder setPadX(int padX) {
        constraints.ipadx = padX;
        return this;
    }

    public ConstraintBuilder setPadY(int padY) {
        constraints.ipady = padY;
        return this;
    }

    public GridBagConstraints build() {
        return constraints;
    }
}
