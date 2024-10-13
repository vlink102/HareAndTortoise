package me.vlink102;

import lombok.Getter;
import me.vlink102.game.GameInternal;
import me.vlink102.internal.ConstraintBuilder;
import me.vlink102.objects.ContestantModelComparator;
import me.vlink102.objects.FrameAdapter;
import me.vlink102.objects.Participant;
import me.vlink102.objects.Speed;
import me.vlink102.objects.ui.ClickableComponent;
import me.vlink102.objects.ui.ContestantFrame;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class MainFrame extends FrameAdapter {

    public class GridBagPanel extends JPanel {
        public GridBagPanel() {
            super(new GridBagLayout());
        }

        public GridBagPanel(Consumer<JPanel> consumer) {
            this();
            consumer.accept(this);
        }
    }

    public class CustomPanel extends JPanel {
        public CustomPanel(JComponent parent, GridBagConstraints constraints, Consumer<JPanel> consumer) {
            this(parent, constraints);
            consumer.accept(this);
        }
        public CustomPanel(JComponent parent, GridBagConstraints constraints) {
            super();
            parent.add(this, constraints);
        }
    }

    private static Class<?>[] getClasses(Object... params) {
        Class<?>[] classes = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            switch (params[i]) {
                case Integer _ -> classes[i] = int.class;
                case Boolean _ -> classes[i] = boolean.class;
                case Double _ -> classes[i] = double.class;
                case Long _ -> classes[i] = long.class;
                case Float _ -> classes[i] = float.class;
                case Character _ -> classes[i] = char.class;
                case Byte _ -> classes[i] = byte.class;
                case Short _ -> classes[i] = short.class;
                case null, default -> {
                    assert params[i] != null;
                    classes[i] = params[i].getClass();
                }
            }
        }
        return classes;
    }

    @Getter
    public static class CustomComponent<T extends JComponent> extends JComponent {
        private final T component;
        public CustomComponent(Class<T> componentClass, JComponent parent, GridBagConstraints constraints, Consumer<T> consumer, Object... params) {
            this(componentClass, parent, constraints, params);
            consumer.accept(component);
        }

        public CustomComponent(Class<T> componentClass, JComponent parent, GridBagConstraints constraints, Object... params) {
            try {
                Class<?>[] classes = getClasses(params);
                component = componentClass.getConstructor(classes).newInstance(params);
                parent.add(component, constraints);
            } catch (Exception e) {
                throw new RuntimeException("Error creating component", e);
            }
        }

        @Deprecated
        public CustomComponent(JComponent existing, JComponent parent, GridBagConstraints constraints, Consumer<T> consumer) {
            this(existing, parent, constraints);
            consumer.accept(component);
        }

        @Deprecated
        public CustomComponent(JComponent existing, JComponent parent, GridBagConstraints constraints) {
            component = (T) existing;
            parent.add(component, constraints);
        }
    }

    private static final String[] columns = {"Name", "Min Speed", "Max Speed", "Endurance", "UUID"};

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
        table.putClientProperty("terminateEditOnFocusLost", true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);

        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0 || row >= table.getRowCount()) return;
                if (table.getSelectedRowCount() == 0) table.addRowSelectionInterval(row, row);
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem deleteRow = new JMenuItem("Delete Row" + (table.getSelectedRowCount() > 1 ? "s" : ""));
                    deleteRow.addActionListener(new AbstractAction() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            IntStream.of(table.getSelectedRows()).boxed().sorted(Collections.reverseOrder()).map(table::convertRowIndexToModel).forEach(model::removeRow);
                        }
                    });
                    popup.add(deleteRow);

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if(event.getID() == MouseEvent.MOUSE_CLICKED) {
                MouseEvent mouseEvent = (MouseEvent) event;
                int row = table.rowAtPoint(mouseEvent.getPoint());
                if(row == -1) table.clearSelection();
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
        return table;
    }

    private JButton getClearButton(JTable table) {
        JButton button = ClickableComponent.of("Clear All", _ -> {
            int user = JOptionPane.showConfirmDialog(MainFrame.this, "Are you sure?");

            if (user == JOptionPane.YES_OPTION) {
                ((DefaultTableModel) table.getModel()).setRowCount(0);
            }
        });
        button.setEnabled(false);
        return button;
    }

    protected void init() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainFrame.this.setVisible(false);
                MainFrame.this.dispose();
            }
        });

        setPreferredSize(new Dimension(1080, 720));
    }

    /**
     * Explicit type is not a declared constructor for spinner
     * {@link JSpinner#JSpinner(SpinnerModel)}
     */
    public MainFrame() {
        super("Hare and Tortoise v2");
        init();
        GameInternal internal = new GameInternal(this);
        Container contentPane = getContentPane();

        final JPanel mainPanel = new GridBagPanel();
        final JPanel settings = new CustomComponent<>(JPanel.class, mainPanel, ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build(), panel -> panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null))).getComponent();

        final JLabel settingsLabel = new CustomComponent<>(JLabel.class, settings, ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).setAnchor(GridBagConstraints.NORTH).build(), "Settings").getComponent();

        final JPanel settingsPanel = new GridBagPanel(panel -> panel.setAutoscrolls(false));

        final JScrollPane settingsScrollPane = new CustomComponent<>(JScrollPane.class, settings, ConstraintBuilder.builder().setGridX(0).setGridY(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build(), pane -> pane.setViewportView(settingsPanel), ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER).getComponent();

        GridBagConstraints constraints = new GridBagConstraints();
        final JLabel raceLengthLabel = new CustomComponent<>(JLabel.class, settingsPanel, ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(0.5f).setAnchor(GridBagConstraints.WEST).build(), "Race Length").getComponent();

        final JSpinner raceLengthSpinner = new CustomComponent<>(JSpinner.class, settingsPanel, ConstraintBuilder.builder().setGridX(1).setGridY(0).setWeightX(0.5f).setAnchor(GridBagConstraints.EAST).build()).getComponent();
        raceLengthSpinner.setModel(new SpinnerNumberModel(100, 1, 10000, 10));

        final JPanel contestantPanel = new CustomComponent<>(JPanel.class, mainPanel, ConstraintBuilder.builder().setGridX(1).setGridY(0).setWeightX(1).setWeightY(1).setFill(GridBagConstraints.BOTH).build()).getComponent();

        final JPanel contestantManagerPanel = new CustomComponent<>(JPanel.class, contestantPanel, ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).setFill(GridBagConstraints.BOTH).build(), panel -> panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null))).getComponent();

        final DefaultTableModel model = getTableModel();
        final JTable table = getParticipantTable(model);

        final JButton clearAllButton = new CustomComponent<JButton>(getClearButton(table), contestantManagerPanel, ConstraintBuilder.builder().setGridX(1).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.BOTH).build()).getComponent();
        table.getModel().addTableModelListener(_ -> clearAllButton.setEnabled(table.getRowCount() != 0));

        final JPanel addInsetPanel = new CustomComponent<>(JPanel.class, contestantManagerPanel, ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.BOTH).build()).getComponent();

        final JButton addButton = new CustomComponent<JButton>(ClickableComponent.of("Add Contestant", _ -> {
            ContestantFrame frame = new ContestantFrame();
            int user = JOptionPane.showOptionDialog(MainFrame.this, frame, "Add Contestant", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] {"Add", "Discard"}, 0);
            if (user == JOptionPane.YES_OPTION) {
                Participant participant = new Participant(frame.getName(), frame.getSpeed(), frame.getEndurance());

                SwingUtilities.invokeLater(() -> {
                    Object[] data = participant.tableData();
                    DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                    tableModel.addRow(data);
                    table.doLayout();
                });
            }
        }), addInsetPanel, ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(0.5f).setWeightY(1).setFill(GridBagConstraints.HORIZONTAL).build()).getComponent();

        final JButton randomContestant = new CustomComponent<JButton>(ClickableComponent.of("Generate Random", _ -> {
            Participant randomParticipant = Participant.generateRandom();
            SwingUtilities.invokeLater(() -> {
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.addRow(randomParticipant.tableData());
                table.doLayout();
            });
        }), addInsetPanel, ConstraintBuilder.builder().setGridX(0).setGridY(1).setFill(GridBagConstraints.HORIZONTAL).build()).getComponent();

        final JScrollPane participantScrollPane = new CustomComponent<>(JScrollPane.class, contestantPanel, ConstraintBuilder.builder().setGridX(0).setGridY(1).setWeightX(1).setWeightY(0.3f).setFill(GridBagConstraints.BOTH).build(), pane -> {
            pane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
            pane.setViewportView(table);
        }).getComponent();

        final JPanel startPanel = new CustomComponent<>(JPanel.class, contestantPanel, ConstraintBuilder.builder().setGridX(0).setGridY(2).setWeightX(1).setFill(GridBagConstraints.BOTH).build(), panel -> panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null))).getComponent();

        ClickableComponent startButton = new CustomComponent<ClickableComponent>(ClickableComponent.of("Start Race", component -> {
            if (table.getRowCount() <= 1) {
                JOptionPane.showMessageDialog(MainFrame.this, "Not enough race participants to start", "Could not start race", JOptionPane.ERROR_MESSAGE);
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
                    Speed speed = new Speed((int) table.getModel().getValueAt(i, 1), (int) table.getModel().getValueAt(i, 2));
                    int endurance = ((Float) table.getModel().getValueAt(i, 3)).intValue();
                    UUID uuid = UUID.fromString(table.getModel().getValueAt(i, 4).toString());

                    Participant participant = new Participant(name, speed, endurance, uuid);
                    internal.addParticipant(participant);
                }

                internal.getParticipants().forEach((_, participant) -> System.out.println(participant.toString()));
            });
        }), startPanel, ConstraintBuilder.builder().setGridX(0).setGridY(0).setWeightX(1).setFill(GridBagConstraints.HORIZONTAL).build()).getComponent();

        contentPane.add(mainPanel);
        pack();
    }
/*
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
        ClickableComponent start = ClickableComponent.of("Start Game", _ -> {
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
                ClickableComponent.of("Add", _ -> {
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
    }*/
}
