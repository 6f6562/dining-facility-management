package view.admin;

import model.Ingredient;
import model.IngredientBatch;
import model.PurchaseOrderDetail;
import service.IngredientBatchService;
import service.IngredientService;
import service.PurchaseOrderDetailService;
import service.impl.IngredientBatchServiceImpl;
import service.impl.IngredientServiceImpl;
import service.impl.PurchaseOrderDetailServiceImpl;

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
import java.time.format.DateTimeParseException;
import java.util.List;

public class IngredientBatchAdminView extends JPanel {
    private final IngredientBatchService ingredientBatchService;
    private final IngredientService ingredientService;
    private final PurchaseOrderDetailService purchaseOrderDetailService;
    
    // UI Components
    private JTable batchTable;
    private DefaultTableModel tableModel;
    private JComboBox<Ingredient> ingredientComboBox;
    private JComboBox<PurchaseOrderDetail> purchaseOrderDetailComboBox;
    private JTextField stockQuantityField;
    private JTextField receivedDateField;
    private JTextField expiryDateField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Data
    private Integer selectedId = null;
    
    // Constants
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );

    public IngredientBatchAdminView() throws RemoteException {
        this.ingredientBatchService = new IngredientBatchServiceImpl();
        this.ingredientService = new IngredientServiceImpl();
        this.purchaseOrderDetailService = new PurchaseOrderDetailServiceImpl();
        initComponents();
        loadIngredientOptions();
        loadPurchaseOrderOptions();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Thông tin lô nguyên liệu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Ingredient
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nguyên liệu:"), gbc);
        gbc.gridx = 1;
        ingredientComboBox = new JComboBox<>();
        ingredientComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Ingredient) {
                    setText(((Ingredient) value).getName());
                }
                return this;
            }
        });
        formPanel.add(ingredientComboBox, gbc);

        // Purchase Order Detail
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Đơn nhập:"), gbc);
        gbc.gridx = 1;
        purchaseOrderDetailComboBox = new JComboBox<>();
        purchaseOrderDetailComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PurchaseOrderDetail) {
                    setText("#PO-" + ((PurchaseOrderDetail) value).getPurchaseOrderHeader().getId());
                }
                return this;
            }
        });
        formPanel.add(purchaseOrderDetailComboBox, gbc);

        // Stock Quantity
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Số lượng nhập:"), gbc);
        gbc.gridx = 1;
        stockQuantityField = new JTextField(10);
        formPanel.add(stockQuantityField, gbc);

        // Received Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Ngày nhận (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        receivedDateField = new JTextField(10);
        formPanel.add(receivedDateField, gbc);

        // Expiry Date
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Hạn sử dụng (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        expiryDateField = new JTextField(10);
        formPanel.add(expiryDateField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = createStyledButton("Thêm", "Thêm lô nguyên liệu mới");
        updateButton = createStyledButton("Cập nhật", "Cập nhật lô nguyên liệu đã chọn");
        deleteButton = createStyledButton("Xóa", "Xóa lô nguyên liệu đã chọn");
        refreshButton = createStyledButton("Làm mới", "Làm mới danh sách lô nguyên liệu");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Nguyên liệu", "PO_ID", "Số lượng", "Ngày nhận", "Hạn sử dụng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        batchTable = new JTable(tableModel);
        batchTable.setFillsViewportHeight(true);
        batchTable.setRowHeight(25);
        batchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        batchTable.setGridColor(Color.LIGHT_GRAY);
        batchTable.setShowGrid(true);
        batchTable.setBackground(Color.WHITE);
        batchTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = batchTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(batchTable);
        tableScrollPane.setBorder(new TitledBorder("Danh sách lô nguyên liệu"));
        tableScrollPane.setBackground(Color.WHITE);

        // Add components to main panel
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void loadIngredientOptions() {
        try {
            ingredientComboBox.removeAllItems();
            List<Ingredient> ingredients = ingredientService.findAll();
            for (Ingredient ingredient : ingredients) {
                ingredientComboBox.addItem(ingredient);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nguyên liệu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPurchaseOrderOptions() {
        try {
            purchaseOrderDetailComboBox.removeAllItems();
            List<PurchaseOrderDetail> purchaseOrderDetails = purchaseOrderDetailService.findAll();
            for (PurchaseOrderDetail detail : purchaseOrderDetails) {
                purchaseOrderDetailComboBox.addItem(detail);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách đơn nhập: " + e.getMessage(), 
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
        batchTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = batchTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    
                    // Get the batch details
                    try {
                        IngredientBatch batch = ingredientBatchService.findById(selectedId);
                        if (batch != null) {
                            // Set ingredient in combo box
                            for (int i = 0; i < ingredientComboBox.getItemCount(); i++) {
                                Ingredient ingredient = ingredientComboBox.getItemAt(i);
                                if (ingredient.getId() == batch.getIngredient().getId()) {
                                    ingredientComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            
                            // Set purchase order detail in combo box
                            for (int i = 0; i < purchaseOrderDetailComboBox.getItemCount(); i++) {
                                PurchaseOrderDetail detail = purchaseOrderDetailComboBox.getItemAt(i);
                                if (detail.getId() == batch.getPurchaseOrderDetail().getId()) {
                                    purchaseOrderDetailComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            
                            // Set other fields
                            stockQuantityField.setText(String.valueOf(batch.getStockQuantity()));
                            receivedDateField.setText(batch.getReceivedDate().format(DATE_FORMATTER));
                            expiryDateField.setText(batch.getExpiryDate().format(DATE_FORMATTER));
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin lô nguyên liệu: " + ex.getMessage(), 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addBatch());
        updateButton.addActionListener(e -> updateBatch());
        deleteButton.addActionListener(e -> deleteBatch());
        refreshButton.addActionListener(e -> loadTableData());
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<IngredientBatch> batches = ingredientBatchService.findAll();
            for (IngredientBatch batch : batches) {
                tableModel.addRow(new Object[]{
                    batch.getId(),
                    batch.getIngredient().getName(),
                    "#PO-" + batch.getPurchaseOrderDetail().getPurchaseOrderHeader().getId(),
                    String.format("%,.1f", batch.getStockQuantity()),
                    batch.getReceivedDate().format(DATE_FORMATTER),
                    batch.getExpiryDate().format(DATE_FORMATTER)
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách lô nguyên liệu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBatch() {
        if (validateForm()) {
            try {
                IngredientBatch batch = new IngredientBatch();
                batch.setIngredient((Ingredient) ingredientComboBox.getSelectedItem());
                batch.setPurchaseOrderDetail((PurchaseOrderDetail) purchaseOrderDetailComboBox.getSelectedItem());
                batch.setStockQuantity(Double.parseDouble(stockQuantityField.getText().trim()));
                batch.setReceivedDate(parseDate(receivedDateField.getText().trim()));
                batch.setExpiryDate(parseDate(expiryDateField.getText().trim()));

                ingredientBatchService.create(batch);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Thêm lô nguyên liệu thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm lô nguyên liệu: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateBatch() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lô nguyên liệu cần cập nhật", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validateForm()) {
            try {
                IngredientBatch batch = ingredientBatchService.findById(selectedId);
                if (batch != null) {
                    batch.setIngredient((Ingredient) ingredientComboBox.getSelectedItem());
                    batch.setPurchaseOrderDetail((PurchaseOrderDetail) purchaseOrderDetailComboBox.getSelectedItem());
                    batch.setStockQuantity(Double.parseDouble(stockQuantityField.getText().trim()));
                    batch.setReceivedDate(parseDate(receivedDateField.getText().trim()));
                    batch.setExpiryDate(parseDate(expiryDateField.getText().trim()));

                    ingredientBatchService.update(batch);
                    loadTableData();
                    resetForm();
                    JOptionPane.showMessageDialog(this, "Cập nhật lô nguyên liệu thành công!", 
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật lô nguyên liệu: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteBatch() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lô nguyên liệu cần xóa", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa lô nguyên liệu này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                ingredientBatchService.deleteById(selectedId);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Xóa lô nguyên liệu thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa lô nguyên liệu: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetForm() {
        selectedId = null;
        ingredientComboBox.setSelectedIndex(0);
        purchaseOrderDetailComboBox.setSelectedIndex(0);
        stockQuantityField.setText("");
        receivedDateField.setText("");
        expiryDateField.setText("");
        batchTable.clearSelection();
    }

    private boolean validateForm() {
        // Validate ingredient
        if (ingredientComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate purchase order detail
        if (purchaseOrderDetailComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn nhập", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate stock quantity
        try {
            double stockQuantity = Double.parseDouble(stockQuantityField.getText().trim());
            if (stockQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng nhập phải lớn hơn 0", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng nhập phải là số hợp lệ", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate received date
        LocalDate receivedDate = null;
        try {
            receivedDate = parseDate(receivedDateField.getText().trim());
            if (receivedDate == null) {
                JOptionPane.showMessageDialog(this, "Ngày nhận không được để trống", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày nhận phải có định dạng dd/MM/yyyy", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate expiry date
        LocalDate expiryDate = null;
        try {
            expiryDate = parseDate(expiryDateField.getText().trim());
            if (expiryDate == null) {
                JOptionPane.showMessageDialog(this, "Hạn sử dụng không được để trống", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (expiryDate.isBefore(receivedDate)) {
                JOptionPane.showMessageDialog(this, "Hạn sử dụng phải sau ngày nhận", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Hạn sử dụng phải có định dạng dd/MM/yyyy", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
    }
} 