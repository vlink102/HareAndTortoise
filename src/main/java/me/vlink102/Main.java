package me.vlink102;

import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicIJTheme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FlatMaterialOceanicIJTheme.setup();
            //final MainFrame frame = new MainFrame("Hare and Tortoise (vlink102)");
            final MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
