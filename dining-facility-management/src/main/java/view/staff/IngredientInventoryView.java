package view.staff;

import model.Ingredient;
import model.IngredientModel;
import service.IngredientService;
import service.IngredientModelService;
import service.impl.IngredientServiceImpl;
import service.impl.IngredientModelServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class IngredientInventoryView extends JPanel {
    private final IngredientServiceImpl ingredientService;
    private final IngredientModelServiceImpl ingredientModelService;
    
    private JTable ingredientTable;
    private DefaultTableModel ingredientModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton reorderButton;

    public IngredientInventoryView(IngredientServiceImpl ingredientService, IngredientModelServiceImpl ingredientModelService) {
        this.ingredientService = ingredientService;
        this.ingredientModelService = ingredientModelService;
        initComponents();
        setupEventListeners();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Add Ingredient");
        editButton = new JButton("Edit Ingredient");
        deleteButton = new JButton("Delete Ingredient");
        reorderButton = new JButton("Reorder");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(reorderButton);
        add(buttonPanel, BorderLayout.NORTH);

        // Ingredient table
        String[] columnNames = {"ID", "Name", "Model", "Unit of Measure", "Stock Quantity", 
                              "Expiry Date", "Safety Stock", "Reorder Point"};
        ingredientModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ingredientTable = new JTable(ingredientModel);
        JScrollPane scrollPane = new JScrollPane(ingredientTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        addButton.addActionListener(e -> showAddIngredientDialog());
        editButton.addActionListener(e -> showEditIngredientDialog());
        deleteButton.addActionListener(e -> deleteSelectedIngredient());
        reorderButton.addActionListener(e -> showReorderDialog());
    }

    private void loadData() {
        try {
            ingredientModel.setRowCount(0);
            List<Ingredient> ingredients = ingredientService.findAll();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Ingredient ingredient : ingredients) {
                ingredientModel.addRow(new Object[]{
                        ingredient.getId(),
                        ingredient.getName(),
                        ingredient.getIngredientModel().getName(),
                        ingredient.getUnitOfMeasure(),
                        ingredient.getStockQuantity(),
                        ingredient.getExpiryDate() != null
                                ? ingredient.getExpiryDate().format(formatter)
                                : "N/A",
                        ingredient.getSafetyStockLevel(),
                        ingredient.getReorderPoint()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading ingredients: " + e.getMessage());
        }
    }

    private void showAddIngredientDialog() {
        try {
            List<IngredientModel> models = ingredientModelService.findAll();
            JPanel panel = new JPanel(new GridLayout(8, 2));
            
            JTextField nameField = new JTextField();
            JComboBox<IngredientModel> modelCombo = new JComboBox<>(models.toArray(new IngredientModel[0]));
            JTextField unitField = new JTextField();
            JTextField quantityField = new JTextField();
            JTextField expiryDateField = new JTextField();
            JTextField safetyStockField = new JTextField();
            JTextField reorderPointField = new JTextField();

            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Model:"));
            panel.add(modelCombo);
            panel.add(new JLabel("Unit of Measure:"));
            panel.add(unitField);
            panel.add(new JLabel("Stock Quantity:"));
            panel.add(quantityField);
            panel.add(new JLabel("Expiry Date (yyyy-MM-dd):"));
            panel.add(expiryDateField);
            panel.add(new JLabel("Safety Stock Level:"));
            panel.add(safetyStockField);
            panel.add(new JLabel("Reorder Point:"));
            panel.add(reorderPointField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Add New Ingredient",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(nameField.getText());
                ingredient.setIngredientModel((IngredientModel) modelCombo.getSelectedItem());
                ingredient.setUnitOfMeasure(unitField.getText());
                ingredient.setStockQuantity(Double.parseDouble(quantityField.getText()));
                
                if (!expiryDateField.getText().isEmpty()) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate localDate = LocalDate.parse(expiryDateField.getText(), formatter);

                    ingredient.setExpiryDate(localDate);
                }
                
                ingredient.setSafetyStockLevel(Double.parseDouble(safetyStockField.getText()));
                ingredient.setReorderPoint(Double.parseDouble(reorderPointField.getText()));
                
                ingredientService.create(ingredient);
                loadData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding ingredient: " + e.getMessage());
        }
    }

    private void showEditIngredientDialog() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int ingredientId = (int) ingredientModel.getValueAt(selectedRow, 0);
                Ingredient ingredient = ingredientService.findById(ingredientId);
                List<IngredientModel> models = ingredientModelService.findAll();
                
                JPanel panel = new JPanel(new GridLayout(8, 2));
                
                JTextField nameField = new JTextField(ingredient.getName());
                JComboBox<IngredientModel> modelCombo = new JComboBox<>(models.toArray(new IngredientModel[0]));
                modelCombo.setSelectedItem(ingredient.getIngredientModel());
                JTextField unitField = new JTextField(ingredient.getUnitOfMeasure());
                JTextField quantityField = new JTextField(String.valueOf(ingredient.getStockQuantity()));
                JTextField expiryDateField = new JTextField(ingredient.getExpiryDate() != null ? 
                        new SimpleDateFormat("yyyy-MM-dd").format(ingredient.getExpiryDate()) : "");
                JTextField safetyStockField = new JTextField(String.valueOf(ingredient.getSafetyStockLevel()));
                JTextField reorderPointField = new JTextField(String.valueOf(ingredient.getReorderPoint()));

                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Model:"));
                panel.add(modelCombo);
                panel.add(new JLabel("Unit of Measure:"));
                panel.add(unitField);
                panel.add(new JLabel("Stock Quantity:"));
                panel.add(quantityField);
                panel.add(new JLabel("Expiry Date (yyyy-MM-dd):"));
                panel.add(expiryDateField);
                panel.add(new JLabel("Safety Stock Level:"));
                panel.add(safetyStockField);
                panel.add(new JLabel("Reorder Point:"));
                panel.add(reorderPointField);

                int result = JOptionPane.showConfirmDialog(this, panel, "Edit Ingredient",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    ingredient.setName(nameField.getText());
                    ingredient.setIngredientModel((IngredientModel) modelCombo.getSelectedItem());
                    ingredient.setUnitOfMeasure(unitField.getText());
                    ingredient.setStockQuantity(Double.parseDouble(quantityField.getText()));
                    
                    if (!expiryDateField.getText().isEmpty()) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate localDate = LocalDate.parse(expiryDateField.getText(), formatter);

                        ingredient.setExpiryDate(localDate);
                    }
                    
                    ingredient.setSafetyStockLevel(Double.parseDouble(safetyStockField.getText()));
                    ingredient.setReorderPoint(Double.parseDouble(reorderPointField.getText()));
                    
                    ingredientService.update(ingredient);
                    loadData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error editing ingredient: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ingredient to edit");
        }
    }

    private void deleteSelectedIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int ingredientId = (int) ingredientModel.getValueAt(selectedRow, 0);
                int result = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this ingredient?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    ingredientService.deleteById(ingredientId);
                    loadData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting ingredient: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an ingredient to delete");
        }
    }

    private void showReorderDialog() {
        try {
            List<Ingredient> ingredients = ingredientService.findAll();
            StringBuilder reorderList = new StringBuilder("Ingredients below reorder point:\n\n");
            boolean needsReorder = false;
            
            for (Ingredient ingredient : ingredients) {
                if (ingredient.getStockQuantity() <= ingredient.getReorderPoint()) {
                    needsReorder = true;
                    reorderList.append(ingredient.getName())
                              .append(" - Current: ")
                              .append(ingredient.getStockQuantity())
                              .append(", Reorder Point: ")
                              .append(ingredient.getReorderPoint())
                              .append("\n");
                }
            }
            
            if (needsReorder) {
                JOptionPane.showMessageDialog(this, reorderList.toString(), 
                        "Reorder Alert", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "All ingredients are above reorder point",
                        "Inventory Status", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking inventory: " + e.getMessage());
        }
    }
}