package view.admin;

import model.PurchaseOrderDetail;
import model.PurchaseOrderHeader;
import service.PurchaseOrderDetailService;
import service.PurchaseOrderHeaderService;
import service.impl.PurchaseOrderDetailServiceImpl;
import service.impl.PurchaseOrderHeaderServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class PurchaseOrderDetailAdminView extends JPanel {
    private final PurchaseOrderDetailService purchaseOrderDetailService;
    private final PurchaseOrderHeaderService purchaseOrderHeaderService;
    
    // UI Components
    private JTable detailTable;
    private DefaultTableModel tableModel;
    private JComboBox<PurchaseOrderHeader> purchaseOrderComboBox;
    private JTextField orderQtyField;
    private JTextField unitPriceField;
    private JTextField lineTotalField;
    private JTextField receivedQtyField;
    private JTextField rejectedQtyField;
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

    public PurchaseOrderDetailAdminView() throws RemoteException {
        this.purchaseOrderDetailService = new PurchaseOrderDetailServiceImpl();
        this.purchaseOrderHeaderService = new PurchaseOrderHeaderServiceImpl();
        initComponents();
        loadPurchaseOrderHeaderOptions();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết đơn nhập nguyên liệu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Purchase Order ComboBox
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Đơn nhập:"), gbc);
        gbc.gridx = 1;
        purchaseOrderComboBox = new JComboBox<>();
        purchaseOrderComboBox.setBorder(COMPONENT_BORDER);
        formPanel.add(purchaseOrderComboBox, gbc);

        // Order Quantity Field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Số lượng đặt:"), gbc);
        gbc.gridx = 1;
        orderQtyField = new JTextField();
        orderQtyField.setBorder(COMPONENT_BORDER);
        formPanel.add(orderQtyField, gbc);

        // Unit Price Field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Đơn giá:"), gbc);
        gbc.gridx = 1;
        unitPriceField = new JTextField();
        unitPriceField.setBorder(COMPONENT_BORDER);
        formPanel.add(unitPriceField, gbc);

        // Line Total Field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tổng tiền:"), gbc);
        gbc.gridx = 1;
        lineTotalField = new JTextField();
        lineTotalField.setBorder(COMPONENT_BORDER);
        formPanel.add(lineTotalField, gbc);

        // Received Quantity Field
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Số nhận thực tế:"), gbc);
        gbc.gridx = 1;
        receivedQtyField = new JTextField();
        receivedQtyField.setBorder(COMPONENT_BORDER);
        formPanel.add(receivedQtyField, gbc);

        // Rejected Quantity Field
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Bị từ chối:"), gbc);
        gbc.gridx = 1;
        rejectedQtyField = new JTextField();
        rejectedQtyField.setBorder(COMPONENT_BORDER);
        formPanel.add(rejectedQtyField, gbc);

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
        String[] columnNames = {"ID", "Đơn nhập", "Nhà cung cấp", "Số lượng đặt", "Đơn giá", "Tổng tiền", "Số nhận", "Bị từ chối"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        detailTable = new JTable(tableModel);
        detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader header = detailTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(detailTable);

        // Add components to panel
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPurchaseOrderHeaderOptions() {
        try {
            purchaseOrderComboBox.removeAllItems();
            List<PurchaseOrderHeader> orders = purchaseOrderHeaderService.findAll();
            for (PurchaseOrderHeader order : orders) {
                purchaseOrderComboBox.addItem(order);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách đơn nhập: " + e.getMessage());
        }
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<PurchaseOrderDetail> details = purchaseOrderDetailService.findAll();
            for (PurchaseOrderDetail detail : details) {
                PurchaseOrderHeader order = detail.getPurchaseOrderHeader();
                tableModel.addRow(new Object[]{
                    detail.getId(),
                    "#PO-" + order.getId(),
                    order.getVendor().getName(),
                    detail.getOrderQty(),
                    detail.getUnitPrice(),
                    detail.getLineTotal(),
                    detail.getReceivedQty(),
                    detail.getRejectedQty()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        // Calculate line total when order quantity or unit price changes
        orderQtyField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateLineTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateLineTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateLineTotal(); }
        });

        unitPriceField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calculateLineTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calculateLineTotal(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calculateLineTotal(); }
        });

        // Table selection listener
        detailTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = detailTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    try {
                        PurchaseOrderDetail detail = purchaseOrderDetailService.findById(selectedId);
                        if (detail != null) {
                            purchaseOrderComboBox.setSelectedItem(detail.getPurchaseOrderHeader());
                            orderQtyField.setText(String.valueOf(detail.getOrderQty()));
                            unitPriceField.setText(String.valueOf(detail.getUnitPrice()));
                            lineTotalField.setText(String.valueOf(detail.getLineTotal()));
                            receivedQtyField.setText(String.valueOf(detail.getReceivedQty()));
                            rejectedQtyField.setText(String.valueOf(detail.getRejectedQty()));
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải chi tiết: " + ex.getMessage());
                    }
                }
            }
        });

        // Add button listener
        addButton.addActionListener(e -> {
            try {
                if (validateForm()) {
                    PurchaseOrderDetail detail = new PurchaseOrderDetail();
                    detail.setPurchaseOrderHeader((PurchaseOrderHeader) purchaseOrderComboBox.getSelectedItem());
                    detail.setOrderQty(Double.parseDouble(orderQtyField.getText()));
                    detail.setUnitPrice(Double.parseDouble(unitPriceField.getText()));
                    detail.setLineTotal(Double.parseDouble(lineTotalField.getText()));
                    detail.setReceivedQty(Double.parseDouble(receivedQtyField.getText()));
                    detail.setRejectedQty(Double.parseDouble(rejectedQtyField.getText()));

                    purchaseOrderDetailService.create(detail);
                    JOptionPane.showMessageDialog(this, "Thêm chi tiết đơn nhập thành công!");
                    resetForm();
                    loadTableData();
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm chi tiết: " + ex.getMessage());
            }
        });

        // Update button listener
        updateButton.addActionListener(e -> {
            try {
                if (selectedId != null && validateForm()) {
                    PurchaseOrderDetail detail = purchaseOrderDetailService.findById(selectedId);
                    if (detail != null) {
                        detail.setPurchaseOrderHeader((PurchaseOrderHeader) purchaseOrderComboBox.getSelectedItem());
                        detail.setOrderQty(Double.parseDouble(orderQtyField.getText()));
                        detail.setUnitPrice(Double.parseDouble(unitPriceField.getText()));
                        detail.setLineTotal(Double.parseDouble(lineTotalField.getText()));
                        detail.setReceivedQty(Double.parseDouble(receivedQtyField.getText()));
                        detail.setRejectedQty(Double.parseDouble(rejectedQtyField.getText()));

                        purchaseOrderDetailService.update(detail);
                        JOptionPane.showMessageDialog(this, "Cập nhật chi tiết đơn nhập thành công!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn chi tiết đơn nhập cần cập nhật");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật chi tiết: " + ex.getMessage());
            }
        });

        // Delete button listener
        deleteButton.addActionListener(e -> {
            try {
                if (selectedId != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa chi tiết đơn nhập này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        purchaseOrderDetailService.deleteById(selectedId);
                        JOptionPane.showMessageDialog(this, "Xóa chi tiết đơn nhập thành công!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn chi tiết đơn nhập cần xóa");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa chi tiết: " + ex.getMessage());
            }
        });

        // Refresh button listener
        refreshButton.addActionListener(e -> {
            resetForm();
            loadTableData();
        });
    }

    private void calculateLineTotal() {
        try {
            double orderQty = Double.parseDouble(orderQtyField.getText());
            double unitPrice = Double.parseDouble(unitPriceField.getText());
            double lineTotal = orderQty * unitPrice;
            lineTotalField.setText(String.valueOf(lineTotal));
        } catch (NumberFormatException e) {
            // Ignore if fields are empty or contain invalid numbers
        }
    }

    private boolean validateForm() {
        if (purchaseOrderComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn nhập");
            return false;
        }

        try {
            double orderQty = Double.parseDouble(orderQtyField.getText());
            if (orderQty <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng đặt phải lớn hơn 0");
                return false;
            }

            double unitPrice = Double.parseDouble(unitPriceField.getText());
            if (unitPrice <= 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0");
                return false;
            }

            double receivedQty = Double.parseDouble(receivedQtyField.getText());
            double rejectedQty = Double.parseDouble(rejectedQtyField.getText());
            
            // Kiểm tra tổng số lượng nhận và bị từ chối có gần bằng số lượng đặt
            double total = receivedQty + rejectedQty;
            if (Math.abs(total - orderQty) > 0.01) { // Cho phép sai số 0.01
                JOptionPane.showMessageDialog(this, 
                    "Tổng số lượng nhận và bị từ chối phải bằng số lượng đặt");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho các trường số lượng");
            return false;
        }

        return true;
    }

    private void resetForm() {
        selectedId = null;
        purchaseOrderComboBox.setSelectedIndex(0);
        orderQtyField.setText("");
        unitPriceField.setText("");
        lineTotalField.setText("");
        receivedQtyField.setText("");
        rejectedQtyField.setText("");
        detailTable.clearSelection();
    }
} 