package view.admin;

import model.PurchaseOrderHeader;
import model.Vendor;
import service.PurchaseOrderHeaderService;
import service.VendorService;
import service.impl.PurchaseOrderHeaderServiceImpl;
import service.impl.VendorServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PurchaseOrderHeaderAdminView extends JPanel {
    private final PurchaseOrderHeaderService purchaseOrderHeaderService;
    private final VendorService vendorService;
    
    // UI Components
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JTextField orderDateField;
    private JTextField shipDateField;
    private JTextField subTotalField;
    private JComboBox<String> statusComboBox;
    private JComboBox<Vendor> vendorComboBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Data
    private Integer selectedId = null;
    
    // Constants
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );
    private static final String[] STATUS_OPTIONS = {"Pending", "Approved", "Received", "Cancelled"};

    public PurchaseOrderHeaderAdminView() throws RemoteException {
        this.purchaseOrderHeaderService = new PurchaseOrderHeaderServiceImpl();
        this.vendorService = new VendorServiceImpl();
        initComponents();
        loadVendorOptions();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Quản lý đơn đặt nguyên liệu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Order Date Field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Ngày đặt hàng:"), gbc);
        gbc.gridx = 1;
        orderDateField = new JTextField();
        orderDateField.setBorder(COMPONENT_BORDER);
        formPanel.add(orderDateField, gbc);

        // Ship Date Field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Ngày giao hàng:"), gbc);
        gbc.gridx = 1;
        shipDateField = new JTextField();
        shipDateField.setBorder(COMPONENT_BORDER);
        formPanel.add(shipDateField, gbc);

        // Status ComboBox
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(STATUS_OPTIONS);
        statusComboBox.setBorder(COMPONENT_BORDER);
        formPanel.add(statusComboBox, gbc);

        // Sub Total Field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tổng tiền:"), gbc);
        gbc.gridx = 1;
        subTotalField = new JTextField();
        subTotalField.setBorder(COMPONENT_BORDER);
        formPanel.add(subTotalField, gbc);

        // Vendor ComboBox
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Nhà cung cấp:"), gbc);
        gbc.gridx = 1;
        vendorComboBox = new JComboBox<>();
        vendorComboBox.setBorder(COMPONENT_BORDER);
        formPanel.add(vendorComboBox, gbc);

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

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Table
        String[] columnNames = {"ID", "Ngày đặt", "Ngày giao", "Trạng thái", "Tổng tiền", "Nhà cung cấp"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader header = orderTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(orderTable);

        // Add components to panel
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadVendorOptions() {
        try {
            vendorComboBox.removeAllItems();
            List<Vendor> vendors = vendorService.findAll();
            for (Vendor vendor : vendors) {
                vendorComboBox.addItem(vendor);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhà cung cấp: " + e.getMessage());
        }
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<PurchaseOrderHeader> orders = purchaseOrderHeaderService.findAll();
            for (PurchaseOrderHeader order : orders) {
                tableModel.addRow(new Object[]{
                    order.getId(),
                    order.getOrderDate().format(DATE_TIME_FORMATTER),
                    order.getShipDate() != null ? order.getShipDate().format(DATE_TIME_FORMATTER) : "",
                    order.getStatus(),
                    order.getSubTotal(),
                    order.getVendor().getName()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        // Table selection listener
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    try {
                        PurchaseOrderHeader order = purchaseOrderHeaderService.findById(selectedId);
                        if (order != null) {
                            orderDateField.setText(order.getOrderDate().format(DATE_TIME_FORMATTER));
                            shipDateField.setText(order.getShipDate() != null ? 
                                order.getShipDate().format(DATE_TIME_FORMATTER) : "");
                            statusComboBox.setSelectedItem(order.getStatus());
                            subTotalField.setText(String.valueOf(order.getSubTotal()));
                            vendorComboBox.setSelectedItem(order.getVendor());
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin đơn hàng: " + ex.getMessage());
                    }
                }
            }
        });

        // Add button listener
        addButton.addActionListener(e -> {
            try {
                if (validateForm()) {
                    PurchaseOrderHeader order = new PurchaseOrderHeader();
                    
                    // Set order date (use current time if not specified)
                    String orderDateStr = orderDateField.getText().trim();
                    if (orderDateStr.isEmpty()) {
                        order.setOrderDate(LocalDateTime.now());
                    } else {
                        try {
                            order.setOrderDate(LocalDateTime.parse(orderDateStr, DATE_TIME_FORMATTER));
                        } catch (DateTimeParseException ex) {
                            JOptionPane.showMessageDialog(this, "Định dạng ngày đặt hàng không hợp lệ. Sử dụng định dạng: yyyy-MM-dd HH:mm");
                            return;
                        }
                    }
                    
                    // Set ship date if specified
                    String shipDateStr = shipDateField.getText().trim();
                    if (!shipDateStr.isEmpty()) {
                        try {
                            order.setShipDate(LocalDateTime.parse(shipDateStr, DATE_TIME_FORMATTER));
                        } catch (DateTimeParseException ex) {
                            JOptionPane.showMessageDialog(this, "Định dạng ngày giao hàng không hợp lệ. Sử dụng định dạng: yyyy-MM-dd HH:mm");
                            return;
                        }
                    }
                    
                    order.setStatus((String) statusComboBox.getSelectedItem());
                    order.setSubTotal(Double.parseDouble(subTotalField.getText()));
                    order.setVendor((Vendor) vendorComboBox.getSelectedItem());

                    purchaseOrderHeaderService.create(order);
                    JOptionPane.showMessageDialog(this, "Thêm đơn đặt hàng thành công!");
                    resetForm();
                    loadTableData();
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm đơn đặt hàng: " + ex.getMessage());
            }
        });

        // Update button listener
        updateButton.addActionListener(e -> {
            try {
                if (selectedId != null && validateForm()) {
                    PurchaseOrderHeader order = purchaseOrderHeaderService.findById(selectedId);
                    if (order != null) {
                        // Set order date
                        String orderDateStr = orderDateField.getText().trim();
                        if (!orderDateStr.isEmpty()) {
                            try {
                                order.setOrderDate(LocalDateTime.parse(orderDateStr, DATE_TIME_FORMATTER));
                            } catch (DateTimeParseException ex) {
                                JOptionPane.showMessageDialog(this, "Định dạng ngày đặt hàng không hợp lệ. Sử dụng định dạng: yyyy-MM-dd HH:mm");
                                return;
                            }
                        }
                        
                        // Set ship date
                        String shipDateStr = shipDateField.getText().trim();
                        if (!shipDateStr.isEmpty()) {
                            try {
                                order.setShipDate(LocalDateTime.parse(shipDateStr, DATE_TIME_FORMATTER));
                            } catch (DateTimeParseException ex) {
                                JOptionPane.showMessageDialog(this, "Định dạng ngày giao hàng không hợp lệ. Sử dụng định dạng: yyyy-MM-dd HH:mm");
                                return;
                            }
                        } else {
                            order.setShipDate(null);
                        }
                        
                        order.setStatus((String) statusComboBox.getSelectedItem());
                        order.setSubTotal(Double.parseDouble(subTotalField.getText()));
                        order.setVendor((Vendor) vendorComboBox.getSelectedItem());

                        purchaseOrderHeaderService.update(order);
                        JOptionPane.showMessageDialog(this, "Cập nhật đơn đặt hàng thành công!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn đặt hàng cần cập nhật");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật đơn đặt hàng: " + ex.getMessage());
            }
        });

        // Delete button listener
        deleteButton.addActionListener(e -> {
            try {
                if (selectedId != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa đơn đặt hàng này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        purchaseOrderHeaderService.deleteById(selectedId);
                        JOptionPane.showMessageDialog(this, "Xóa đơn đặt hàng thành công!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn đặt hàng cần xóa");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa đơn đặt hàng: " + ex.getMessage());
            }
        });

        // Refresh button listener
        refreshButton.addActionListener(e -> {
            resetForm();
            loadTableData();
        });
    }

    private boolean validateForm() {
        if (vendorComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp");
            return false;
        }

        try {
            double subTotal = Double.parseDouble(subTotalField.getText());
            if (subTotal < 0) {
                JOptionPane.showMessageDialog(this, "Tổng tiền không được âm");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho tổng tiền");
            return false;
        }

        return true;
    }

    private void resetForm() {
        selectedId = null;
        orderDateField.setText("");
        shipDateField.setText("");
        statusComboBox.setSelectedIndex(0);
        subTotalField.setText("");
        vendorComboBox.setSelectedIndex(0);
        orderTable.clearSelection();
    }
} 