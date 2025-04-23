package view.staff;

import model.Dish;
import service.DishService;
import service.RecipeService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class DishManagementView extends JPanel {
    private final DishService dishService;
    private final RecipeService recipeService;
    
    private JTable dishesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );

    public DishManagementView(DishService dishService, RecipeService recipeService) {
        this.dishService = dishService;
        this.recipeService = recipeService;
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
        searchField.setToolTipText("Search dishes by name or category");
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
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        buttonPanel.setBackground(Color.WHITE);

        addButton = createStyledButton("➕ Add", "Add new dish");
        editButton = createStyledButton("✏️ Edit", "Edit selected dish");
        deleteButton = createStyledButton("🗑️ Delete", "Delete selected dish");
        refreshButton = createStyledButton("🔄 Refresh", "Refresh dishes list");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        topPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Name", "Category", "Price", "Status"};
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
        deleteButton.addActionListener(e -> deleteSelectedDish());
        refreshButton.addActionListener(e -> loadData());
        searchField.addActionListener(e -> searchDishes());
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Dish> dishes = dishService.findAll();
            for (Dish dish : dishes) {
                tableModel.addRow(new Object[]{
                    dish.getId(),
                    dish.getName(),
                    dish.getCategory(),
                    String.format("$%.2f", dish.getUnitPrice()),
                    dish.getStatus()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading dishes: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Unavailable"});

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Dish",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Dish dish = new Dish();
                dish.setName(nameField.getText());
                dish.setCategory(categoryField.getText());
                dish.setUnitPrice(Double.parseDouble(priceField.getText()));
                dish.setStatus((String) statusCombo.getSelectedItem());

                dishService.create(dish);
                loadData();
                JOptionPane.showMessageDialog(this, "Dish added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding dish: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = dishesTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int dishId = (int) tableModel.getValueAt(selectedRow, 0);
                Dish dish = dishService.findById(dishId);

                JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
                panel.setBorder(new EmptyBorder(10, 10, 10, 10));

                JTextField nameField = new JTextField(dish.getName());
                JTextField categoryField = new JTextField(dish.getCategory());
                JTextField priceField = new JTextField(String.valueOf(dish.getUnitPrice()));
                JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Unavailable"});
                statusCombo.setSelectedItem(dish.getStatus());

                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Category:"));
                panel.add(categoryField);
                panel.add(new JLabel("Price:"));
                panel.add(priceField);
                panel.add(new JLabel("Status:"));
                panel.add(statusCombo);

                int result = JOptionPane.showConfirmDialog(this, panel, "Edit Dish",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    dish.setName(nameField.getText());
                    dish.setCategory(categoryField.getText());
                    dish.setUnitPrice(Double.parseDouble(priceField.getText()));
                    dish.setStatus((String) statusCombo.getSelectedItem());

                    dishService.update(dish);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Dish updated successfully!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error editing dish: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a dish to edit");
        }
    }

    private void deleteSelectedDish() {
        int selectedRow = dishesTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int dishId = (int) tableModel.getValueAt(selectedRow, 0);
                
                int result = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this dish?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    dishService.deleteById(dishId);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Dish deleted successfully!");
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error deleting dish: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a dish to delete");
        }
    }

    private void searchDishes() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        try {
            List<Dish> dishes = dishService.findAll();
            for (Dish dish : dishes) {
                if (dish.getName().toLowerCase().contains(searchText) ||
                    dish.getCategory().toLowerCase().contains(searchText) ||
                    dish.getStatus().toLowerCase().contains(searchText)) {
                    tableModel.addRow(new Object[]{
                        dish.getId(),
                        dish.getName(),
                        dish.getCategory(),
                        String.format("$%.2f", dish.getUnitPrice()),
                        dish.getStatus()
                    });
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error searching dishes: " + e.getMessage());
        }
    }
} 