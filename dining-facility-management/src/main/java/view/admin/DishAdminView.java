package view.admin;

import model.Dish;
import service.DishService;
import service.impl.DishServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class DishAdminView extends JPanel {
    private final DishService dishService;
    
    // UI Components
    private JTable dishesTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryComboBox;
    private JTextField unitPriceField;
    private JTextField preparationTimeField;
    private JTextField caloriesField;
    private JComboBox<String> statusComboBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Data
    private Integer selectedId = null;
    
    // Constants
    private static final String[] CATEGORY_OPTIONS = {"Appetizer", "Main", "Dessert", "Drink"};
    private static final String[] STATUS_OPTIONS = {"Available", "Out of Stock", "Discontinued"};
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );

    public DishAdminView() throws RemoteException {
        this.dishService = new DishServiceImpl();
        initComponents();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Dish Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descriptionScrollPane, gbc);

        // Category
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>(CATEGORY_OPTIONS);
        formPanel.add(categoryComboBox, gbc);

        // Unit Price
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Unit Price:"), gbc);
        gbc.gridx = 1;
        unitPriceField = new JTextField(10);
        formPanel.add(unitPriceField, gbc);

        // Preparation Time
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Preparation Time (min):"), gbc);
        gbc.gridx = 1;
        preparationTimeField = new JTextField(10);
        formPanel.add(preparationTimeField, gbc);

        // Calories
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Calories:"), gbc);
        gbc.gridx = 1;
        caloriesField = new JTextField(10);
        formPanel.add(caloriesField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(STATUS_OPTIONS);
        formPanel.add(statusComboBox, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = createStyledButton("Add", "Add a new dish");
        updateButton = createStyledButton("Update", "Update selected dish");
        deleteButton = createStyledButton("Delete", "Delete selected dish");
        refreshButton = createStyledButton("Refresh", "Refresh dish data");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Name", "Category", "Unit Price", "Preparation Time", "Calories", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dishesTable = new JTable(tableModel);
        dishesTable.setFillsViewportHeight(true);
        dishesTable.setRowHeight(25);
        dishesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dishesTable.setGridColor(Color.LIGHT_GRAY);
        dishesTable.setShowGrid(true);
        dishesTable.setBackground(Color.WHITE);
        dishesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = dishesTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(dishesTable);
        tableScrollPane.setBorder(new TitledBorder("Dishes List"));
        tableScrollPane.setBackground(Color.WHITE);

        // Add components to main panel
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
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
        dishesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = dishesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    nameField.setText((String) tableModel.getValueAt(selectedRow, 1));
                    
                    // Get the dish details to populate description
                    try {
                        Dish dish = dishService.findById(selectedId);
                        if (dish != null) {
                            descriptionArea.setText(dish.getDescription());
                            categoryComboBox.setSelectedItem(dish.getCategory());
                            unitPriceField.setText(String.valueOf(dish.getUnitPrice()));
                            preparationTimeField.setText(String.valueOf(dish.getPreparationTime()));
                            caloriesField.setText(String.valueOf(dish.getCalories()));
                            statusComboBox.setSelectedItem(dish.getStatus());
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Error loading dish details: " + ex.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addDish());
        updateButton.addActionListener(e -> updateDish());
        deleteButton.addActionListener(e -> deleteDish());
        refreshButton.addActionListener(e -> loadTableData());
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<Dish> dishes = dishService.findAll();
            for (Dish dish : dishes) {
                tableModel.addRow(new Object[]{
                    dish.getId(),
                    dish.getName(),
                    dish.getCategory(),
                    String.format("$%.2f", dish.getUnitPrice()),
                    dish.getPreparationTime(),
                    dish.getCalories(),
                    dish.getStatus()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading dishes: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDish() {
        if (validateForm()) {
            try {
                Dish dish = new Dish();
                dish.setName(nameField.getText());
                dish.setDescription(descriptionArea.getText());
                dish.setCategory((String) categoryComboBox.getSelectedItem());
                dish.setUnitPrice(Double.parseDouble(unitPriceField.getText()));
                dish.setPreparationTime(Integer.parseInt(preparationTimeField.getText()));
                dish.setCalories(Integer.parseInt(caloriesField.getText()));
                dish.setStatus((String) statusComboBox.getSelectedItem());

                dishService.create(dish);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Dish added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error adding dish: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateDish() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Please select a dish to update", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validateForm()) {
            try {
                Dish dish = dishService.findById(selectedId);
                if (dish != null) {
                    dish.setName(nameField.getText());
                    dish.setDescription(descriptionArea.getText());
                    dish.setCategory((String) categoryComboBox.getSelectedItem());
                    dish.setUnitPrice(Double.parseDouble(unitPriceField.getText()));
                    dish.setPreparationTime(Integer.parseInt(preparationTimeField.getText()));
                    dish.setCalories(Integer.parseInt(caloriesField.getText()));
                    dish.setStatus((String) statusComboBox.getSelectedItem());

                    dishService.update(dish);
                    loadTableData();
                    resetForm();
                    JOptionPane.showMessageDialog(this, "Dish updated successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error updating dish: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteDish() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Please select a dish to delete", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this dish?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                dishService.deleteById(selectedId);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Dish deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error deleting dish: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetForm() {
        selectedId = null;
        nameField.setText("");
        descriptionArea.setText("");
        categoryComboBox.setSelectedIndex(0);
        unitPriceField.setText("");
        preparationTimeField.setText("");
        caloriesField.setText("");
        statusComboBox.setSelectedIndex(0);
        dishesTable.clearSelection();
    }

    private boolean validateForm() {
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate unit price
        try {
            double price = Double.parseDouble(unitPriceField.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Unit price must be greater than 0", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Unit price must be a valid number", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate preparation time
        try {
            int prepTime = Integer.parseInt(preparationTimeField.getText());
            if (prepTime <= 0) {
                JOptionPane.showMessageDialog(this, "Preparation time must be greater than 0", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preparation time must be a valid integer", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate calories (optional, can be 0)
        try {
            Integer.parseInt(caloriesField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Calories must be a valid integer", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
} 