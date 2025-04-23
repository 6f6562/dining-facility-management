package view.admin;

import model.Dish;
import model.IngredientModel;
import model.Recipe;
import service.DishService;
import service.IngredientModelService;
import service.RecipeService;
import service.impl.DishServiceImpl;
import service.impl.IngredientModelServiceImpl;
import service.impl.RecipeServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class RecipeAdminView extends JPanel {
    private final RecipeService recipeService;
    private final DishService dishService;
    private final IngredientModelService ingredientModelService;

    // UI Components
    private JTable recipeTable;
    private DefaultTableModel tableModel;
    private JComboBox<Dish> dishComboBox;
    private JComboBox<IngredientModel> ingredientModelComboBox;
    private JTextField requiredQuantityField;
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
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY), new EmptyBorder(5, 5, 5, 5));

    public RecipeAdminView() throws RemoteException {
        this.recipeService = new RecipeServiceImpl();
        this.dishService = new DishServiceImpl();
        this.ingredientModelService = new IngredientModelServiceImpl();
        initComponents();
        loadDishOptions();
        loadIngredientModelOptions();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Quản lý công thức chế biến"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Dish ComboBox
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Món ăn:"), gbc);
        gbc.gridx = 1;
        dishComboBox = new JComboBox<>();
        dishComboBox.setBorder(COMPONENT_BORDER);
        formPanel.add(dishComboBox, gbc);

        // Ingredient Model ComboBox
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Nguyên liệu:"), gbc);
        gbc.gridx = 1;
        ingredientModelComboBox = new JComboBox<>();
        ingredientModelComboBox.setBorder(COMPONENT_BORDER);
        formPanel.add(ingredientModelComboBox, gbc);

        // Required Quantity Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Lượng cần:"), gbc);
        gbc.gridx = 1;
        requiredQuantityField = new JTextField();
        requiredQuantityField.setBorder(COMPONENT_BORDER);
        formPanel.add(requiredQuantityField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Thêm");
        updateButton = new JButton("Cập nhật");
        deleteButton = new JButton("Xoá");
        refreshButton = new JButton("Làm mới");

//        addButton.setBackground(PRIMARY_COLOR);
//        addButton.setForeground(Color.WHITE);
//        updateButton.setBackground(PRIMARY_COLOR);
//        updateButton.setForeground(Color.WHITE);
//        deleteButton.setBackground(PRIMARY_COLOR);
//        deleteButton.setForeground(Color.WHITE);
//        refreshButton.setBackground(PRIMARY_COLOR);
//        refreshButton.setForeground(Color.WHITE);

        JButton[] buttons = {addButton, updateButton, deleteButton, refreshButton};
        for (JButton button : buttons) {
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            button.setBackground(new Color(30, 144, 255));  // Dodger Blue
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        }

        for (JButton button : buttons) {
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(65, 165, 255));  // Hover
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(30, 144, 255));  // Default
                }
            });
        }

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);



        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Table
        String[] columnNames = {"ID", "Món ăn", "Nguyên liệu", "Số lượng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recipeTable = new JTable(tableModel);
        recipeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader header = recipeTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(recipeTable);

        // Add components to panel
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDishOptions() {
        try {
            dishComboBox.removeAllItems();
            List<Dish> dishes = dishService.findAll();
            for (Dish dish : dishes) {
                dishComboBox.addItem(dish);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách món ăn: " + e.getMessage());
        }
    }

    private void loadIngredientModelOptions() {
        try {
            ingredientModelComboBox.removeAllItems();
            List<IngredientModel> ingredientModels = ingredientModelService.findAll();
            for (IngredientModel model : ingredientModels) {
                ingredientModelComboBox.addItem(model);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nguyên liệu: " + e.getMessage());
        }
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<Recipe> recipes = recipeService.findAll();
            for (Recipe recipe : recipes) {
                tableModel.addRow(new Object[]{recipe.getId(), recipe.getDish().getName(), recipe.getIngredientModel().getName(), recipe.getRequiredQuantity()});
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        // Table selection listener
        recipeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = recipeTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    try {
                        Recipe recipe = recipeService.findById(selectedId);
                        if (recipe != null) {
                            dishComboBox.setSelectedItem(recipe.getDish());
                            ingredientModelComboBox.setSelectedItem(recipe.getIngredientModel());
                            requiredQuantityField.setText(String.valueOf(recipe.getRequiredQuantity()));
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin công thức: " + ex.getMessage());
                    }
                }
            }
        });

        // Add button listener
        addButton.addActionListener(e -> {
            try {
                if (validateForm()) {
                    Recipe recipe = new Recipe();
                    recipe.setDish((Dish) dishComboBox.getSelectedItem());
                    recipe.setIngredientModel((IngredientModel) ingredientModelComboBox.getSelectedItem());
                    recipe.setRequiredQuantity(Double.parseDouble(requiredQuantityField.getText()));

                    recipeService.create(recipe);
                    JOptionPane.showMessageDialog(this, "Thêm công thức thành công!");
                    resetForm();
                    loadTableData();
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm công thức: " + ex.getMessage());
            }
        });

        // Update button listener
        updateButton.addActionListener(e -> {
            try {
                if (selectedId != null && validateForm()) {
                    Recipe recipe = recipeService.findById(selectedId);
                    if (recipe != null) {
                        recipe.setDish((Dish) dishComboBox.getSelectedItem());
                        recipe.setIngredientModel((IngredientModel) ingredientModelComboBox.getSelectedItem());
                        recipe.setRequiredQuantity(Double.parseDouble(requiredQuantityField.getText()));

                        recipeService.update(recipe);
                        JOptionPane.showMessageDialog(this, "Cập nhật công thức thành công!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn công thức cần cập nhật");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật công thức: " + ex.getMessage());
            }
        });

        // Delete button listener
        deleteButton.addActionListener(e -> {
            try {
                if (selectedId != null) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa công thức này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        recipeService.deleteById(selectedId);
                        JOptionPane.showMessageDialog(this, "Xóa công thức thành công!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn công thức cần xóa");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa công thức: " + ex.getMessage());
            }
        });

        // Refresh button listener
        refreshButton.addActionListener(e -> {
            resetForm();
            loadTableData();
        });
    }

    private boolean validateForm() {
        if (dishComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn");
            return false;
        }

        if (ingredientModelComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nguyên liệu");
            return false;
        }

        try {
            double quantity = Double.parseDouble(requiredQuantityField.getText());
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho số lượng");
            return false;
        }

        return true;
    }

    private void resetForm() {
        selectedId = null;
        dishComboBox.setSelectedIndex(0);
        ingredientModelComboBox.setSelectedIndex(0);
        requiredQuantityField.setText("");
        recipeTable.clearSelection();
    }
} 