package me.vlink102.objects.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ClickableComponent extends JButton {
    public ClickableComponent(String title, Runnable runnable) {
        super(title);
        this.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.run();
            }
        });
    }

    public static ClickableComponent of(String title, Runnable runnable) {
        return new ClickableComponent(title, runnable);
    }
}
