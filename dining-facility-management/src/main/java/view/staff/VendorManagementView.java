package view.staff;

import model.Vendor;
import service.VendorService;
import service.impl.VendorServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class VendorManagementView extends JPanel {
    private final VendorServiceImpl vendorService;
    
    private JTable vendorsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton toggleActiveButton;
    private JButton togglePreferredButton;
    
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color WARNING_COLOR = new Color(255, 87, 34);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );

    public VendorManagementView(VendorServiceImpl vendorService) {
        this.vendorService = vendorService;
        initComponents();
        setupEventListeners();
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
        searchField.setToolTipText("Search vendors by name or address");
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
        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 5, 0));
        buttonPanel.setBackground(Color.WHITE);

        addButton = createStyledButton("➕ Add", "Add new vendor");
        editButton = createStyledButton("✏️ Edit", "Edit selected vendor");
        deleteButton = createStyledButton("🗑️ Delete", "Delete selected vendor");
        refreshButton = createStyledButton("🔄 Refresh", "Refresh vendor list");
        toggleActiveButton = createStyledButton("🟢 Toggle Active", "Toggle vendor active status");
        togglePreferredButton = createStyledButton("⭐ Toggle Preferred", "Toggle vendor preferred status");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(toggleActiveButton);
        buttonPanel.add(togglePreferredButton);

        topPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Name", "Address", "Active", "Preferred"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vendorsTable = new JTable(tableModel);
        vendorsTable.setFillsViewportHeight(true);
        vendorsTable.setRowHeight(25);
        vendorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vendorsTable.setGridColor(Color.LIGHT_GRAY);
        vendorsTable.setShowGrid(true);
        vendorsTable.setBackground(Color.WHITE);
        vendorsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = vendorsTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(vendorsTable);
        tableScrollPane.setBorder(new TitledBorder("Vendors List"));
        tableScrollPane.setBackground(Color.WHITE);

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
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
        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteSelectedVendor());
        refreshButton.addActionListener(e -> loadData());
        toggleActiveButton.addActionListener(e -> toggleActiveStatus());
        togglePreferredButton.addActionListener(e -> togglePreferredStatus());
        searchField.addActionListener(e -> searchVendors());
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Vendor> vendors = vendorService.findAll();
            for (Vendor vendor : vendors) {
                tableModel.addRow(new Object[]{
                    vendor.getId(),
                    vendor.getName(),
                    vendor.getAddress(),
                    vendor.isActiveFlag() ? "Yes" : "No",
                    vendor.isPreferredVendorStatus() ? "Yes" : "No"
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading vendors: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField nameField = new JTextField();
        JTextArea addressArea = new JTextArea(3, 20);
        JCheckBox preferredCheckBox = new JCheckBox("Preferred Vendor");

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(new JScrollPane(addressArea));
        panel.add(new JLabel(""));
        panel.add(preferredCheckBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Vendor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Vendor vendor = new Vendor();
                vendor.setName(nameField.getText());
                vendor.setAddress(addressArea.getText());
                vendor.setActiveFlag(true);
                vendor.setPreferredVendorStatus(preferredCheckBox.isSelected());
                
                vendorService.create(vendor);
                loadData();
                JOptionPane.showMessageDialog(this, "Vendor added successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding vendor: " + e.getMessage());
            }
        }
    }

    private void showEditDialog() {
        int selectedRow = vendorsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int vendorId = (int) tableModel.getValueAt(selectedRow, 0);
                Vendor vendor = vendorService.findById(vendorId);
                
                JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
                panel.setBorder(new EmptyBorder(10, 10, 10, 10));

                JTextField nameField = new JTextField(vendor.getName());
                JTextArea addressArea = new JTextArea(vendor.getAddress(), 3, 20);
                JCheckBox preferredCheckBox = new JCheckBox("Preferred Vendor", vendor.isPreferredVendorStatus());

                panel.add(new JLabel("Name:"));
                panel.add(nameField);
                panel.add(new JLabel("Address:"));
                panel.add(new JScrollPane(addressArea));
                panel.add(new JLabel(""));
                panel.add(preferredCheckBox);

                int result = JOptionPane.showConfirmDialog(this, panel, "Edit Vendor",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    vendor.setName(nameField.getText());
                    vendor.setAddress(addressArea.getText());
                    vendor.setPreferredVendorStatus(preferredCheckBox.isSelected());
                    
                    vendorService.update(vendor);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Vendor updated successfully!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error editing vendor: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a vendor to edit");
        }
    }

    private void deleteSelectedVendor() {
        int selectedRow = vendorsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int vendorId = (int) tableModel.getValueAt(selectedRow, 0);
                
                int result = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this vendor?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    vendorService.deleteById(vendorId);
                    loadData();
                    JOptionPane.showMessageDialog(this, "Vendor deleted successfully!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting vendor: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a vendor to delete");
        }
    }

    private void toggleActiveStatus() {
        int selectedRow = vendorsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int vendorId = (int) tableModel.getValueAt(selectedRow, 0);
                Vendor vendor = vendorService.findById(vendorId);
                
                vendor.setActiveFlag(!vendor.isActiveFlag());
                vendorService.update(vendor);
                loadData();
                
                JOptionPane.showMessageDialog(this,
                        "Vendor " + (vendor.isActiveFlag() ? "activated" : "deactivated") + " successfully!",
                        "Status Updated",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error toggling active status: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a vendor to toggle active status");
        }
    }

    private void togglePreferredStatus() {
        int selectedRow = vendorsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int vendorId = (int) tableModel.getValueAt(selectedRow, 0);
                Vendor vendor = vendorService.findById(vendorId);
                
                vendor.setPreferredVendorStatus(!vendor.isPreferredVendorStatus());
                vendorService.update(vendor);
                loadData();
                
                JOptionPane.showMessageDialog(this,
                        "Vendor " + (vendor.isPreferredVendorStatus() ? "marked as preferred" : "unmarked as preferred") + " successfully!",
                        "Status Updated",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error toggling preferred status: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a vendor to toggle preferred status");
        }
    }

    private void searchVendors() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        try {
            List<Vendor> vendors = vendorService.findAll();
            for (Vendor vendor : vendors) {
                if (vendor.getName().toLowerCase().contains(searchText) ||
                    vendor.getAddress().toLowerCase().contains(searchText)) {
                    tableModel.addRow(new Object[]{
                        vendor.getId(),
                        vendor.getName(),
                        vendor.getAddress(),
                        vendor.isActiveFlag() ? "Yes" : "No",
                        vendor.isPreferredVendorStatus() ? "Yes" : "No"
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching vendors: " + e.getMessage());
        }
    }
}