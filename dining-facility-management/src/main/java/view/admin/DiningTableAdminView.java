package view.admin;

import model.DiningTable;
import service.DiningTableService;
import service.impl.DiningTableServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class DiningTableAdminView extends JPanel {
    private final DiningTableService diningTableService;
    
    // UI Components
    private JTable tablesTable;
    private DefaultTableModel tableModel;
    private JTextField tableNumberField;
    private JComboBox<String> statusComboBox;
    private JTextField seatingCapacityField;
    private JTextField locationField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Data
    private Integer selectedId = null;
    
    // Constants
    private static final String[] STATUS_OPTIONS = {"Available", "Reserved", "Occupied", "Cleaning"};
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );

    public DiningTableAdminView() throws RemoteException {
        this.diningTableService = new DiningTableServiceImpl();
        initComponents();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Table Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Table Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Table Number:"), gbc);
        gbc.gridx = 1;
        tableNumberField = new JTextField(15);
        formPanel.add(tableNumberField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(STATUS_OPTIONS);
        formPanel.add(statusComboBox, gbc);

        // Seating Capacity
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Seating Capacity:"), gbc);
        gbc.gridx = 1;
        seatingCapacityField = new JTextField(15);
        formPanel.add(seatingCapacityField, gbc);

        // Location
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        locationField = new JTextField(15);
        formPanel.add(locationField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = createStyledButton("Add", "Add a new table");
        updateButton = createStyledButton("Update", "Update selected table");
        deleteButton = createStyledButton("Delete", "Delete selected table");
        refreshButton = createStyledButton("Refresh", "Refresh table data");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Table Number", "Status", "Seating Capacity", "Location"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablesTable = new JTable(tableModel);
        tablesTable.setFillsViewportHeight(true);
        tablesTable.setRowHeight(25);
        tablesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablesTable.setGridColor(Color.LIGHT_GRAY);
        tablesTable.setShowGrid(true);
        tablesTable.setBackground(Color.WHITE);
        tablesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = tablesTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(tablesTable);
        tableScrollPane.setBorder(new TitledBorder("Tables List"));
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
        tablesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tablesTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    tableNumberField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                    statusComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 2));
                    seatingCapacityField.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
                    locationField.setText((String) tableModel.getValueAt(selectedRow, 4));
                }
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addTable());
        updateButton.addActionListener(e -> updateTable());
        deleteButton.addActionListener(e -> deleteTable());
        refreshButton.addActionListener(e -> loadTableData());
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<DiningTable> tables = diningTableService.findAll();
            for (DiningTable table : tables) {
                tableModel.addRow(new Object[]{
                    table.getId(),
                    table.getTableNumber(),
                    table.getStatus(),
                    table.getSeatingCapacity(),
                    table.getLocation()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading tables: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTable() {
        if (validateForm()) {
            try {
                DiningTable table = new DiningTable();
                table.setTableNumber(Integer.parseInt(tableNumberField.getText()));
                table.setStatus((String) statusComboBox.getSelectedItem());
                table.setSeatingCapacity(Integer.parseInt(seatingCapacityField.getText()));
                table.setLocation(locationField.getText());

                diningTableService.create(table);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Table added successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error adding table: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTable() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Please select a table to update", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validateForm()) {
            try {
                DiningTable table = diningTableService.findById(selectedId);
                if (table != null) {
                    table.setTableNumber(Integer.parseInt(tableNumberField.getText()));
                    table.setStatus((String) statusComboBox.getSelectedItem());
                    table.setSeatingCapacity(Integer.parseInt(seatingCapacityField.getText()));
                    table.setLocation(locationField.getText());

                    diningTableService.update(table);
                    loadTableData();
                    resetForm();
                    JOptionPane.showMessageDialog(this, "Table updated successfully!", 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error updating table: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteTable() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Please select a table to delete", 
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this table?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                diningTableService.deleteById(selectedId);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Table deleted successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error deleting table: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetForm() {
        selectedId = null;
        tableNumberField.setText("");
        statusComboBox.setSelectedIndex(0);
        seatingCapacityField.setText("");
        locationField.setText("");
        tablesTable.clearSelection();
    }

    private boolean validateForm() {
        // Validate table number
        try {
            int tableNumber = Integer.parseInt(tableNumberField.getText());
            if (tableNumber <= 0) {
                JOptionPane.showMessageDialog(this, "Table number must be greater than 0", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Table number must be a valid integer", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate seating capacity
        try {
            int capacity = Integer.parseInt(seatingCapacityField.getText());
            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this, "Seating capacity must be greater than 0", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Seating capacity must be a valid integer", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate location (optional field, can be empty)
        
        return true;
    }
} 