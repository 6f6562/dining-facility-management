package view.staff;

import model.Ingredient;
import model.IngredientModel;
import service.IngredientService;
import service.IngredientModelService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public class InventoryView extends JPanel {
    private final IngredientService ingredientService;
    private final IngredientModelService ingredientModelService;
    
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton checkReorderButton;
    
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );

    public InventoryView(IngredientService ingredientService, IngredientModelService ingredientModelService) {
        this.ingredientService = ingredientService;
        this.ingredientModelService = ingredientModelService;
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
        searchField.setToolTipText("Search inventory by name or model");
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

        addButton = createStyledButton("➕ Add", "Add new inventory item");
        editButton = createStyledButton("✏️ Edit", "Edit selected inventory item");
        deleteButton = createStyledButton("🗑️ Delete", "Delete selected inventory item");
        refreshButton = createStyledButton("🔄 Refresh", "Refresh inventory list");
        checkReorderButton = createStyledButton("⚠️ Reorder", "Check reorder status");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(checkReorderButton);

        topPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Name", "Model", "Unit", "Stock", "Expiry Date", "Safety Stock", "Reorder Point"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFillsViewportHeight(true);
        inventoryTable.setRowHeight(25);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setGridColor(Color.LIGHT_GRAY);
        inventoryTable.setShowGrid(true);
        inventoryTable.setBackground(Color.WHITE);
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = inventoryTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        tableScrollPane.setBorder(new TitledBorder("Inventory List"));
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
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteSelectedItem());
        refreshButton.addActionListener(e -> loadData());
        checkReorderButton.addActionListener(e -> checkReorderStatus());
        searchField.addActionListener(e -> searchInventory());
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Ingredient> ingredients = ingredientService.findAll();
            for (Ingredient ingredient : ingredients) {
                tableModel.addRow(new Object[]{
                    ingredient.getId(),
                    ingredient.getName(),
                    ingredient.getIngredientModel().getName(),
                    ingredient.getUnitOfMeasure(),
                    ingredient.getStockQuantity(),
                    ingredient.getExpiryDate(),
                    ingredient.getSafetyStockLevel(),
                    ingredient.getReorderPoint()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading inventory: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JComboBox<IngredientModel> modelCombo = new JComboBox<>();
        JTextField unitField = new JTextField();
        JTextField stockField = new JTextField();
        JTextField expiryDateField = new JTextField();
        JTextField safetyStockField = new JTextField();
        JTextField reorderPointField = new JTextField();

        try {
            List<IngredientModel> models = ingredientModelService.findAll();
            for (IngredientModel model : models) {
                modelCombo.addItem(model);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading ingredient models: " + e.getMessage());
        }

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Model:"));
        panel.add(modelCombo);
        panel.add(new JLabel("Unit of Measure:"));
        panel.add(unitField);
        panel.add(new JLabel("Stock Quantity:"));
        panel.add(stockField);
        panel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
        panel.add(expiryDateField);
        panel.add(new JLabel("Safety Stock Level:"));
        panel.add(safetyStockField);
        panel.add(new JLabel("Reorder Point:"));
        panel.add(reorderPointField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Inventory Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(nameField.getText());
                ingredient.setIngredientModel((IngredientModel) modelCombo.getSelectedItem());
                ingredient.setUnitOfMeasure(unitField.getText());
                ingredient.setStockQuantity(Double.parseDouble(stockField.getText()));
                ingredient.setExpiryDate(LocalDate.parse(expiryDateField.getText()));
                ingredient.setSafetyStockLevel(Double.parseDouble(safetyStockField.getText()));
                ingredient.setReorderPoint(Double.parseDouble(reorderPointField.getText()));

                ingredientService.create(ingredient);
                loadData();
                JOptionPane.showMessageDialog(this, "Inventory item added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding inventory item: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int ingredientId = (int) tableModel.getValueAt(selectedRow, 0);
                Ingredient ingredient = ingredientService.findById(ingredientId);

                JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
                panel.setBorder(new EmptyBorder(10, 10, 10, 10));

                JTextField nameField = new JTextField(ingredient.getName());
                JComboBox<IngredientModel> modelCombo = new JComboBox<>();
                JTextField unitField = new JTextField(ingredient.getUnitOfMeasure());
                JTextField stockField = new JTextField(String.valueOf(ingredient.getStockQuantity()));
                JTextField expiryDateField = new JTextField(ingredient.getExpiryDate().toString());
                JTextField safetyStockField = new JTextField(String.valueOf(ingredient.getSafetyStockLevel()));
                JTextField reorderPointField = new JTextField(String.valueOf(ingredient.getReorderPoint()));

                List<IngredientModel> models = ingredientModelService.findAll();
                for (IngredientModel model : models) {
                    modelCombo.addItem(model);
                    if (model.getId() == ingredient.getIngredientModel().getId()) {
                        modelCombo.setSelectedItem(model);
                    }
                }

                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Model:"));
                panel.add(modelCombo);
                panel.add(new JLabel("Unit of Measure:"));
                panel.add(unitField);
                panel.add(new JLabel("Stock Quantity:"));
                panel.add(stockField);
                panel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
                panel.add(expiryDateField);
                panel.add(new JLabel("Safety Stock Level:"));
                panel.add(safetyStockField);
                panel.add(new JLabel("Reorder Point:"));
                panel.add(reorderPointField);

                int result = JOptionPane.showConfirmDialog(this, panel, "Edit Inventory Item",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    ingredient.setName(nameField.getText());
                    ingredient.setIngredientModel((IngredientModel) modelCombo.getSelectedItem());
                    ingredient.setUnitOfMeasure(unitField.getText());
                    ingredient.setStockQuantity(Double.parseDouble(stockField.getText()));
                    ingredient.setExpiryDate(LocalDate.parse(expiryDateField.getText()));
                    ingredient.setSafetyStockLevel(Double.parseDouble(safetyStockField.getText()));
                    ingredient.setReorderPoint(Double.parseDouble(reorderPointField.getText()));

                    ingredientService.update(ingredient);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Inventory item updated successfully!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error editing inventory item: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an inventory item to edit");
        }
    }

    private void deleteSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int ingredientId = (int) tableModel.getValueAt(selectedRow, 0);
                
                int result = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this inventory item?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    ingredientService.deleteById(ingredientId);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Inventory item deleted successfully!");
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error deleting inventory item: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an inventory item to delete");
        }
    }

    private void checkReorderStatus() {
        try {
            List<Ingredient> ingredients = ingredientService.findAll();
            StringBuilder reorderList = new StringBuilder();
            reorderList.append("Items that need reordering:\n\n");

            for (Ingredient ingredient : ingredients) {
                if (ingredient.getStockQuantity() <= ingredient.getReorderPoint()) {
                    reorderList.append(String.format("- %s (%s)\n", ingredient.getName(), 
                        ingredient.getIngredientModel().getName()));
                }
            }

            if (reorderList.length() > 30) { // More than just the header
                JTextArea textArea = new JTextArea(reorderList.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                JOptionPane.showMessageDialog(this, scrollPane, "Reorder Status", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No items need reordering at this time.",
                    "Reorder Status", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error checking reorder status: " + e.getMessage());
        }
    }

    private void searchInventory() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        try {
            List<Ingredient> ingredients = ingredientService.findAll();
            for (Ingredient ingredient : ingredients) {
                if (ingredient.getName().toLowerCase().contains(searchText) ||
                    ingredient.getIngredientModel().getName().toLowerCase().contains(searchText)) {
                    tableModel.addRow(new Object[]{
                        ingredient.getId(),
                        ingredient.getName(),
                        ingredient.getIngredientModel().getName(),
                        ingredient.getUnitOfMeasure(),
                        ingredient.getStockQuantity(),
                        ingredient.getExpiryDate(),
                        ingredient.getSafetyStockLevel(),
                        ingredient.getReorderPoint()
                    });
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error searching inventory: " + e.getMessage());
        }
    }
} 