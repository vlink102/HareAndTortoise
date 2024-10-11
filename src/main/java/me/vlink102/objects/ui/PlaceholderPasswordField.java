package me.vlink102.objects.ui;

import javax.swing.*;
import java.awt.*;

public class PlaceholderPasswordField extends JPasswordField implements PlaceholderField {
    private String placeholder;

    public PlaceholderPasswordField(String text, int columns) {
        super(text, columns);
    }

    public PlaceholderPasswordField(String text) {
        super(text);
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.isEmpty() || !getText().isEmpty()) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(placeholder, getInsets().left, pG.getFontMetrics()
                .getMaxAscent() + getInsets().top);
    }

    public void setPlaceholder(final String s) {
        placeholder = s;
    }

    @Override
    public String extract() {
        return getText();
    }
}