package view.admin;

import model.Report;
import service.ReportService;
import service.impl.ReportServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ReportAdminView extends JPanel {
    private final ReportService reportService;
    
    // UI Components
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JTextField titleField;
    private JComboBox<String> typeComboBox;
    private JTextField periodField;
    private JTextField generatedDateField;
    private JComboBox<String> statusComboBox;
    private JTextArea contentArea;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Data
    private Integer selectedId = null;
    
    // Constants
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );
    private static final String[] TYPE_OPTIONS = {"Doanh thu", "Nguyên liệu", "Hiệu suất bàn"};
    private static final String[] STATUS_OPTIONS = {"Generated", "Pending", "Approved"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReportAdminView() throws RemoteException {
        this.reportService = new ReportServiceImpl();
        initComponents();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Quản lý báo cáo thống kê"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title Field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1;
        titleField = new JTextField();
        titleField.setBorder(COMPONENT_BORDER);
        formPanel.add(titleField, gbc);

        // Type ComboBox
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Loại:"), gbc);
        gbc.gridx = 1;
        typeComboBox = new JComboBox<>(TYPE_OPTIONS);
        typeComboBox.setBorder(COMPONENT_BORDER);
        formPanel.add(typeComboBox, gbc);

        // Period Field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Kỳ báo cáo:"), gbc);
        gbc.gridx = 1;
        periodField = new JTextField();
        periodField.setBorder(COMPONENT_BORDER);
        formPanel.add(periodField, gbc);

        // Generated Date Field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày tạo:"), gbc);
        gbc.gridx = 1;
        generatedDateField = new JTextField();
        generatedDateField.setBorder(COMPONENT_BORDER);
        formPanel.add(generatedDateField, gbc);

        // Status ComboBox
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(STATUS_OPTIONS);
        statusComboBox.setBorder(COMPONENT_BORDER);
        formPanel.add(statusComboBox, gbc);

        // Content Area
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Nội dung:"), gbc);
        gbc.gridx = 1;
        contentArea = new JTextArea(5, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(COMPONENT_BORDER);
        formPanel.add(contentScrollPane, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Thêm");
        updateButton = new JButton("Cập nhật");
        deleteButton = new JButton("Xoá");
        refreshButton = new JButton("Làm mới");

        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        updateButton.setBackground(PRIMARY_COLOR);
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(PRIMARY_COLOR);
        deleteButton.setForeground(Color.WHITE);
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Table
        String[] columnNames = {"ID", "Tiêu đề", "Loại", "Kỳ", "Ngày tạo", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(tableModel);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader header = reportTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(reportTable);

        // Add components to panel
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<Report> reports = reportService.findAll();
            for (Report report : reports) {
                tableModel.addRow(new Object[]{
                    report.getId(),
                    report.getTitle(),
                    report.getType(),
                    report.getPeriod(),
                    report.getGeneratedDate().format(DATE_FORMATTER),
                    report.getStatus()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        // Table selection listener
        reportTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = reportTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    try {
                        Report report = reportService.findById(selectedId);
                        if (report != null) {
                            titleField.setText(report.getTitle());
                            typeComboBox.setSelectedItem(report.getType());
                            periodField.setText(report.getPeriod());
                            generatedDateField.setText(report.getGeneratedDate().format(DATE_FORMATTER));
                            statusComboBox.setSelectedItem(report.getStatus());
                            contentArea.setText(report.getContent());
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin báo cáo: " + ex.getMessage());
                    }
                }
            }
        });

        // Add button listener
        addButton.addActionListener(e -> {
            try {
                if (validateForm()) {
                    Report report = new Report();
                    report.setTitle(titleField.getText().trim());
                    report.setType((String) typeComboBox.getSelectedItem());
                    report.setPeriod(periodField.getText().trim());
                    
                    // Set generated date
                    String dateStr = generatedDateField.getText().trim();
                    if (dateStr.isEmpty()) {
                        report.setGeneratedDate(LocalDate.now());
                    } else {
                        try {
                            report.setGeneratedDate(LocalDate.parse(dateStr, DATE_FORMATTER));
                        } catch (DateTimeParseException ex) {
                            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Sử dụng định dạng: dd/MM/yyyy");
                            return;
                        }
                    }
                    
                    report.setStatus((String) statusComboBox.getSelectedItem());
                    report.setContent(contentArea.getText().trim());

                    reportService.create(report);
                    JOptionPane.showMessageDialog(this, "Thêm báo cáo thành công!");
                    resetForm();
                    loadTableData();
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm báo cáo: " + ex.getMessage());
            }
        });

        // Update button listener
        updateButton.addActionListener(e -> {
            try {
                if (selectedId != null && validateForm()) {
                    Report report = reportService.findById(selectedId);
                    if (report != null) {
                        report.setTitle(titleField.getText().trim());
                        report.setType((String) typeComboBox.getSelectedItem());
                        report.setPeriod(periodField.getText().trim());
                        
                        // Set generated date
                        String dateStr = generatedDateField.getText().trim();
                        if (!dateStr.isEmpty()) {
                            try {
                                report.setGeneratedDate(LocalDate.parse(dateStr, DATE_FORMATTER));
                            } catch (DateTimeParseException ex) {
                                JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Sử dụng định dạng: dd/MM/yyyy");
                                return;
                            }
                        }
                        
                        report.setStatus((String) statusComboBox.getSelectedItem());
                        report.setContent(contentArea.getText().trim());

                        reportService.update(report);
                        JOptionPane.showMessageDialog(this, "Cập nhật báo cáo thành công!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn báo cáo cần cập nhật");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật báo cáo: " + ex.getMessage());
            }
        });

        // Delete button listener
        deleteButton.addActionListener(e -> {
            try {
                if (selectedId != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa báo cáo này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        reportService.deleteById(selectedId);
                        JOptionPane.showMessageDialog(this, "Xóa báo cáo thành công!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn báo cáo cần xóa");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa báo cáo: " + ex.getMessage());
            }
        });

        // Refresh button listener
        refreshButton.addActionListener(e -> {
            resetForm();
            loadTableData();
        });
    }

    private boolean validateForm() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tiêu đề báo cáo");
            return false;
        }

        if (periodField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập kỳ báo cáo");
            return false;
        }

        if (contentArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập nội dung báo cáo");
            return false;
        }

        // Validate date format if provided
        String dateStr = generatedDateField.getText().trim();
        if (!dateStr.isEmpty()) {
            try {
                LocalDate.parse(dateStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Sử dụng định dạng: dd/MM/yyyy");
                return false;
            }
        }

        return true;
    }

    private void resetForm() {
        selectedId = null;
        titleField.setText("");
        typeComboBox.setSelectedIndex(0);
        periodField.setText("");
        generatedDateField.setText("");
        statusComboBox.setSelectedIndex(0);
        contentArea.setText("");
        reportTable.clearSelection();
    }
} 