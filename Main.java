import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame {
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField limitField;
    private final JLabel totalLabel;
    private final JLabel averageLabel;
    private final JList<Integer> overList;
    private final JList<Integer> underList;
    private final ProductSales productSales = new ProductSales();

    // new UI components
    private final JTextArea displayArea;
    private final JLabel yearsLabel;
    private final JButton loadBtn;
    private final JButton saveBtn;

    // in-memory default product sales data (makes it available to Load)
    private final int[][] defaultData = {
        {300, 150, 700}, // Year 1: 300 microphones, 150 speakers, 700 mixing desks
        {250, 200, 600}  // Year 2: 250 microphones, 200 speakers, 600 mixing desks
    };
    private final String[] defaultCols = {"Microphones", "Speakers", "Mixing Desks"};

    public Main() {
        super("Product Sales - Sound Equipment Retailer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        // initialize table with in-memory default data
        Object[][] sample = toObjectArray(defaultData);
        tableModel = new DefaultTableModel(sample, defaultCols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Integer.class;
            }
        };
        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(400, 80));

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.setBorder(BorderFactory.createTitledBorder("Product Sales (Editable)"));
        topPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Controls and results
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        controlPanel.add(new JLabel("Sales Limit:"));
        limitField = new JTextField("500", 6);
        controlPanel.add(limitField);

        JButton calcBtn = new JButton("Calculate");
        controlPanel.add(calcBtn);

        // Load and Save buttons
        loadBtn = new JButton("Load");
        saveBtn = new JButton("Save");
        controlPanel.add(loadBtn);
        controlPanel.add(saveBtn);

        // years label (read-only display)
        yearsLabel = new JLabel("Years processed: " + tableModel.getRowCount());
        controlPanel.add(yearsLabel);

        // Results panel
        JPanel results = new JPanel(new GridLayout(1, 3, 10, 10));
        results.setBorder(BorderFactory.createTitledBorder("Report"));

        JPanel summary = new JPanel(new GridLayout(4, 1));
        totalLabel = new JLabel("Total: ");
        averageLabel = new JLabel("Average: ");
        summary.add(totalLabel);
        summary.add(averageLabel);
        results.add(summary);

        JPanel overPanel = new JPanel(new BorderLayout());
        overPanel.setBorder(BorderFactory.createTitledBorder("Sales >= Limit"));
        overList = new JList<>(new DefaultListModel<>());
        overPanel.add(new JScrollPane(overList), BorderLayout.CENTER);
        results.add(overPanel);

        JPanel underPanel = new JPanel(new BorderLayout());
        underPanel.setBorder(BorderFactory.createTitledBorder("Sales < Limit"));
        underList = new JList<>(new DefaultListModel<>());
        underPanel.add(new JScrollPane(underList), BorderLayout.CENTER);
        results.add(underPanel);

        // Text area to display product sales data (read-only)
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane displayScroll = new JScrollPane(displayArea);
        displayScroll.setPreferredSize(new Dimension(360, 300));
        displayScroll.setBorder(BorderFactory.createTitledBorder("Product Sales Data"));

        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.add(controlPanel, BorderLayout.NORTH);
        centerPanel.add(results, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout(8, 8));
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(displayScroll, BorderLayout.EAST);

        // Action listeners
        calcBtn.addActionListener(e -> calculateAndDisplay());
        // Load should populate table from in-memory default data and recalculate
        loadBtn.addActionListener(e -> loadFromMemory());
        // Save should write the text area contents to data.txt on the user's Desktop
        saveBtn.addActionListener(e -> saveDisplayToDataTxt());

        // initial calculation and display
        calculateAndDisplay();
    }

    private Object[][] toObjectArray(int[][] src) {
        Object[][] out = new Object[src.length][src[0].length];
        for (int r = 0; r < src.length; r++) {
            for (int c = 0; c < src[r].length; c++) out[r][c] = src[r][c];
        }
        return out;
    }

    private void calculateAndDisplay() {
        int rows = tableModel.getRowCount();
        int cols = tableModel.getColumnCount();
        int[][] data = new int[rows][cols];

        try {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    Object val = tableModel.getValueAt(r, c);
                    if (val == null) val = 0;
                    data[r][c] = (val instanceof Number) ? ((Number) val).intValue() : Integer.parseInt(val.toString().trim());
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integer sales values.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int total = productSales.TotalSales(data);
        double avg = productSales.AverageSales(data);

        int limit;
        try {
            limit = Integer.parseInt(limitField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid sales limit. Using default 500.", "Input Warning", JOptionPane.WARNING_MESSAGE);
            limit = 500;
            limitField.setText("500");
        }

        ArrayList<Integer> over = new ArrayList<>();
        ArrayList<Integer> under = new ArrayList<>();
        for (int[] year : data) {
            for (int v : year) {
                if (v >= limit) over.add(v); else under.add(v);
            }
        }

        totalLabel.setText(String.format("Total: %,d", total));
        averageLabel.setText(String.format("Average: %,.2f", avg));
        yearsLabel.setText("Years processed: " + rows);

        DefaultListModel<Integer> overModel = new DefaultListModel<>();
        for (Integer v : over) overModel.addElement(v);
        overList.setModel(overModel);

        DefaultListModel<Integer> underModel = new DefaultListModel<>();
        for (Integer v : under) underModel.addElement(v);
        underList.setModel(underModel);

        updateDisplayArea(data);
    }

    // update the text area with formatted product sales data
    private void updateDisplayArea(int[][] data) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s", "Year"));
        for (int c = 0; c < tableModel.getColumnCount(); c++) {
            sb.append(String.format("%-15s", tableModel.getColumnName(c)));
        }
        sb.append(String.format("%-12s%-12s%n", "YearTotal", "YearAvg"));
        for (int r = 0; r < data.length; r++) {
            int yearTotal = 0;
            sb.append(String.format("Year %-3d", r + 1));
            for (int c = 0; c < data[r].length; c++) {
                sb.append(String.format("%-15d", data[r][c]));
                yearTotal += data[r][c];
            }
            double yearAvg = (data[r].length > 0) ? (double) yearTotal / data[r].length : 0.0;
            sb.append(String.format("%-12d%-12.2f%n", yearTotal, yearAvg));
        }
        displayArea.setText(sb.toString());
    }

    // load in-memory default product sales into the table and recalculate
    private void loadFromMemory() {
        Object[][] tableData = toObjectArray(defaultData);
        tableModel.setDataVector(tableData, defaultCols);
        // Keep cells editable
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(new JTextField()));
        }
        calculateAndDisplay();
        JOptionPane.showMessageDialog(this, "Loaded product sales data from memory.", "Load", JOptionPane.INFORMATION_MESSAGE);
    }

    // save the contents of the displayArea to data.txt on the user's Desktop
    private void saveDisplayToDataTxt() {
        String text = displayArea.getText();
        if (text == null) text = "";
        File desktop = new File(System.getProperty("user.home"), "Desktop");
        File out = new File(desktop, "data.txt");
        try (PrintWriter pw = new PrintWriter(new FileWriter(out))) {
            pw.print(text);
            JOptionPane.showMessageDialog(this, "Report saved to " + out.getAbsolutePath(), "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // existing CSV load/save methods remain available if needed
    private void saveToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int res = chooser.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) file = new File(file.getAbsolutePath() + ".csv");

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            int rows = tableModel.getRowCount();
            int cols = tableModel.getColumnCount();
            for (int r = 0; r < rows; r++) {
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < cols; c++) {
                    Object val = tableModel.getValueAt(r, c);
                    line.append((val == null) ? "0" : val.toString());
                    if (c < cols - 1) line.append(",");
                }
                pw.println(line.toString());
            }
            JOptionPane.showMessageDialog(this, "Data saved to " + file.getAbsolutePath(), "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));
        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();

        ArrayList<int[]> rowsList = new ArrayList<>();
        int maxCols = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int[] row = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    row[i] = Integer.parseInt(parts[i].trim());
                }
                rowsList.add(row);
                if (parts.length > maxCols) maxCols = parts.length;
            }
        } catch (IOException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (rowsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "File contains no data.", "Load", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // prepare column names Q1..Qn
        String[] cols = new String[maxCols];
        for (int i = 0; i < maxCols; i++) cols[i] = "Q" + (i + 1);

        // build table data, fill missing columns with 0
        Object[][] tableData = new Object[rowsList.size()][maxCols];
        for (int r = 0; r < rowsList.size(); r++) {
            int[] row = rowsList.get(r);
            for (int c = 0; c < maxCols; c++) {
                tableData[r][c] = (c < row.length) ? row[c] : 0;
            }
        }

        tableModel.setDataVector(tableData, cols);
        // ensure column class remains Integer
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(new JTextField()));
        }

        // update UI
        calculateAndDisplay();
        JOptionPane.showMessageDialog(this, "Data loaded from " + file.getAbsolutePath(), "Load", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Ensure GUI runs on EDT
        SwingUtilities.invokeLater(() -> {
            Main gui = new Main();
            gui.setVisible(true);
        });
    }
}
