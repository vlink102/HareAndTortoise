package me.vlink102.internal;

import lombok.Getter;
import me.vlink102.objects.ContestantModelComparator;
import me.vlink102.objects.Participant;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class ResultsDialog extends JDialog {

    private static final String[] columns = {"Rank", "Participant", "Best Round (m)"};

    @Getter
    private static JTable table;

    private static DefaultTableModel getTableModel() {
        return new DefaultTableModel(null, columns) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 1, 3 -> Integer.class;
                    case 2 -> String.class;
                    default -> String.class;
                };
            }
        };
    }

    private static JTable getParticipantTable(DefaultTableModel model) {
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
    public ResultsDialog(JFrame parent, List<List<Participant>> winnerMap, HashMap<Participant, Integer> bestRound) {
        super(parent, true);


        setTitle("Results");
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);

        final DefaultTableModel model = getTableModel();
        table = getParticipantTable(model);

        table.putClientProperty("terminateEditOnFocusLost", true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        /*table.addMouseListener(new MouseAdapter() {
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
        });*/
        table.setFillsViewportHeight(true);
        final JScrollPane participantScrollPane = new JScrollPane();
        participantScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        participantScrollPane.setViewportView(table);
        participantScrollPane.setPreferredSize(new Dimension(800, 600));

        add(participantScrollPane);

        /*winnerMap.forEach((integer, winners) -> {
            for (Participant winner : winners) {
                ((DefaultTableModel) getTable().getModel()).addRow(new Object[] {integer, winner.getName(), 0});
            }
        });*/

        winnerMap.forEach(participants -> {
            for (int i = 0; i < participants.size(); i++) {
                Participant winner = participants.get(i);
                getTable().setGridColor(switch (i) {
                    case 0 -> Color.GREEN;
                    case 1 -> Color.orange;
                    case 2 -> Color.yellow;
                    default -> new Color(0, 0, 0, 0);
                });

                ((DefaultTableModel) getTable().getModel()).addRow(new Object[] {i + 1, winner.getName(), bestRound.get(winner)});
            }
        });

        pack();
        setVisible(true);
    }
}
