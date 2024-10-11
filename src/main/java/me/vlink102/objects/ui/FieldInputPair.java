package me.vlink102.objects.ui;

import me.vlink102.objects.FrameAdapter;

import javax.swing.*;
import java.awt.*;

public class FieldInputPair extends JPanel {
    private final boolean secure;

    private final JComponent field;

    public String getResult() {
        return ((PlaceholderField) field).extract();
    }

    public FieldInputPair(String label, String prompt, boolean secure) {
        this.secure = secure;

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        FrameAdapter.addComponent(this, new JLabel(label), gridBag, constraints, 0, 0, 1, 1);

        if (secure) {
            field = new PlaceholderPasswordField(prompt);
        } else {
            field = new PlaceholderTextField(prompt);
        }
        FrameAdapter.addComponent(this, field, gridBag, constraints, 0, 1, 1, 1);
    }

    public static FieldInputPair of(String label, String prompt, boolean secure) {
        return new FieldInputPair(label, prompt, secure);
    }

    public boolean isSecure() {
        return secure;
    }
}
