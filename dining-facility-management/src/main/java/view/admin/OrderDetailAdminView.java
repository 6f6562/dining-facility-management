package view.admin;

import model.Dish;
import model.OrderDetail;
import model.OrderHeader;
import service.DishService;
import service.OrderDetailService;
import service.OrderHeaderService;
import service.impl.DishServiceImpl;
import service.impl.OrderDetailServiceImpl;
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

public class OrderDetailAdminView extends JPanel {
    private final OrderDetailService orderDetailService;
    private final DishService dishService;
    private final OrderHeaderService orderHeaderService;
    
    // UI Components
    private JTable detailTable;
    private DefaultTableModel tableModel;
    private JComboBox<OrderHeader> orderHeaderComboBox;
    private JComboBox<Dish> dishComboBox;
    private JTextField orderQtyField;
    private JTextField priceField;
    private JTextField orderDateField;
    private JTextField deliveryTimeField;
    private JTextField subTotalField;
    private JTextArea specialInstructionsArea;
    private JComboBox<String> statusComboBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton calculateButton;
    
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
    private static final String[] STATUS_OPTIONS = {"Pending", "Cooking", "Delivered", "Cancelled"};

    public OrderDetailAdminView() throws RemoteException {
        this.orderDetailService = new OrderDetailServiceImpl();
        this.dishService = new DishServiceImpl();
        this.orderHeaderService = new OrderHeaderServiceImpl();
        initComponents();
        loadDishOptions();
        loadOrderOptions();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Thông tin chi tiết đơn hàng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Order Header
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Đơn hàng:"), gbc);
        gbc.gridx = 1;
        orderHeaderComboBox = new JComboBox<>();
        orderHeaderComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof OrderHeader) {
                    setText("#OD-" + ((OrderHeader) value).getId() + " - Bàn " + 
                           ((OrderHeader) value).getDiningTable().getTableNumber());
                }
                return this;
            }
        });
        formPanel.add(orderHeaderComboBox, gbc);

        // Dish
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Món ăn:"), gbc);
        gbc.gridx = 1;
        dishComboBox = new JComboBox<>();
        dishComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Dish) {
                    setText(((Dish) value).getName());
                }
                return this;
            }
        });
        formPanel.add(dishComboBox, gbc);

        // Order Quantity
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        orderQtyField = new JTextField(10);
        formPanel.add(orderQtyField, gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Giá:"), gbc);
        gbc.gridx = 1;
        priceField = new JTextField(10);
        formPanel.add(priceField, gbc);

        // Order Date
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Ngày đặt món (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        orderDateField = new JTextField(20);
        formPanel.add(orderDateField, gbc);

        // Delivery Time
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Thời gian giao (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        deliveryTimeField = new JTextField(20);
        formPanel.add(deliveryTimeField, gbc);

        // Sub Total
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Thành tiền:"), gbc);
        gbc.gridx = 1;
        subTotalField = new JTextField(10);
        formPanel.add(subTotalField, gbc);

        // Special Instructions
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1;
        specialInstructionsArea = new JTextArea(3, 20);
        specialInstructionsArea.setLineWrap(true);
        specialInstructionsArea.setWrapStyleWord(true);
        JScrollPane instructionsScrollPane = new JScrollPane(specialInstructionsArea);
        formPanel.add(instructionsScrollPane, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(STATUS_OPTIONS);
        formPanel.add(statusComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = createStyledButton("Thêm", "Thêm chi tiết đơn hàng mới");
        updateButton = createStyledButton("Cập nhật", "Cập nhật chi tiết đơn hàng đã chọn");
        deleteButton = createStyledButton("Xóa", "Xóa chi tiết đơn hàng đã chọn");
        refreshButton = createStyledButton("Làm mới", "Làm mới danh sách chi tiết đơn hàng");
        calculateButton = createStyledButton("Tính tiền", "Tính thành tiền dựa trên số lượng và giá");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(calculateButton);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Đơn hàng", "Món ăn", "Số lượng", "Giá", "Thành tiền", "Ngày đặt", "Thời gian giao", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        detailTable = new JTable(tableModel);
        detailTable.setFillsViewportHeight(true);
        detailTable.setRowHeight(25);
        detailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        detailTable.setGridColor(Color.LIGHT_GRAY);
        detailTable.setShowGrid(true);
        detailTable.setBackground(Color.WHITE);
        detailTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = detailTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(detailTable);
        tableScrollPane.setBorder(new TitledBorder("Danh sách chi tiết đơn hàng"));
        tableScrollPane.setBackground(Color.WHITE);

        // Add components to main panel
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void loadDishOptions() {
        try {
            dishComboBox.removeAllItems();
            List<Dish> dishes = dishService.findAll();
            for (Dish dish : dishes) {
                dishComboBox.addItem(dish);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách món ăn: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrderOptions() {
        try {
            orderHeaderComboBox.removeAllItems();
            List<OrderHeader> orderHeaders = orderHeaderService.findAll();
            for (OrderHeader orderHeader : orderHeaders) {
                orderHeaderComboBox.addItem(orderHeader);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách đơn hàng: " + e.getMessage(), 
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
        detailTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = detailTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    
                    // Get the detail details
                    try {
                        OrderDetail detail = orderDetailService.findById(selectedId);
                        if (detail != null) {
                            // Set order header in combo box
                            for (int i = 0; i < orderHeaderComboBox.getItemCount(); i++) {
                                OrderHeader orderHeader = orderHeaderComboBox.getItemAt(i);
                                if (orderHeader.getId() == detail.getOrderHeader().getId()) {
                                    orderHeaderComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            
                            // Set dish in combo box
                            for (int i = 0; i < dishComboBox.getItemCount(); i++) {
                                Dish dish = dishComboBox.getItemAt(i);
                                if (dish.getId() == detail.getDish().getId()) {
                                    dishComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            
                            // Set other fields
                            orderQtyField.setText(String.valueOf(detail.getOrderQty()));
                            priceField.setText(String.valueOf(detail.getPrice()));
                            orderDateField.setText(detail.getOrderDate().format(DATE_TIME_FORMATTER));
                            
                            if (detail.getDeliveryTime() != null) {
                                deliveryTimeField.setText(detail.getDeliveryTime().format(DATE_TIME_FORMATTER));
                            } else {
                                deliveryTimeField.setText("");
                            }
                            
                            subTotalField.setText(String.valueOf(detail.getSubTotal()));
                            specialInstructionsArea.setText(detail.getSpecialInstructions());
                            
                            // Set status in combo box
                            for (int i = 0; i < STATUS_OPTIONS.length; i++) {
                                if (STATUS_OPTIONS[i].equals(detail.getStatus())) {
                                    statusComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin chi tiết đơn hàng: " + ex.getMessage(), 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addDetail());
        updateButton.addActionListener(e -> updateDetail());
        deleteButton.addActionListener(e -> deleteDetail());
        refreshButton.addActionListener(e -> loadTableData());
        calculateButton.addActionListener(e -> calculateSubTotal());
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<OrderDetail> details = orderDetailService.findAll();
            for (OrderDetail detail : details) {
                tableModel.addRow(new Object[]{
                    detail.getId(),
                    "#OD-" + detail.getOrderHeader().getId() + " - Bàn " + 
                    detail.getOrderHeader().getDiningTable().getTableNumber(),
                    detail.getDish().getName(),
                    detail.getOrderQty(),
                    String.format("%,.0f", detail.getPrice()),
                    String.format("%,.0f", detail.getSubTotal()),
                    detail.getOrderDate().format(DATE_TIME_FORMATTER),
                    detail.getDeliveryTime() != null ? detail.getDeliveryTime().format(DATE_TIME_FORMATTER) : "",
                    detail.getStatus()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách chi tiết đơn hàng: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDetail() {
        if (validateForm()) {
            try {
                OrderDetail detail = new OrderDetail();
                detail.setOrderHeader((OrderHeader) orderHeaderComboBox.getSelectedItem());
                detail.setDish((Dish) dishComboBox.getSelectedItem());
                detail.setOrderQty(Integer.parseInt(orderQtyField.getText().trim()));
                detail.setPrice(Double.parseDouble(priceField.getText().trim()));
                detail.setOrderDate(parseDateTime(orderDateField.getText().trim()));
                
                if (!deliveryTimeField.getText().trim().isEmpty()) {
                    detail.setDeliveryTime(parseDateTime(deliveryTimeField.getText().trim()));
                }
                
                detail.setSubTotal(Double.parseDouble(subTotalField.getText().trim()));
                detail.setSpecialInstructions(specialInstructionsArea.getText().trim());
                detail.setStatus((String) statusComboBox.getSelectedItem());

                orderDetailService.create(detail);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Thêm chi tiết đơn hàng thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm chi tiết đơn hàng: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateDetail() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chi tiết đơn hàng cần cập nhật", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validateForm()) {
            try {
                OrderDetail detail = orderDetailService.findById(selectedId);
                if (detail != null) {
                    detail.setOrderHeader((OrderHeader) orderHeaderComboBox.getSelectedItem());
                    detail.setDish((Dish) dishComboBox.getSelectedItem());
                    detail.setOrderQty(Integer.parseInt(orderQtyField.getText().trim()));
                    detail.setPrice(Double.parseDouble(priceField.getText().trim()));
                    detail.setOrderDate(parseDateTime(orderDateField.getText().trim()));
                    
                    if (!deliveryTimeField.getText().trim().isEmpty()) {
                        detail.setDeliveryTime(parseDateTime(deliveryTimeField.getText().trim()));
                    } else {
                        detail.setDeliveryTime(null);
                    }
                    
                    detail.setSubTotal(Double.parseDouble(subTotalField.getText().trim()));
                    detail.setSpecialInstructions(specialInstructionsArea.getText().trim());
                    detail.setStatus((String) statusComboBox.getSelectedItem());

                    orderDetailService.update(detail);
                    loadTableData();
                    resetForm();
                    JOptionPane.showMessageDialog(this, "Cập nhật chi tiết đơn hàng thành công!", 
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật chi tiết đơn hàng: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteDetail() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chi tiết đơn hàng cần xóa", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chi tiết đơn hàng này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                orderDetailService.deleteById(selectedId);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Xóa chi tiết đơn hàng thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa chi tiết đơn hàng: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void calculateSubTotal() {
        try {
            if (!orderQtyField.getText().trim().isEmpty() && !priceField.getText().trim().isEmpty()) {
                int orderQty = Integer.parseInt(orderQtyField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                double subTotal = orderQty * price;
                subTotalField.setText(String.valueOf(subTotal));
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng và giá trước khi tính thành tiền", 
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng và giá phải là số hợp lệ", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        selectedId = null;
        orderHeaderComboBox.setSelectedIndex(0);
        dishComboBox.setSelectedIndex(0);
        orderQtyField.setText("");
        priceField.setText("");
        orderDateField.setText("");
        deliveryTimeField.setText("");
        subTotalField.setText("");
        specialInstructionsArea.setText("");
        statusComboBox.setSelectedIndex(0);
        detailTable.clearSelection();
    }

    private boolean validateForm() {
        // Validate order header
        if (orderHeaderComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn hàng", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate dish
        if (dishComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate order quantity
        try {
            int orderQty = Integer.parseInt(orderQtyField.getText().trim());
            if (orderQty <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số hợp lệ", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate price
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Giá phải lớn hơn 0", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá phải là số hợp lệ", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate order date
        LocalDateTime orderDate = null;
        try {
            orderDate = parseDateTime(orderDateField.getText().trim());
            if (orderDate == null) {
                JOptionPane.showMessageDialog(this, "Ngày đặt món không được để trống", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày đặt món phải có định dạng yyyy-MM-dd HH:mm", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate delivery time if provided
        if (!deliveryTimeField.getText().trim().isEmpty()) {
            try {
                LocalDateTime deliveryTime = parseDateTime(deliveryTimeField.getText().trim());
                if (deliveryTime == null) {
                    JOptionPane.showMessageDialog(this, "Thời gian giao không hợp lệ", 
                            "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                if (deliveryTime.isBefore(orderDate)) {
                    JOptionPane.showMessageDialog(this, "Thời gian giao phải sau ngày đặt món", 
                            "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Thời gian giao phải có định dạng yyyy-MM-dd HH:mm", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // Validate sub total
        try {
            double subTotal = Double.parseDouble(subTotalField.getText().trim());
            if (subTotal <= 0) {
                JOptionPane.showMessageDialog(this, "Thành tiền phải lớn hơn 0", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Thành tiền phải là số hợp lệ", 
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