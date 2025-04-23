package view.staff;

import model.OrderHeader;
import model.Report;
import service.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportView extends JPanel {
    private final ReportService reportService;
    private final OrderHeaderService orderHeaderService;
    private final OrderDetailService orderDetailService;
    private final DishService dishService;
    private final IngredientService ingredientService;
    private final VendorService vendorService;
    private final PurchaseOrderHeaderService purchaseOrderHeaderService;
    
    private JTable reportsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton generateButton;
    private JButton viewButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton exportButton;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color WARNING_COLOR = new Color(255, 87, 34);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );

    public ReportView(
            ReportService reportService,
            OrderHeaderService orderHeaderService,
            OrderDetailService orderDetailService,
            DishService dishService,
            IngredientService ingredientService,
            VendorService vendorService,
            PurchaseOrderHeaderService purchaseOrderHeaderService) {
        this.reportService = reportService;
        this.orderHeaderService = orderHeaderService;
        this.orderDetailService = orderDetailService;
        this.dishService = dishService;
        this.ingredientService = ingredientService;
        this.vendorService = vendorService;
        this.purchaseOrderHeaderService = purchaseOrderHeaderService;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(PADDING_BORDER);
        setBackground(Color.WHITE);

        // Create top panel with search and buttons
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(new TitledBorder("Search and Actions"));
        topPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Search field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(Color.WHITE);
        searchField = new JTextField();
        searchField.setBorder(COMPONENT_BORDER);
        searchField.setToolTipText("Search reports by title or type");
        JButton searchButton = new JButton("🔍");
        searchButton.setBorder(COMPONENT_BORDER);
        searchButton.setBackground(SECONDARY_COLOR);
        searchButton.setToolTipText("Search");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        topPanel.add(searchPanel, gbc);

        // Buttons
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 5, 0));
        buttonPanel.setBackground(Color.WHITE);

        generateButton = createStyledButton("📊 Generate", "Generate new report");
        viewButton = createStyledButton("👁️ View", "View selected report");
        deleteButton = createStyledButton("🗑️ Delete", "Delete selected report");
        refreshButton = createStyledButton("🔄 Refresh", "Refresh reports list");
        exportButton = createStyledButton("📥 Export", "Export selected report");

        buttonPanel.add(generateButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);

        topPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Title", "Type", "Generated Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportsTable = new JTable(tableModel);
        reportsTable.setFillsViewportHeight(true);
        reportsTable.setRowHeight(25);
        reportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportsTable.setGridColor(Color.LIGHT_GRAY);
        reportsTable.setShowGrid(true);
        reportsTable.setBackground(Color.WHITE);
        reportsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = reportsTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(reportsTable);
        tableScrollPane.setBorder(new TitledBorder("Reports List"));
        tableScrollPane.setBackground(Color.WHITE);

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        setupEventListeners();
    }

    private JButton createStyledButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(COMPONENT_BORDER);
        button.setToolTipText(tooltip);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return button;
    }

    private void setupEventListeners() {
        generateButton.addActionListener(e -> showGenerateDialog());
        viewButton.addActionListener(e -> viewSelectedReport());
        deleteButton.addActionListener(e -> deleteSelectedReport());
        refreshButton.addActionListener(e -> loadData());
        exportButton.addActionListener(e -> exportSelectedReport());
        searchField.addActionListener(e -> searchReports());
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Report> reports = reportService.findAll();
            for (Report report : reports) {
                tableModel.addRow(new Object[]{
                    report.getId(),
                    report.getTitle(),
                    report.getType(),
                    report.getGeneratedDate().format(DATE_FORMATTER),
                    report.getStatus()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading reports: " + e.getMessage());
        }
    }

    private void showGenerateDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Sales", "Inventory", "Staff", "Customer"});
        JComboBox<String> periodCombo = new JComboBox<>(new String[]{"Daily", "Weekly", "Monthly", "Yearly"});

        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Period:"));
        panel.add(periodCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Generate New Report",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Report report = new Report();
                report.setTitle(titleField.getText());
                report.setType((String) typeCombo.getSelectedItem());
                report.setPeriod((String) periodCombo.getSelectedItem());
                report.setGeneratedDate(LocalDate.now());
                report.setStatus("Processing");

                // Generate report content based on type
                String content = generateReportContent(report.getType(), report.getPeriod());
                report.setContent(content);
                report.setStatus("Completed");

                reportService.create(report);
                loadData();
                JOptionPane.showMessageDialog(this, "Report generated successfully!");
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
            }
        }
    }

    private String generateReportContent(String type, String period) throws RemoteException {
        StringBuilder content = new StringBuilder();
        LocalDate startDate = getStartDate(period);
        LocalDate endDate = LocalDate.now();

        content.append("Report Period: ").append(startDate.format(DATE_FORMATTER))
               .append(" to ").append(endDate.format(DATE_FORMATTER)).append("\n\n");

        switch (type) {
            case "Sales":
                content.append(generateSalesReport(startDate, endDate));
                break;
            case "Inventory":
                content.append(generateInventoryReport());
                break;
            case "Staff":
                content.append(generateStaffReport(startDate, endDate));
                break;
            case "Customer":
                content.append(generateCustomerReport(startDate, endDate));
                break;
        }

        return content.toString();
    }

    private LocalDate getStartDate(String period) {
        LocalDate now = LocalDate.now();
        switch (period) {
            case "Daily":
                return now;
            case "Weekly":
                return now.minusWeeks(1);
            case "Monthly":
                return now.minusMonths(1);
            case "Yearly":
                return now.minusYears(1);
            default:
                return now;
        }
    }

    private String generateSalesReport(LocalDate startDate, LocalDate endDate) throws RemoteException {
        StringBuilder report = new StringBuilder();
        report.append("SALES REPORT\n");
        report.append("====================\n\n");

        // Total sales
        List<OrderHeader> orders = orderHeaderService.findByDateRange(
            startDate.format(DATE_FORMATTER),
            endDate.format(DATE_FORMATTER)
        );

        double totalSales = orders.stream()
            .mapToDouble(OrderHeader::getSubTotal)
            .sum();

        report.append(String.format("Total Sales: $%.2f\n", totalSales));
        report.append(String.format("Total Orders: %d\n\n", orders.size()));

        // Sales by status
        report.append("Orders by Status:\n");
        orders.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                OrderHeader::getStatus,
                java.util.stream.Collectors.counting()
            ))
            .forEach((status, count) -> report.append(String.format("- %s: %d\n", status, count)));

        return report.toString();
    }

    private String generateInventoryReport() throws RemoteException {
        StringBuilder report = new StringBuilder();
        report.append("INVENTORY REPORT\n");
        report.append("====================\n\n");

        List<model.Ingredient> ingredients = ingredientService.findAll();
        
        report.append("Low Stock Items:\n");
        ingredients.stream()
            .filter(i -> i.getStockQuantity() <= i.getReorderPoint())
            .forEach(i -> report.append(String.format("- %s (%.2f %s)\n",
                i.getName(), i.getStockQuantity(), i.getUnitOfMeasure())));

        report.append("\nExpiring Soon:\n");
        ingredients.stream()
            .filter(i -> i.getExpiryDate().isBefore(LocalDate.now().plusDays(30)))
            .forEach(i -> report.append(String.format("- %s (Expires: %s)\n",
                i.getName(), i.getExpiryDate().format(DATE_FORMATTER))));

        return report.toString();
    }

    private String generateStaffReport(LocalDate startDate, LocalDate endDate) throws RemoteException {
        StringBuilder report = new StringBuilder();
        report.append("STAFF PERFORMANCE REPORT\n");
        report.append("====================\n\n");

        List<OrderHeader> orders = orderHeaderService.findByDateRange(
            startDate.format(DATE_FORMATTER),
            endDate.format(DATE_FORMATTER)
        );

        // Orders processed
        report.append(String.format("Total Orders Processed: %d\n", orders.size()));
        
        // Average order processing time
        double avgProcessingTime = orders.stream()
            .filter(o -> o.getStatus().equals("COMPLETED"))
            .mapToLong(o -> o.getOrderDetails().stream()
                .mapToLong(od -> 
                    od.getDeliveryTime() != null ? 
                    java.time.Duration.between(od.getOrderDate(), od.getDeliveryTime()).toMinutes() : 0
                ).sum()
            )
            .average()
            .orElse(0.0);

        report.append(String.format("Average Order Processing Time: %.2f minutes\n", avgProcessingTime));

        return report.toString();
    }

    private String generateCustomerReport(LocalDate startDate, LocalDate endDate) throws RemoteException {
        StringBuilder report = new StringBuilder();
        report.append("CUSTOMER SATISFACTION REPORT\n");
        report.append("====================\n\n");

        List<OrderHeader> orders = orderHeaderService.findByDateRange(
            startDate.format(DATE_FORMATTER),
            endDate.format(DATE_FORMATTER)
        );

        // Order statistics
        long completedOrders = orders.stream()
            .filter(o -> o.getStatus().equals("COMPLETED"))
            .count();
        long canceledOrders = orders.stream()
            .filter(o -> o.getStatus().equals("CANCELED"))
            .count();

        report.append(String.format("Completed Orders: %d\n", completedOrders));
        report.append(String.format("Canceled Orders: %d\n", canceledOrders));
        
        if (orders.size() > 0) {
            double completionRate = (double) completedOrders / orders.size() * 100;
            report.append(String.format("Order Completion Rate: %.2f%%\n", completionRate));
        }

        return report.toString();
    }

    private void viewSelectedReport() {
        int selectedRow = reportsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int reportId = (int) tableModel.getValueAt(selectedRow, 0);
                Report report = reportService.findById(reportId);
                
                JPanel panel = new JPanel(new BorderLayout(5, 5));
                panel.setBorder(new EmptyBorder(10, 10, 10, 10));
                
                JTextArea contentArea = new JTextArea(report.getContent());
                contentArea.setEditable(false);
                contentArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                
                panel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
                
                JOptionPane.showMessageDialog(this, panel, "Report Details",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error viewing report: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a report to view");
        }
    }

    private void deleteSelectedReport() {
        int selectedRow = reportsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int reportId = (int) tableModel.getValueAt(selectedRow, 0);
                
                int result = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this report?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    reportService.deleteById(reportId);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Report deleted successfully!");
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error deleting report: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a report to delete");
        }
    }

    private void exportSelectedReport() {
        int selectedRow = reportsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int reportId = (int) tableModel.getValueAt(selectedRow, 0);
                Report report = reportService.findById(reportId);
                
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Export Report");
                fileChooser.setSelectedFile(new java.io.File(report.getTitle() + ".pdf"));
                
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = fileChooser.getSelectedFile();
                    reportService.exportReport(reportId, file.getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Report exported successfully!");
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a report to export");
        }
    }

    private void searchReports() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        try {
            List<Report> reports = reportService.findAll();
            for (Report report : reports) {
                if (report.getTitle().toLowerCase().contains(searchText) ||
                    report.getType().toLowerCase().contains(searchText)) {
                    tableModel.addRow(new Object[]{
                        report.getId(),
                        report.getTitle(),
                        report.getType(),
                        report.getGeneratedDate().format(DATE_FORMATTER),
                        report.getStatus()
                    });
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error searching reports: " + e.getMessage());
        }
    }
}