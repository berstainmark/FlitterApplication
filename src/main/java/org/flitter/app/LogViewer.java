package org.flitter.app;

import org.flitter.app.service.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

public class LogViewer {

    private static final int MAX_LOG_ROWS = 200;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> sorter;
    private final JFrame frame;

    private JSONArray logsArray;

    public LogViewer() {
        frame = new JFrame("Viewer");
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"Time", "Level", "Name", "Description"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public String getColumnName(int column) {
                return columnNames[column];
            }
        };

        table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(0, new Comparator<String>() {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            @Override
            public int compare(String o1, String o2) {
                LocalDateTime dateTime1 = LocalDateTime.parse(o1, formatter);
                LocalDateTime dateTime2 = LocalDateTime.parse(o2, formatter);
                return dateTime1.compareTo(dateTime2);
            }
        });
        table.setRowSorter(sorter);


        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col >= 0) {
                    String name = (String) table.getValueAt(row, 2);
                    String description = (String) table.getValueAt(row, 3);
                    showDialog(name, description);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearLogs();
                deleteLogs();
                updateTable();
            }
        });
        frame.add(clearButton, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void showDialog(String name, String description) {
        StringBuilder sb = new StringBuilder("<html><body style='width: 400px'>");
        int index = 0;
        while (index < description.length()) {
            sb.append(description, index, Math.min(index + 100, description.length()));
            if (index + 100 < description.length()) {
                sb.append("<br>");
            }
            index += 100;
        }

        sb.append("</body></html>");
        String formattedDescription = sb.toString();

        ImageIcon icon = createCustomIcon();
        JOptionPane.showMessageDialog(frame, formattedDescription, name, JOptionPane.INFORMATION_MESSAGE, icon);
    }

    private ImageIcon createCustomIcon() {
        Utils utils = new Utils();
        ImageIcon icon = utils.readImage("flitterRounded.png");
        Image image = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    public void loadLogs(String filePath) {
        try {

            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            logsArray = new JSONArray(content);


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


            int startIndex = Math.max(0, logsArray.length() - MAX_LOG_ROWS);


            for (int i = startIndex; i < logsArray.length(); i++) {
                JSONObject log = logsArray.getJSONObject(i);
                if (log.has("time")) {
                    String time = log.getString("time");


                    try {
                        LocalDateTime.parse(time, formatter);
                    } catch (DateTimeParseException e) {
                        continue;
                    }
                    if (log.has("level") && log.has("name") && log.has("description")) {
                        tableModel.addRow(new Object[]{
                                time,
                                log.getString("level"),
                                log.getString("name"),
                                log.getString("description")
                        });
                    }
                }
            }


            sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));


            TableColumnAdjuster.adjustColumns(table);


            TableColumn columnDescription = table.getColumnModel().getColumn(3);
            columnDescription.setMaxWidth(400);

        } catch (IOException e) {

        }
    }

    private void clearLogs() {
        tableModel.setRowCount(0);
    }

    private void deleteLogs() {
        logsArray = new JSONArray();


        try (FileWriter fileWriter = new FileWriter(PrivateConfig.LOG_URL + "/flitter-log.json")) {
            fileWriter.write(logsArray.toString());
            fileWriter.flush();
        } catch (IOException e) {

        }
    }

    private void updateTable() {

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.removeRow(i);
        }
        tableModel.setRowCount(0);
    }

    public JFrame getFrame() {
        return frame;
    }

    static class TableColumnAdjuster {

        public static void adjustColumns(JTable table) {
            TableColumnModel columnModel = table.getColumnModel();
            FontMetrics fontMetrics = table.getFontMetrics(table.getFont());

            for (int col = 0; col < table.getColumnCount(); col++) {
                int width = 0;

                for (int row = 0; row < table.getRowCount(); row++) {
                    TableCellRenderer renderer = table.getCellRenderer(row, col);
                    Component comp = table.prepareRenderer(renderer, row, col);
                    width = Math.max(comp.getPreferredSize().width + 5, width);
                }

                TableColumn column = columnModel.getColumn(col);
                column.setPreferredWidth(Math.max(width, fontMetrics.stringWidth(column.getHeaderValue().toString())));
            }
        }
    }
}
