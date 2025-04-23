package view.staff;

import model.Dish;
import model.OrderDetail;
import model.OrderHeader;
import service.DishService;
import service.OrderHeaderService;
import service.OrderDetailService;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderManagementView extends JPanel {
    private final OrderHeaderService orderHeaderService;
    private final OrderDetailService orderDetailService;
    private final DishService dishService;
    private final int tableId;
    
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton viewDetailsButton;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );

    public OrderManagementView(int tableId, OrderHeaderService orderHeaderService, 
                             OrderDetailService orderDetailService, DishService dishService) {
        this.tableId = tableId;
        this.orderHeaderService = orderHeaderService;
        this.orderDetailService = orderDetailService;
        this.dishService = dishService;
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
        searchField.setToolTipText("Search orders by ID or table number");
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

        addButton = createStyledButton("➕ Add", "Create new order");
        editButton = createStyledButton("✏️ Edit", "Edit selected order");
        deleteButton = createStyledButton("🗑️ Delete", "Delete selected order");
        refreshButton = createStyledButton("🔄 Refresh", "Refresh order list");
        viewDetailsButton = createStyledButton("📋 Details", "View order details");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton);

        topPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Table Number", "Status", "Total Amount", "Created At", "Updated At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(tableModel);
        ordersTable.setFillsViewportHeight(true);
        ordersTable.setRowHeight(25);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setGridColor(Color.LIGHT_GRAY);
        ordersTable.setShowGrid(true);
        ordersTable.setBackground(Color.WHITE);
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = ordersTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(ordersTable);
        tableScrollPane.setBorder(new TitledBorder("Orders List"));
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
        deleteButton.addActionListener(e -> deleteSelectedOrder());
        refreshButton.addActionListener(e -> loadData());
        viewDetailsButton.addActionListener(e -> viewOrderDetails());
        searchField.addActionListener(e -> searchOrders());
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            // TODO: Load order details for the current table
            updateTotal();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void showAddDishDialog() {
        try {
            List<Dish> dishes = dishService.findAll();
            JComboBox<Dish> dishCombo = new JComboBox<>(dishes.toArray(new Dish[0]));
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

            JPanel panel = new JPanel(new GridLayout(2, 2));
            panel.add(new JLabel("Dish:"));
            panel.add(dishCombo);
            panel.add(new JLabel("Quantity:"));
            panel.add(quantitySpinner);

            int result = JOptionPane.showConfirmDialog(this, panel, "Add Dish", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                Dish selectedDish = (Dish) dishCombo.getSelectedItem();
                int quantity = (int) quantitySpinner.getValue();
                addDishToOrder(selectedDish, quantity);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading dishes: " + e.getMessage());
        }
    }

    private void addDishToOrder(Dish dish, int quantity) {
        double subtotal = dish.getUnitPrice() * quantity;
        tableModel.addRow(new Object[]{
                dish.getId(),
                dish.getName(),
                quantity,
                dish.getUnitPrice(),
                subtotal
        });
        updateTotal();
    }

    private void removeSelectedDish() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (double) tableModel.getValueAt(i, 4); // Subtotal column
        }
        // Assuming totalLabel is a JLabel in the original code
        // totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void completeOrder() {
        // TODO: Implement order completion logic
        JOptionPane.showMessageDialog(this, "Order completed!");
    }

    private void sendToKitchen() {
        // TODO: Implement send to kitchen logic
        JOptionPane.showMessageDialog(this, "Order sent to kitchen!");
    }

    private void showAddDialog() {
        // Implementation of showAddDialog method
    }

    private void showEditDialog() {
        // Implementation of showEditDialog method
    }

    private void deleteSelectedOrder() {
        // Implementation of deleteSelectedOrder method
    }

    private void viewOrderDetails() {
        // Implementation of viewOrderDetails method
    }

    private void searchOrders() {
        // Implementation of searchOrders method
    }
} 