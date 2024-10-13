package me.vlink102.objects.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class ClickableComponent extends JButton {
    public ClickableComponent(String title, Consumer<ClickableComponent> runnable) {
        super(title);
        this.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runnable.accept(ClickableComponent.this);
            }
        });
    }

    public static ClickableComponent of(String title, Consumer<ClickableComponent> runnable) {
        return new ClickableComponent(title, runnable);
    }
}
