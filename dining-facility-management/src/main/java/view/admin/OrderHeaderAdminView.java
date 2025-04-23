package view.admin;

import model.DiningTable;
import model.OrderHeader;
import service.DiningTableService;
import service.OrderHeaderService;
import service.impl.DiningTableServiceImpl;
import service.impl.OrderHeaderServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class OrderHeaderAdminView extends JPanel {
    private final OrderHeaderService orderHeaderService;
    private final DiningTableService diningTableService;
    
    // UI Components
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JComboBox<DiningTable> diningTableComboBox;
    private JTextField orderDateField;
    private JComboBox<String> statusComboBox;
    private JTextField subTotalField;
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
    private static final String[] STATUS_OPTIONS = {"Pending", "Processing", "Completed", "Cancelled"};

    public OrderHeaderAdminView() throws RemoteException {
        this.orderHeaderService = new OrderHeaderServiceImpl();
        this.diningTableService = new DiningTableServiceImpl();
        initComponents();
        loadDiningTableOptions();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Thông tin đơn hàng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Dining Table
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Bàn ăn:"), gbc);
        gbc.gridx = 1;
        diningTableComboBox = new JComboBox<>();
        diningTableComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DiningTable) {
                    setText("Bàn " + ((DiningTable) value).getTableNumber());
                }
                return this;
            }
        });
        formPanel.add(diningTableComboBox, gbc);

        // Order Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Ngày đặt (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        orderDateField = new JTextField(20);
        formPanel.add(orderDateField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(STATUS_OPTIONS);
        formPanel.add(statusComboBox, gbc);

        // Sub Total
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Tổng tiền:"), gbc);
        gbc.gridx = 1;
        subTotalField = new JTextField(10);
        formPanel.add(subTotalField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = createStyledButton("Thêm", "Thêm đơn hàng mới");
        updateButton = createStyledButton("Cập nhật", "Cập nhật đơn hàng đã chọn");
        deleteButton = createStyledButton("Xóa", "Xóa đơn hàng đã chọn");
        refreshButton = createStyledButton("Làm mới", "Làm mới danh sách đơn hàng");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Bàn", "Ngày đặt", "Trạng thái", "Tổng tiền"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setFillsViewportHeight(true);
        orderTable.setRowHeight(25);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setGridColor(Color.LIGHT_GRAY);
        orderTable.setShowGrid(true);
        orderTable.setBackground(Color.WHITE);
        orderTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = orderTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(orderTable);
        tableScrollPane.setBorder(new TitledBorder("Danh sách đơn hàng"));
        tableScrollPane.setBackground(Color.WHITE);

        // Add components to main panel
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void loadDiningTableOptions() {
        try {
            diningTableComboBox.removeAllItems();
            List<DiningTable> tables = diningTableService.findAll();
            for (DiningTable table : tables) {
                diningTableComboBox.addItem(table);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách bàn ăn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
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
        // Table selection listener
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    
                    // Get the order details
                    try {
                        OrderHeader order = orderHeaderService.findById(selectedId);
                        if (order != null) {
                            // Set dining table in combo box
                            for (int i = 0; i < diningTableComboBox.getItemCount(); i++) {
                                DiningTable table = diningTableComboBox.getItemAt(i);
                                if (table.getId() == order.getDiningTable().getId()) {
                                    diningTableComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            
                            // Set other fields
                            orderDateField.setText(order.getOrderDate().format(DATE_TIME_FORMATTER));
                            
                            // Set status in combo box
                            for (int i = 0; i < STATUS_OPTIONS.length; i++) {
                                if (STATUS_OPTIONS[i].equals(order.getStatus())) {
                                    statusComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            
                            subTotalField.setText(String.valueOf(order.getSubTotal()));
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin đơn hàng: " + ex.getMessage(), 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addOrder());
        updateButton.addActionListener(e -> updateOrder());
        deleteButton.addActionListener(e -> deleteOrder());
        refreshButton.addActionListener(e -> loadTableData());
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<OrderHeader> orders = orderHeaderService.findAll();
            for (OrderHeader order : orders) {
                tableModel.addRow(new Object[]{
                    order.getId(),
                    "Bàn " + order.getDiningTable().getTableNumber(),
                    order.getOrderDate().format(DATE_TIME_FORMATTER),
                    order.getStatus(),
                    String.format("%,.0f VNĐ", order.getSubTotal())
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách đơn hàng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addOrder() {
        if (validateForm()) {
            try {
                OrderHeader order = new OrderHeader();
                order.setDiningTable((DiningTable) diningTableComboBox.getSelectedItem());
                
                // Set order date - if empty, use current time
                if (orderDateField.getText().trim().isEmpty()) {
                    order.setOrderDate(LocalDateTime.now());
                } else {
                    order.setOrderDate(parseDateTime(orderDateField.getText().trim()));
                }
                
                order.setStatus((String) statusComboBox.getSelectedItem());
                order.setSubTotal(Double.parseDouble(subTotalField.getText().trim()));

                orderHeaderService.create(order);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Thêm đơn hàng thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm đơn hàng: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateOrder() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần cập nhật", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validateForm()) {
            try {
                OrderHeader order = orderHeaderService.findById(selectedId);
                if (order != null) {
                    order.setDiningTable((DiningTable) diningTableComboBox.getSelectedItem());
                    order.setOrderDate(parseDateTime(orderDateField.getText().trim()));
                    order.setStatus((String) statusComboBox.getSelectedItem());
                    order.setSubTotal(Double.parseDouble(subTotalField.getText().trim()));

                    orderHeaderService.update(order);
                    loadTableData();
                    resetForm();
                    JOptionPane.showMessageDialog(this, "Cập nhật đơn hàng thành công!", 
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật đơn hàng: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteOrder() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng cần xóa", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa đơn hàng này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                orderHeaderService.deleteById(selectedId);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Xóa đơn hàng thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa đơn hàng: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetForm() {
        selectedId = null;
        diningTableComboBox.setSelectedIndex(0);
        orderDateField.setText("");
        statusComboBox.setSelectedIndex(0);
        subTotalField.setText("");
        orderTable.clearSelection();
    }

    private boolean validateForm() {
        // Validate dining table
        if (diningTableComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn ăn", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate order date if provided
        if (!orderDateField.getText().trim().isEmpty()) {
            try {
                parseDateTime(orderDateField.getText().trim());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Ngày đặt phải có định dạng yyyy-MM-dd HH:mm", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // Validate sub total
        try {
            double subTotal = Double.parseDouble(subTotalField.getText().trim());
            if (subTotal < 0) {
                JOptionPane.showMessageDialog(this, "Tổng tiền không được âm", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tổng tiền phải là số hợp lệ", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr.trim(), DATE_TIME_FORMATTER);
    }
} 