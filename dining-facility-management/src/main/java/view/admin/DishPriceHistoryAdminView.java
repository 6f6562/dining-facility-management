package view.admin;

import model.Dish;
import model.DishPriceHistory;
import service.DishPriceHistoryService;
import service.DishService;
import service.impl.DishPriceHistoryServiceImpl;
import service.impl.DishServiceImpl;

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

public class DishPriceHistoryAdminView extends JPanel {
    private final DishPriceHistoryService priceHistoryService;
    private final DishService dishService;
    
    // UI Components
    private JTable priceHistoryTable;
    private DefaultTableModel tableModel;
    private JComboBox<Dish> dishComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextField priceField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Data
    private Integer selectedId = null;
    
    // Constants
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );

    public DishPriceHistoryAdminView() throws RemoteException {
        this.priceHistoryService = new DishPriceHistoryServiceImpl();
        this.dishService = new DishServiceImpl();
        initComponents();
        loadDishOptions();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Price History Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Dish
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Dish:"), gbc);
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

        // Start Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Start Date (dd/MM/yyyy HH:mm):"), gbc);
        gbc.gridx = 1;
        startDateField = new JTextField(15);
        formPanel.add(startDateField, gbc);

        // End Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("End Date (dd/MM/yyyy HH:mm):"), gbc);
        gbc.gridx = 1;
        endDateField = new JTextField(15);
        formPanel.add(endDateField, gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Price (VNĐ):"), gbc);
        gbc.gridx = 1;
        priceField = new JTextField(10);
        formPanel.add(priceField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = createStyledButton("Add", "Add a new price history");
        updateButton = createStyledButton("Update", "Update selected price history");
        deleteButton = createStyledButton("Delete", "Delete selected price history");
        refreshButton = createStyledButton("Refresh", "Refresh price history data");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Dish", "Start Date", "End Date", "Price (VNĐ)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        priceHistoryTable = new JTable(tableModel);
        priceHistoryTable.setFillsViewportHeight(true);
        priceHistoryTable.setRowHeight(25);
        priceHistoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        priceHistoryTable.setGridColor(Color.LIGHT_GRAY);
        priceHistoryTable.setShowGrid(true);
        priceHistoryTable.setBackground(Color.WHITE);
        priceHistoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = priceHistoryTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(priceHistoryTable);
        tableScrollPane.setBorder(new TitledBorder("Price History List"));
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
            JOptionPane.showMessageDialog(this, "Error loading dishes: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
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
        priceHistoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = priceHistoryTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    
                    // Get the price history details
                    try {
                        DishPriceHistory priceHistory = priceHistoryService.findById(selectedId);
                        if (priceHistory != null) {
                            // Set dish in combo box
                            for (int i = 0; i < dishComboBox.getItemCount(); i++) {
                                Dish dish = dishComboBox.getItemAt(i);
                                if (dish.getId() == priceHistory.getDish().getId()) {
                                    dishComboBox.setSelectedIndex(i);
                                    break;
                                }
                            }
                            
                            // Set other fields
                            startDateField.setText(priceHistory.getStartDate().format(DATE_FORMATTER));
                            if (priceHistory.getEndDate() != null) {
                                endDateField.setText(priceHistory.getEndDate().format(DATE_FORMATTER));
                            } else {
                                endDateField.setText("");
                            }
                            priceField.setText(String.valueOf(priceHistory.getPrice()));
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Error loading price history details: " + ex.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addPriceHistory());
        updateButton.addActionListener(e -> updatePriceHistory());
        deleteButton.addActionListener(e -> deletePriceHistory());
        refreshButton.addActionListener(e -> loadTableData());
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<DishPriceHistory> priceHistories = priceHistoryService.findAll();
            for (DishPriceHistory priceHistory : priceHistories) {
                String endDateStr = priceHistory.getEndDate() != null ? 
                        priceHistory.getEndDate().format(DATE_FORMATTER) : "Ongoing";
                
                tableModel.addRow(new Object[]{
                    priceHistory.getId(),
                    priceHistory.getDish().getName(),
                    priceHistory.getStartDate().format(DATE_FORMATTER),
                    endDateStr,
                    String.format("%,.0f", priceHistory.getPrice())
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading price histories: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPriceHistory() {
        if (validateForm()) {
            try {
                DishPriceHistory priceHistory = new DishPriceHistory();
                priceHistory.setDish((Dish) dishComboBox.getSelectedItem());
                priceHistory.setStartDate(parseDateTime(startDateField.getText()));
                
                if (!endDateField.getText().trim().isEmpty()) {
                    priceHistory.setEndDate(parseDateTime(endDateField.getText()));
                }
                
                priceHistory.setPrice(Double.parseDouble(priceField.getText()));

                priceHistoryService.create(priceHistory);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Price history added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error adding price history: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updatePriceHistory() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Please select a price history to update", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validateForm()) {
            try {
                DishPriceHistory priceHistory = priceHistoryService.findById(selectedId);
                if (priceHistory != null) {
                    priceHistory.setDish((Dish) dishComboBox.getSelectedItem());
                    priceHistory.setStartDate(parseDateTime(startDateField.getText()));
                    
                    if (!endDateField.getText().trim().isEmpty()) {
                        priceHistory.setEndDate(parseDateTime(endDateField.getText()));
                    } else {
                        priceHistory.setEndDate(null);
                    }
                    
                    priceHistory.setPrice(Double.parseDouble(priceField.getText()));

                    priceHistoryService.update(priceHistory);
                    loadTableData();
                    resetForm();
                    JOptionPane.showMessageDialog(this, "Price history updated successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error updating price history: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePriceHistory() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Please select a price history to delete", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this price history?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                priceHistoryService.deleteById(selectedId);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Price history deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error deleting price history: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetForm() {
        selectedId = null;
        dishComboBox.setSelectedIndex(0);
        startDateField.setText("");
        endDateField.setText("");
        priceField.setText("");
        priceHistoryTable.clearSelection();
    }

    private boolean validateForm() {
        // Validate dish selection
        if (dishComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a dish", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate start date
        try {
            LocalDateTime startDate = parseDateTime(startDateField.getText());
            if (startDate == null) {
                JOptionPane.showMessageDialog(this, "Start date is required", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Start date must be in format dd/MM/yyyy HH:mm", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate end date if provided
        if (!endDateField.getText().trim().isEmpty()) {
            try {
                LocalDateTime endDate = parseDateTime(endDateField.getText());
                LocalDateTime startDate = parseDateTime(startDateField.getText());
                
                if (endDate.isBefore(startDate)) {
                    JOptionPane.showMessageDialog(this, "End date cannot be before start date", 
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "End date must be in format dd/MM/yyyy HH:mm", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // Validate price
        try {
            double price = Double.parseDouble(priceField.getText());
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than 0", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr.trim(), DATE_FORMATTER);
    }
} 