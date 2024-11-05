package me.vlink102.objects.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PanelTest {
    public JButton start;
    public JButton add;
    public JButton clearAl;
    public JPanel left;
    public JLabel settingsLabel;
    public JPanel right;
    public JPanel addPanel;
    public JScrollPane participantScroll;
    public JPanel startPanel;
    public JPanel main;
    public JScrollPane settingsScrollpane;
    public JPanel settingsPanel;
    public JSpinner raceLengthSpinner;
    public JLabel raceLengthLabel;
    public JButton generateRandomButton;

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        main = new JPanel();
        main.setLayout(new GridBagLayout());
        left = new JPanel();
        left.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(left, gbc);
        left.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        settingsLabel = new JLabel();
        settingsLabel.setText("Settings");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        left.add(settingsLabel, gbc);
        settingsScrollpane = new JScrollPane();
        settingsScrollpane.setHorizontalScrollBarPolicy(31);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        left.add(settingsScrollpane, gbc);
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());
        settingsPanel.setAutoscrolls(false);
        settingsScrollpane.setViewportView(settingsPanel);
        raceLengthLabel = new JLabel();
        raceLengthLabel.setFocusable(false);
        raceLengthLabel.setText("Race Length");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        settingsPanel.add(raceLengthLabel, gbc);
        raceLengthSpinner = new JSpinner();
        raceLengthSpinner.setFocusable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        settingsPanel.add(raceLengthSpinner, gbc);
        final JLabel label1 = new JLabel();
        label1.setFocusable(false);
        label1.setText("Round Interval");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        settingsPanel.add(label1, gbc);
        final JSpinner spinner1 = new JSpinner();
        spinner1.setFocusable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        settingsPanel.add(spinner1, gbc);
        right = new JPanel();
        right.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        main.add(right, gbc);
        addPanel = new JPanel();
        addPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        right.add(addPanel, gbc);
        addPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        clearAl = new JButton();
        clearAl.setText("Clear All");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        addPanel.add(clearAl, gbc);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        addPanel.add(panel1, gbc);
        add = new JButton();
        add.setHideActionText(true);
        add.setText("Add Contestant");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(add, gbc);
        generateRandomButton = new JButton();
        generateRandomButton.setText("Generate Random");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(generateRandomButton, gbc);
        participantScroll = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        right.add(participantScroll, gbc);
        participantScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JTable table1 = new JTable();
        table1.setAutoCreateRowSorter(true);
        table1.setEnabled(true);
        table1.setFillsViewportHeight(true);
        participantScroll.setViewportView(table1);
        startPanel = new JPanel();
        startPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        right.add(startPanel, gbc);
        startPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        start = new JButton();
        start.setText("Start Game");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        startPanel.add(start, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return main;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
