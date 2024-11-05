package me.vlink102;

import me.vlink102.game.GameInternal;
import me.vlink102.game.RaceFrame;
import me.vlink102.internal.ConstraintBuilder;
import me.vlink102.internal.FileManager;
import me.vlink102.objects.ContestantModelComparator;
import me.vlink102.objects.FrameAdapter;
import me.vlink102.objects.Participant;
import me.vlink102.objects.Speed;
import me.vlink102.objects.ui.ClickableComponent;
import me.vlink102.objects.ui.ContestantFrame;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

public class MainFrame extends FrameAdapter {

    private static final String[] columns = {"Name", "Min Speed", "Max Speed", "Endurance", "UUID"};
    private final JSpinner raceLengthSpinner;
    private final JSpinner roundIntervalSpinner;

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

        Container contentPane = getContentPane();

        final JPanel mainPanel = new JPanel();
        final JPanel settings = new JPanel();

        mainPanel.setLayout(new GridBagLayout());
        settings.setLayout(new GridBagLayout());

        GridBagConstraints constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        mainPanel.add(settings, constraints);
        settings.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                null,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                null,
                null)
        );


        final JLabel settingsLabel = new JLabel("Settings");
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).setAnchor(GridBagConstraints.NORTH).build();
        settings.add(settingsLabel, constraints);

        final JScrollPane settingsScrollPane = new JScrollPane();
        settingsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        settingsScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        settings.add(settingsScrollPane, constraints);

        final JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridBagLayout());
        settingsPanel.setAutoscrolls(false);

        settingsScrollPane.setViewportView(settingsPanel);

        final JLabel raceLengthLabel = new JLabel("Race Length");
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(0.5f).setAnchor(GridBagConstraints.WEST).build();
        settingsPanel.add(raceLengthLabel, constraints);

        this.raceLengthSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 10));
        constraints = ConstraintBuilder.builder().setGridX(1).setGridY(0).setWeightX(0.5f).setAnchor(GridBagConstraints.EAST).build();
        settingsPanel.add(raceLengthSpinner, constraints);

        final JLabel roundIntervalLabel = new JLabel("Round Interval (s):");
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(1).setWeightX(0.5f).setAnchor(GridBagConstraints.WEST).build();
        settingsPanel.add(roundIntervalLabel, constraints);

        this.roundIntervalSpinner = new JSpinner(new SpinnerNumberModel(500, 50, 5000, 100));
        constraints = ConstraintBuilder.builder().setGridX(1).setGridY(1).setWeightX(0.5f).setAnchor(GridBagConstraints.EAST).build();
        settingsPanel.add(roundIntervalSpinner, constraints);

        final JPanel contestantPanel = new JPanel();
        contestantPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.builder().setGridX(1).setGridY(0).setWeightX(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        mainPanel.add(contestantPanel, constraints);

        final JPanel contestantManagerPanel = new JPanel();
        contestantManagerPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).setFill(GridBagConstraints.BOTH).build();
        contestantPanel.add(contestantManagerPanel, constraints);
        contestantManagerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));

        final DefaultTableModel model = getTableModel();
        final JTable table = getParticipantTable(model);

        final JButton clearAllButton = getClearButton(table);
        clearAllButton.setEnabled(false);
        constraints = ConstraintBuilder.builder().setGridX(1).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        contestantManagerPanel.add(clearAllButton, constraints);

        final JPanel addInsetPanel = new JPanel();
        addInsetPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        contestantManagerPanel.add(addInsetPanel, constraints);

        final JButton addButton = ClickableComponent.of("Add Contestant", _ -> {
            ContestantFrame frame = new ContestantFrame();
            int user = JOptionPane.showOptionDialog(
                    MainFrame.this,
                    frame,
                    "Add Contestant",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[]{"Add", "Discard"},
                    0
            );
            if (user == JOptionPane.YES_OPTION) {
                Participant participant = new Participant(frame.getName(), frame.getSpeed(), frame.getEndurance());

                //internal.addParticipant(participant);
                SwingUtilities.invokeLater(() -> {
                    Object[] data = participant.tableData();
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    tableModel.addRow(data);
                    table.doLayout();
                });
            }
        });
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.HORIZONTAL).build();
        addInsetPanel.add(addButton, constraints);

        final JButton randomContestant = ClickableComponent.of("Generate Random", _ -> {
            Participant randomParticipant = Participant.generateRandom();
            SwingUtilities.invokeLater(() -> {
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.addRow(randomParticipant.tableData());
                table.doLayout();
            });
        });
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(1).setFill(GridBagConstraints.HORIZONTAL).build();
        addInsetPanel.add(randomContestant, constraints);

        final JPanel fileManagerPanel = new JPanel();
        fileManagerPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.builder().setGridX(2).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.BOTH).build();
        contestantManagerPanel.add(fileManagerPanel, constraints);

        FileManager manager = new FileManager(this);



        final JScrollPane participantScrollPane = new JScrollPane();
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(1).setWeightX(1).setWeightY(0.3f).setFill(GridBagConstraints.BOTH).build();
        contestantPanel.add(participantScrollPane, constraints);
        participantScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));


        table.putClientProperty("terminateEditOnFocusLost", true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event.getID() == MouseEvent.MOUSE_CLICKED) {
                MouseEvent mouseEvent = (MouseEvent) event;
                int row = table.rowAtPoint(mouseEvent.getPoint());
                if (row == -1) {
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
            }
        });

        table.getModel().addTableModelListener(_ -> clearAllButton.setEnabled(table.getRowCount() != 0));
        table.setFillsViewportHeight(true);
        participantScrollPane.setViewportView(table);


        final JPanel startPanel = new JPanel();
        startPanel.setLayout(new GridBagLayout());
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(2).setWeightX(1).setFill(GridBagConstraints.BOTH).build();
        contestantPanel.add(startPanel, constraints);

        startPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        GameInternal internal = new GameInternal(this);

        final JButton saveToFileButton = new ClickableComponent("Save to File", _ -> {
            ArrayList<Participant> participants = new ArrayList<>();
            for (int i = 0; i < table.getRowCount(); i++) {
                String name = (String) table.getModel().getValueAt(i, 0);
                Speed speed = new Speed((int) table.getModel().getValueAt(i, 1), (int) table.getModel().getValueAt(i, 2));
                int endurance = ((Float) table.getModel().getValueAt(i, 3)).intValue();
                UUID uuid = UUID.fromString(table.getModel().getValueAt(i, 4).toString());

                participants.add(new Participant(name, speed, endurance, uuid));
            }
            manager.serializeParticipants(participants);
        });
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.HORIZONTAL).build();
        fileManagerPanel.add(saveToFileButton, constraints);

        final JButton importFromFileButton = new ClickableComponent("Import from File", _ -> {
            List<Participant> deserialized = manager.deserializeParticipants();

            for (Participant participant : deserialized) {
                Object[] data = participant.tableData();
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.addRow(data);
                table.doLayout();
            }
        });
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(1).setFill(GridBagConstraints.HORIZONTAL).build();
        fileManagerPanel.add(importFromFileButton, constraints);

        ClickableComponent startButton = ClickableComponent.of("Start Race", component -> {
            if (table.getRowCount() <= 1) {
                JOptionPane.showMessageDialog(
                        MainFrame.this,
                        "Not enough race participants to start",
                        "Could not start race",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            table.clearSelection();
            participantScrollPane.setEnabled(false);
            addButton.setEnabled(false);
            randomContestant.setEnabled(false);
            clearAllButton.setEnabled(false);
            settingsScrollPane.setEnabled(false);
            component.setText("Starting...");
            component.setEnabled(false);
            SwingUtilities.invokeLater(() -> {
                for (int i = 0; i < table.getRowCount(); i++) {
                    String name = (String) table.getModel().getValueAt(i, 0);
                    Speed speed = new Speed(
                            (int) table.getModel().getValueAt(i, 1),
                            (int) table.getModel().getValueAt(i, 2)
                    );
                    int endurance = ((Float) table.getModel().getValueAt(i, 3)).intValue();
                    UUID uuid = UUID.fromString(table.getModel().getValueAt(i, 4).toString());

                    Participant participant = new Participant(name, speed, endurance, uuid);
                    internal.addParticipant(participant);
                }

                internal.getParticipants().forEach((_, participant) -> System.out.println(participant.toString()));
                RaceFrame frame = new RaceFrame(internal, this);
                frame.requestFocus();
            });
        });
        constraints = ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).setFill(GridBagConstraints.HORIZONTAL).build();
        startPanel.add(startButton, constraints);

        contentPane.add(mainPanel);
        pack();
    }

    private DefaultTableModel getTableModel() {
        return new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 4;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 1, 2 -> Integer.class;
                    case 3 -> Float.class;
                    case 4 -> UUID.class;
                    default -> String.class;
                };
            }
        };
    }

    private JTable getParticipantTable(DefaultTableModel model) {
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(model);
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };
        table.setRowSorter(rowSorter);
        for (int i = 0; i < columns.length; i++) {
            rowSorter.setComparator(i, new ContestantModelComparator());
        }
        table.setDragEnabled(true);
        return table;
    }

    private JButton getClearButton(JTable table) {
        return ClickableComponent.of("Clear All", _ -> {
            int user = JOptionPane.showConfirmDialog(MainFrame.this, "Are you sure?");

            if (user == JOptionPane.YES_OPTION) {
                ((DefaultTableModel) table.getModel()).setRowCount(0);
            }
        });
    }

    public int getRaceLength() {
        return (int) raceLengthSpinner.getValue();
    }

    public int getRoundIntervalMs() {
        return (int) roundIntervalSpinner.getValue();
    }
}
