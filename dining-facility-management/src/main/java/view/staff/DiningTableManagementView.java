package view.staff;

import model.DiningTable;
import service.DiningTableService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class DiningTableManagementView extends JPanel {
    private final DiningTableService diningTableService;
    
    private JTable tablesTable;
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

    public DiningTableManagementView(DiningTableService diningTableService) {
        this.diningTableService = diningTableService;
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
        searchField.setToolTipText("Search tables by number or location");
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

        addButton = createStyledButton("➕ Add", "Add new table");
        editButton = createStyledButton("✏️ Edit", "Edit selected table");
        deleteButton = createStyledButton("🗑️ Delete", "Delete selected table");
        refreshButton = createStyledButton("🔄 Refresh", "Refresh tables list");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        topPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Table Number", "Capacity", "Status", "Location"};
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
        deleteButton.addActionListener(e -> deleteSelectedTable());
        refreshButton.addActionListener(e -> loadData());
        searchField.addActionListener(e -> searchTables());
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<DiningTable> tables = diningTableService.findAll();
            for (DiningTable table : tables) {
                tableModel.addRow(new Object[]{
                    table.getId(),
                    table.getTableNumber(),
                    table.getSeatingCapacity(),
                    table.getStatus(),
                    table.getLocation()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading tables: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField numberField = new JTextField();
        JTextField capacityField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Occupied", "Reserved"});
        JTextField locationField = new JTextField();

        panel.add(new JLabel("Table Number:"));
        panel.add(numberField);
        panel.add(new JLabel("Capacity:"));
        panel.add(capacityField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Table",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                DiningTable table = new DiningTable();
                table.setTableNumber(Integer.parseInt(numberField.getText()));
                table.setSeatingCapacity(Integer.parseInt(capacityField.getText()));
                table.setStatus((String) statusCombo.getSelectedItem());
                table.setLocation(locationField.getText());

                diningTableService.create(table);
                loadData();
                JOptionPane.showMessageDialog(this, "Table added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding table: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = tablesTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int tableId = (int) tableModel.getValueAt(selectedRow, 0);
                DiningTable table = diningTableService.findById(tableId);

                JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
                panel.setBorder(new EmptyBorder(10, 10, 10, 10));

                JTextField numberField = new JTextField(String.valueOf(table.getTableNumber()));
                JTextField capacityField = new JTextField(String.valueOf(table.getSeatingCapacity()));
                JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Occupied", "Reserved"});
                statusCombo.setSelectedItem(table.getStatus());
                JTextField locationField = new JTextField(table.getLocation());

                panel.add(new JLabel("Table Number:"));
                panel.add(numberField);
                panel.add(new JLabel("Capacity:"));
                panel.add(capacityField);
                panel.add(new JLabel("Status:"));
                panel.add(statusCombo);
                panel.add(new JLabel("Location:"));
                panel.add(locationField);

                int result = JOptionPane.showConfirmDialog(this, panel, "Edit Table",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    table.setTableNumber(Integer.parseInt(numberField.getText()));
                    table.setSeatingCapacity(Integer.parseInt(capacityField.getText()));
                    table.setStatus((String) statusCombo.getSelectedItem());
                    table.setLocation(locationField.getText());

                    diningTableService.update(table);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Table updated successfully!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error editing table: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a table to edit");
        }
    }

    private void deleteSelectedTable() {
        int selectedRow = tablesTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int tableId = (int) tableModel.getValueAt(selectedRow, 0);
                
                int result = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this table?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    diningTableService.deleteById(tableId);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Table deleted successfully!");
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error deleting table: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a table to delete");
        }
    }

    private void searchTables() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        try {
            List<DiningTable> tables = diningTableService.findAll();
            for (DiningTable table : tables) {
                if (String.valueOf(table.getTableNumber()).contains(searchText) ||
                    (table.getLocation() != null && table.getLocation().toLowerCase().contains(searchText))) {
                    tableModel.addRow(new Object[]{
                        table.getId(),
                        table.getTableNumber(),
                        table.getSeatingCapacity(),
                        table.getStatus(),
                        table.getLocation()
                    });
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error searching tables: " + e.getMessage());
        }
    }
} 