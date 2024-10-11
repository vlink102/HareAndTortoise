package me.vlink102;

import me.vlink102.game.GameInternal;
import me.vlink102.internal.ConstraintBuilder;
import me.vlink102.objects.ContestantModelComparator;
import me.vlink102.objects.FrameAdapter;
import me.vlink102.objects.Participant;
import me.vlink102.objects.Speed;
import me.vlink102.objects.ui.ClickableComponent;
import me.vlink102.objects.ui.ContestantFrame;
import me.vlink102.objects.ui.FieldComponentPair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.IntStream;

public class MainFrame extends FrameAdapter {
    private static LineBorder getBorder() {
        return new LineBorder(UIManager.getColor("Panel.background").darker(), 5);
    }

    public static final Color darkerPanel = UIManager.getColor("Panel.background").darker();

    public MainFrame() {
        super("Hare and Tortoise v2");

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainFrame.this.setVisible(false);
                MainFrame.this.dispose();
            }
        });

        setPreferredSize(new Dimension(1080, 720));
        GameInternal internal = new GameInternal(this);
        Container contentPane = getContentPane();



        final JPanel mainPanel = new JPanel();
        final JPanel settings = new JPanel();

        mainPanel.setLayout(new GridBagLayout());
        settings.setLayout(new GridBagLayout());

        GridBagConstraints constraints = ConstraintBuilder.create()
                .setGridX(0)
                .setGridY(0)
                .setWeightX(1)
                .setWeightY(1)
                .setFill(GridBagConstraints.BOTH)
                .build();
        mainPanel.add(settings, constraints);
        settings.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));



        final JLabel settingsLabel = new JLabel("Settings");
        constraints = ConstraintBuilder.create().setGridX(0).setGridY(0).setWeightX(1).setAnchor(GridBagConstraints.NORTH).build();
        settings.add(settingsLabel, constraints);

        final JScrollPane settingsScrollPane = new JScrollPane();
        settingsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        settingsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        constraints = ConstraintBuilder.create().setGridX(0).setGridY(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        settings.add(settingsScrollPane, constraints);

        final JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());
        settingsPanel.setAutoscrolls(false);

        settingsScrollPane.setViewportView(settingsPanel);

        final JLabel raceLengthLabel = new JLabel("Race Length");
        constraints = ConstraintBuilder.create().setGridX(0).setGridY(0).setWeightX(0.5f).setAnchor(GridBagConstraints.WEST).build();
        settingsPanel.add(raceLengthLabel, constraints);

        final JSpinner raceLengthSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 10));
        constraints = ConstraintBuilder.create().setGridX(1).setGridY(0).setWeightX(0.5f).setAnchor(GridBagConstraints.EAST).build();
        settingsPanel.add(raceLengthSpinner, constraints);

        final JPanel contestantPanel = new JPanel();
        contestantPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.create().setGridX(1).setGridY(0).setWeightX(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        mainPanel.add(contestantPanel, constraints);

        final JPanel contestantManagerPanel = new JPanel();
        contestantManagerPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.create().setGridX(0).setGridY(0).setWeightX(1).setFill(GridBagConstraints.BOTH).build();
        contestantPanel.add(contestantManagerPanel, constraints);
        contestantManagerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        String[] columns = {"Name", "Min Speed", "Max Speed", "Endurance", "UUID"};
        DefaultTableModel model = new DefaultTableModel(null, columns);
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(model);
        final JTable table = new JTable(model);
        table.setRowSorter(rowSorter);
        for (int i = 0; i < columns.length; i++) {
            rowSorter.setComparator(i, new ContestantModelComparator());
        }
        table.setDragEnabled(true);

        final JButton clearAllButton = ClickableComponent.of("Clear All", () -> {
            int user = JOptionPane.showConfirmDialog(MainFrame.this, "Are you sure?");

            if (user == JOptionPane.YES_OPTION) {
                ((DefaultTableModel) table.getModel()).setRowCount(0);
            }
        });
        clearAllButton.setEnabled(false);
        constraints = ConstraintBuilder.create().setGridX(1).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        contestantManagerPanel.add(clearAllButton, constraints);

        final JPanel addInsetPanel = new JPanel();
        addInsetPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.create().setGridX(0).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        contestantManagerPanel.add(addInsetPanel, constraints);

        final JButton addButton = ClickableComponent.of("Add Contestant", () -> {
            ContestantFrame frame = new ContestantFrame();
            int user = JOptionPane.showOptionDialog(MainFrame.this, frame, "Add Contestant", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] {"Add", "Discard"}, 0);
            if (user == JOptionPane.YES_OPTION) {
                Participant participant = new Participant(frame.getName(), frame.getSpeed(), frame.getEndurance());

                //internal.addParticipant(participant);
                SwingUtilities.invokeLater(() -> {
                    Object[] data = participant.tableData();
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    tableModel.addRow(data);
                });
            }
        });
        constraints = ConstraintBuilder.create().setGridX(0).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.HORIZONTAL).build();
        addInsetPanel.add(addButton, constraints);

        final JButton randomContestant = ClickableComponent.of("Generate Random", () -> {
            Participant randomParticipant = Participant.generateRandom();
            /*SwingUtilities.invokeLater(() -> internal.addParticipant(randomParticipant));*/
            SwingUtilities.invokeLater(() -> {
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.addRow(randomParticipant.tableData());
            });
        });
        constraints = ConstraintBuilder.create().setGridX(0).setGridY(1).setFill(GridBagConstraints.HORIZONTAL).build();
        addInsetPanel.add(randomContestant, constraints);

        final JScrollPane participantScrollPane = new JScrollPane();
        constraints = ConstraintBuilder.create().setGridX(0).setGridY(1).setWeightX(1).setWeightY(0.3f).setFill(GridBagConstraints.BOTH).build();
        contestantPanel.add(participantScrollPane, constraints);
        participantScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));


        table.putClientProperty("terminateEditOnFocusLost", true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if(event.getID() == MouseEvent.MOUSE_CLICKED) {
                MouseEvent mouseEvent = (MouseEvent) event;
                int row = table.rowAtPoint(mouseEvent.getPoint());
                if(row == -1) {
                    table.clearSelection();
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0 || row >= table.getRowCount()) {
                    return;
                }
                if (table.getSelectedRowCount() == 0) {
                    table.addRowSelectionInterval(row, row);
                }
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteRow = new JMenuItem("Delete Row" + (table.getSelectedRowCount() > 1 ? "s" : ""));
                    deleteRow.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            IntStream.of(table.getSelectedRows())
                                    .boxed()
                                    .sorted(Collections.reverseOrder())
                                    .map(table::convertRowIndexToModel)
                                    .forEach(model::removeRow);
                        }
                    });
                    popup.add(deleteRow);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }/*

            @Override
            public void mouseReleased(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r < 0 || r >= table.getRowCount()) return;
                int rowindex = table.getSelectedRow();
                boolean isContained = false;
                for (int selectedRow : table.getSelectedRows()) {
                    if (r == selectedRow) {
                        isContained = true;
                        break;
                    }
                }
                if (!isContained) {
                    table.setRowSelectionInterval(r, r);
                }
                if (rowindex < 0) return;
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteRow = new JMenuItem("Delete Row");
                    deleteRow.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            IntStream.of(table.getSelectedRows())
                                    .boxed()
                                    .sorted(Collections.reverseOrder())
                                    .map(table::convertRowIndexToModel)
                                    .forEach(model::removeRow);
                        }
                    });
                    popup.add(deleteRow);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }*/
        });

        table.getModel().addTableModelListener(_ -> clearAllButton.setEnabled(table.getRowCount() != 0));
        table.setFillsViewportHeight(true);
        participantScrollPane.setViewportView(table);


        final JPanel startPanel = new JPanel();
        startPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.create().setGridX(0).setGridY(2).setWeightX(1).setFill(GridBagConstraints.BOTH).build();
        contestantPanel.add(startPanel, constraints);

        startPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        ClickableComponent startButton = ClickableComponent.of("Start Race", () -> {
            contentPane.setEnabled(false);
            SwingUtilities.invokeLater(() -> {
                if (table.getRowCount() <= 1) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Not enough race participants to start", "Could not start race", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                for (int i = 0; i < table.getRowCount(); i++) {
                    String name = (String) table.getModel().getValueAt(i, 0);
                    Speed speed = new Speed((int) table.getModel().getValueAt(i, 1), (int) table.getModel().getValueAt(i, 2));
                    int endurance = ((Float) table.getModel().getValueAt(i, 3)).intValue();
                    UUID uuid = (UUID) table.getModel().getValueAt(i, 4);

                    Participant participant = new Participant(name, speed, endurance, uuid);
                    internal.addParticipant(participant);
                }

                internal.getParticipants().forEach((_, participant) -> System.out.println(participant.toString()));
            });
        });
        constraints = ConstraintBuilder.create().setGridX(0).setGridY(0).setWeightX(1).setFill(GridBagConstraints.HORIZONTAL).build();
        startPanel.add(startButton, constraints);

        contentPane.add(mainPanel);

        /*GridBagLayout contentLayout = new GridBagLayout();
        GridBagConstraints contentConstraints = new GridBagConstraints();
        contentConstraints.gridx = 2;
        contentConstraints.gridy = 1;
        contentPane.setLayout(contentLayout);

        JPanel settingsPanel = new JPanel();
        GridBagLayout settingsLayout = new GridBagLayout();
        GridBagConstraints settingsConstraints = new GridBagConstraints();
        settingsPanel.setLayout(settingsLayout);
        settingsPanel.setBorder(getBorder());
        settingsConstraints.weightx = 1;
        settingsConstraints.weighty = 1;

        JPanel contestantPanel = new JPanel();
        GridBagLayout contestantLayout = new GridBagLayout();
        GridBagConstraints contestantConstraints = new GridBagConstraints();
        contestantPanel.setLayout(contestantLayout);


        JPanel manageContestantPanel = new JPanel();
        GridBagLayout manageContestantLayout = new GridBagLayout();
        GridBagConstraints manageContestantConstraints = new GridBagConstraints();
        manageContestantPanel.setLayout(manageContestantLayout);
        manageContestantPanel.setBorder(getBorder());

        addComponent(contestantPanel, manageContestantPanel, contestantLayout, manageContestantConstraints, 0,0,1, 1);

        addComponent(contentPane, settingsPanel, contentLayout, settingsConstraints, 0, 0, 1, 1);
        addComponent(contentPane, contestantPanel, contentLayout, contestantConstraints, 0, 1, 1, 1);
        */
        pack();
    }

    @Deprecated
    public MainFrame(String title) {
        super(title);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainFrame.this.setVisible(false);
                MainFrame.this.dispose();
            }
        });

        setPreferredSize(new Dimension(1080, 720));
        Container contentPane = getContentPane();

        JPanel gameSettings = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSize(new Dimension((contentPane.getWidth() / 2), contentPane.getHeight()));
                this.setLocation(0, 0);
                super.paintComponent(g);
            }
        };

        gameSettings.setBackground(UIManager.getColor("Panel.background").darker());

        GridBagLayout settingsLayout = new GridBagLayout();
        GridBagConstraints settingsConstraints = new GridBagConstraints();

        gameSettings.setLayout(settingsLayout);

        JPanel participantMenu = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSize(new Dimension((contentPane.getWidth() / 2), (contentPane.getHeight() / 2)));
                this.setLocation((contentPane.getWidth() / 2), 0);
                super.paintComponent(g);
            }
        };

        participantMenu.setBorder(new LineBorder(UIManager.getColor("Panel.background"), 5));
        participantMenu.setBackground(UIManager.getColor("Panel.background").darker());

        GridBagLayout participantMenuLayout = new GridBagLayout();
        GridBagConstraints participantMenuConstraints = new GridBagConstraints();
        participantMenu.setLayout(participantMenuLayout);


        String[] columns = {"Name", "Min Speed", "Max Speed", "Endurance", "UUID"};
        DefaultTableModel model = new DefaultTableModel(null, columns);
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane() {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSize(new Dimension((contentPane.getWidth() / 2), (contentPane.getHeight() / 2) - (20)));
                this.setLocation((contentPane.getWidth() / 2), (contentPane.getHeight() / 2));
                this.getViewport().setViewSize(this.getSize());
                super.paintComponent(g);
            }
        };
        scrollPane.setBorder(new LineBorder(UIManager.getColor("Panel.background"), 5));

        scrollPane.getViewport().add(table);
        scrollPane.updateUI();

        GameInternal internal = new GameInternal(this);
        ClickableComponent start = ClickableComponent.of("Start Game", () -> {
            table.setEnabled(false);
            participantMenu.setEnabled(false);
            gameSettings.setEnabled(false);
            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < table.getRowCount(); i++) {
                    String name = (String) table.getModel().getValueAt(i, 0);
                    Speed speed = new Speed((int) table.getModel().getValueAt(i, 1), (int) table.getModel().getValueAt(i, 2));
                    int endurance = (int) table.getModel().getValueAt(i, 3);
                    UUID uuid = UUID.fromString((String) table.getModel().getValueAt(i, 4));

                    Participant participant = new Participant(name, speed, endurance, uuid);
                    internal.addParticipant(participant);
                }

                internal.getParticipants().forEach((_, participant) -> System.out.println(participant.toString()));
            });
        });

        JPanel startPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSize(new Dimension((contentPane.getWidth() / 2), 20));
                this.setLocation((contentPane.getWidth() / 2), contentPane.getHeight() - 20);
                super.paintComponent(g);
            }
        };
        startPanel.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
        GridBagLayout startPanelLayout = new GridBagLayout();
        GridBagConstraints startPanelConstraints = new GridBagConstraints();
        startPanel.setLayout(startPanelLayout);
        startPanelConstraints.fill = GridBagConstraints.HORIZONTAL;

        GridBagLayout contentPaneLayout = new GridBagLayout();
        GridBagConstraints contentPaneConstraints = new GridBagConstraints();
        contentPane.setLayout(contentPaneLayout);
        contentPaneConstraints.ipadx = 5;
        contentPaneConstraints.ipady = 5;
        addComponent(contentPane, gameSettings, contentPaneLayout, contentPaneConstraints, 0, 0, 1, 1);
        addComponent(contentPane, participantMenu, contentPaneLayout, contentPaneConstraints, 0, 1, 1, 1);
        addComponent(contentPane, scrollPane, contentPaneLayout, contentPaneConstraints, 1, 1, 1, 1);

        addComponent(startPanel, start, startPanelLayout, startPanelConstraints, 0, 0, 1, 1);
        contentPaneConstraints.fill = GridBagConstraints.HORIZONTAL;
        addComponent(contentPane, startPanel, contentPaneLayout, contentPaneConstraints, 2, 0, 1, 1);

        addComponent(gameSettings, new JLabel("Wowee"), settingsLayout, settingsConstraints, 0, 0, 1, 1);
        addComponent(gameSettings, new JLabel("Wowwww"), settingsLayout, settingsConstraints, 1, 0, 1, 1);

        table.setDragEnabled(true);

        table.putClientProperty("terminateEditOnFocusLost", true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r < 0 || r >= table.getRowCount()) return;
                int rowindex = table.getSelectedRow();
                boolean isContained = false;
                for (int selectedRow : table.getSelectedRows()) {
                    if (r == selectedRow) {
                        isContained = true;
                        break;
                    }
                }
                if (!isContained) {
                    table.setRowSelectionInterval(r, r);
                }
                if (rowindex < 0) return;
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteRow = new JMenuItem("Delete Row");
                    deleteRow.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            IntStream.of(table.getSelectedRows())
                                    .boxed()
                                    .sorted(Collections.reverseOrder())
                                    .map(table::convertRowIndexToModel)
                                    .forEach(model::removeRow);
                        }
                    });
                    popup.add(deleteRow);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        addComponent(participantMenu, FieldComponentPair.of(
                "New Contestant",
                ClickableComponent.of("Add", () -> {
                    ContestantFrame frame = new ContestantFrame();
                    int user = JOptionPane.showOptionDialog(MainFrame.this, frame, "Add Contestant", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] {"Add", "Discard"}, 0);
                    if (user == JOptionPane.YES_OPTION) {
                        Participant participant = new Participant(frame.getName(), frame.getSpeed(), frame.getEndurance());

                        //internal.addParticipant(participant);
                        SwingUtilities.invokeLater(() -> {
                            Object[] data = participant.tableData();
                            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                            tableModel.addRow(data);
                        });
                    }
                })), participantMenuLayout, participantMenuConstraints, 0, 0, 1, 1);
        pack();
    }
}
