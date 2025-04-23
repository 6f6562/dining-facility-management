package view.admin;

import model.Ingredient;
import model.IngredientModel;
import service.IngredientModelService;
import service.IngredientService;
import service.impl.IngredientModelServiceImpl;
import service.impl.IngredientServiceImpl;

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

public class IngredientAdminView extends JPanel {
    private final IngredientService ingredientService;
    private final IngredientModelService ingredientModelService;
    
    // UI Components
    private JTable ingredientTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JComboBox<IngredientModel> ingredientModelComboBox;
    private JTextField unitOfMeasureField;
    private JTextField stockQuantityField;
    private JTextField expiryDateField;
    private JTextField safetyStockLevelField;
    private JTextField reorderPointField;
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

    public IngredientAdminView() throws RemoteException {
        this.ingredientService = new IngredientServiceImpl();
        this.ingredientModelService = new IngredientModelServiceImpl();
        initComponents();
        loadIngredientModelOptions();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Thông tin nguyên liệu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên nguyên liệu:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Ingredient Model
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Loại nguyên liệu:"), gbc);
        gbc.gridx = 1;
        ingredientModelComboBox = new JComboBox<>();
        ingredientModelComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof IngredientModel) {
                    setText(((IngredientModel) value).getName());
                }
                return this;
            }
        });
        formPanel.add(ingredientModelComboBox, gbc);

        // Unit of Measure
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Đơn vị đo:"), gbc);
        gbc.gridx = 1;
        unitOfMeasureField = new JTextField(10);
        formPanel.add(unitOfMeasureField, gbc);

        // Stock Quantity
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Số lượng tồn kho:"), gbc);
        gbc.gridx = 1;
        stockQuantityField = new JTextField(10);
        formPanel.add(stockQuantityField, gbc);

        // Expiry Date
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Ngày hết hạn (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        expiryDateField = new JTextField(10);
        formPanel.add(expiryDateField, gbc);

        // Safety Stock Level
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Mức tồn kho an toàn:"), gbc);
        gbc.gridx = 1;
        safetyStockLevelField = new JTextField(10);
        formPanel.add(safetyStockLevelField, gbc);

        // Reorder Point
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Điểm đặt hàng lại:"), gbc);
        gbc.gridx = 1;
        reorderPointField = new JTextField(10);
        formPanel.add(reorderPointField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = createStyledButton("Thêm", "Thêm nguyên liệu mới");
        updateButton = createStyledButton("Cập nhật", "Cập nhật nguyên liệu đã chọn");
        deleteButton = createStyledButton("Xóa", "Xóa nguyên liệu đã chọn");
        refreshButton = createStyledButton("Làm mới", "Làm mới danh sách nguyên liệu");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Tên", "Loại", "Đơn vị", "Tồn kho", "Hạn sử dụng", "Tồn kho an toàn", "Điểm đặt lại"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ingredientTable = new JTable(tableModel);
        ingredientTable.setFillsViewportHeight(true);
        ingredientTable.setRowHeight(25);
        ingredientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ingredientTable.setGridColor(Color.LIGHT_GRAY);
        ingredientTable.setShowGrid(true);
        ingredientTable.setBackground(Color.WHITE);
        ingredientTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = ingredientTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(ingredientTable);
        tableScrollPane.setBorder(new TitledBorder("Danh sách nguyên liệu"));
        tableScrollPane.setBackground(Color.WHITE);

        // Add components to main panel
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void loadIngredientModelOptions() {
        try {
            ingredientModelComboBox.removeAllItems();
            List<IngredientModel> models = ingredientModelService.findAll();
            for (IngredientModel model : models) {
                ingredientModelComboBox.addItem(model);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách loại nguyên liệu: " + e.getMessage(), 
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
        ingredientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ingredientTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    
                    // Get the ingredient details
                    try {
                        Ingredient ingredient = ingredientService.findById(selectedId);
                        if (ingredient != null) {
                            // Set ingredient model in combo box
                            for (int i = 0; i < ingredientModelComboBox.getItemCount(); i++) {
                                IngredientModel model = ingredientModelComboBox.getItemAt(i);
                                if (model.getId() == ingredient.getIngredientModel().getId()) {
                                    ingredientModelComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            
                            // Set other fields
                            nameField.setText(ingredient.getName());
                            unitOfMeasureField.setText(ingredient.getUnitOfMeasure());
                            stockQuantityField.setText(String.valueOf(ingredient.getStockQuantity()));
                            expiryDateField.setText(ingredient.getExpiryDate().format(DATE_FORMATTER));
                            safetyStockLevelField.setText(String.valueOf(ingredient.getSafetyStockLevel()));
                            reorderPointField.setText(String.valueOf(ingredient.getReorderPoint()));
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin nguyên liệu: " + ex.getMessage(), 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addIngredient());
        updateButton.addActionListener(e -> updateIngredient());
        deleteButton.addActionListener(e -> deleteIngredient());
        refreshButton.addActionListener(e -> loadTableData());
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<Ingredient> ingredients = ingredientService.findAll();
            for (Ingredient ingredient : ingredients) {
                tableModel.addRow(new Object[]{
                    ingredient.getId(),
                    ingredient.getName(),
                    ingredient.getIngredientModel().getName(),
                    ingredient.getUnitOfMeasure(),
                    String.format("%,.1f", ingredient.getStockQuantity()),
                    ingredient.getExpiryDate().format(DATE_FORMATTER),
                    String.format("%,.1f", ingredient.getSafetyStockLevel()),
                    String.format("%,.1f", ingredient.getReorderPoint())
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nguyên liệu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addIngredient() {
        if (validateForm()) {
            try {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(nameField.getText().trim());
                ingredient.setIngredientModel((IngredientModel) ingredientModelComboBox.getSelectedItem());
                ingredient.setUnitOfMeasure(unitOfMeasureField.getText().trim());
                ingredient.setStockQuantity(Double.parseDouble(stockQuantityField.getText().trim()));
                ingredient.setExpiryDate(parseDate(expiryDateField.getText().trim()));
                ingredient.setSafetyStockLevel(Double.parseDouble(safetyStockLevelField.getText().trim()));
                ingredient.setReorderPoint(Double.parseDouble(reorderPointField.getText().trim()));

                ingredientService.create(ingredient);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Thêm nguyên liệu thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm nguyên liệu: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateIngredient() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu cần cập nhật", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validateForm()) {
            try {
                Ingredient ingredient = ingredientService.findById(selectedId);
                if (ingredient != null) {
                    ingredient.setName(nameField.getText().trim());
                    ingredient.setIngredientModel((IngredientModel) ingredientModelComboBox.getSelectedItem());
                    ingredient.setUnitOfMeasure(unitOfMeasureField.getText().trim());
                    ingredient.setStockQuantity(Double.parseDouble(stockQuantityField.getText().trim()));
                    ingredient.setExpiryDate(parseDate(expiryDateField.getText().trim()));
                    ingredient.setSafetyStockLevel(Double.parseDouble(safetyStockLevelField.getText().trim()));
                    ingredient.setReorderPoint(Double.parseDouble(reorderPointField.getText().trim()));

                    ingredientService.update(ingredient);
                    loadTableData();
                    resetForm();
                    JOptionPane.showMessageDialog(this, "Cập nhật nguyên liệu thành công!", 
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật nguyên liệu: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteIngredient() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu cần xóa", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nguyên liệu này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                ingredientService.deleteById(selectedId);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Xóa nguyên liệu thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa nguyên liệu: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetForm() {
        selectedId = null;
        nameField.setText("");
        ingredientModelComboBox.setSelectedIndex(0);
        unitOfMeasureField.setText("");
        stockQuantityField.setText("");
        expiryDateField.setText("");
        safetyStockLevelField.setText("");
        reorderPointField.setText("");
        ingredientTable.clearSelection();
    }

    private boolean validateForm() {
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nguyên liệu không được để trống", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate ingredient model
        if (ingredientModelComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại nguyên liệu", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate unit of measure
        if (unitOfMeasureField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đơn vị đo không được để trống", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate stock quantity
        try {
            double stockQuantity = Double.parseDouble(stockQuantityField.getText().trim());
            if (stockQuantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng tồn kho phải lớn hơn 0", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng tồn kho phải là số hợp lệ", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate expiry date
        try {
            LocalDate expiryDate = parseDate(expiryDateField.getText().trim());
            if (expiryDate == null) {
                JOptionPane.showMessageDialog(this, "Ngày hết hạn không được để trống", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (expiryDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Ngày hết hạn phải sau ngày hiện tại", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày hết hạn phải có định dạng dd/MM/yyyy", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate safety stock level
        try {
            double safetyStockLevel = Double.parseDouble(safetyStockLevelField.getText().trim());
            if (safetyStockLevel <= 0) {
                JOptionPane.showMessageDialog(this, "Mức tồn kho an toàn phải lớn hơn 0", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mức tồn kho an toàn phải là số hợp lệ", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate reorder point
        try {
            double reorderPoint = Double.parseDouble(reorderPointField.getText().trim());
            if (reorderPoint <= 0) {
                JOptionPane.showMessageDialog(this, "Điểm đặt hàng lại phải lớn hơn 0", 
                        "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm đặt hàng lại phải là số hợp lệ", 
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