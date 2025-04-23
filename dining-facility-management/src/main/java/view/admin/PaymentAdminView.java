package view.admin;

import model.OrderHeader;
import model.Payment;
import service.OrderHeaderService;
import service.PaymentService;
import service.impl.OrderHeaderServiceImpl;
import service.impl.PaymentServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentAdminView extends JPanel {
    private final PaymentService paymentService;
    private final OrderHeaderService orderHeaderService;
    
    // UI Components
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private JComboBox<OrderHeader> orderHeaderComboBox;
    private JTextField amountField;
    private JComboBox<String> paymentMethodComboBox;
    private JComboBox<String> statusComboBox;
    private JTextField createdAtField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Data
    private Integer selectedId = null;
    
    // Constants
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );
    private static final String[] PAYMENT_METHODS = {"Cash", "Credit Card", "Debit Card", "Mobile Payment"};
    private static final String[] STATUS_OPTIONS = {"Pending", "Completed", "Failed", "Refunded"};

    public PaymentAdminView() throws RemoteException {
        this.paymentService = new PaymentServiceImpl();
        this.orderHeaderService = new OrderHeaderServiceImpl();
        initComponents();
        loadOrderOptions();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Order Header ComboBox
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Order:"), gbc);
        gbc.gridx = 1;
        orderHeaderComboBox = new JComboBox<>();
        orderHeaderComboBox.setBorder(COMPONENT_BORDER);
        formPanel.add(orderHeaderComboBox, gbc);

        // Amount Field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField();
        amountField.setBorder(COMPONENT_BORDER);
        formPanel.add(amountField, gbc);

        // Payment Method ComboBox
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1;
        paymentMethodComboBox = new JComboBox<>(PAYMENT_METHODS);
        paymentMethodComboBox.setBorder(COMPONENT_BORDER);
        formPanel.add(paymentMethodComboBox, gbc);

        // Status ComboBox
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusComboBox = new JComboBox<>(STATUS_OPTIONS);
        statusComboBox.setBorder(COMPONENT_BORDER);
        formPanel.add(statusComboBox, gbc);

        // Created At Field
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Created At:"), gbc);
        gbc.gridx = 1;
        createdAtField = new JTextField();
        createdAtField.setBorder(COMPONENT_BORDER);
        createdAtField.setEditable(false);
        formPanel.add(createdAtField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        updateButton.setBackground(PRIMARY_COLOR);
        updateButton.setForeground(Color.WHITE);
        deleteButton.setBackground(PRIMARY_COLOR);
        deleteButton.setForeground(Color.WHITE);
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(Color.WHITE);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Table
        String[] columnNames = {"ID", "Order ID", "Table Number", "Amount", "Payment Method", "Status", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentTable = new JTable(tableModel);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader header = paymentTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(paymentTable);

        // Add components to panel
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadOrderOptions() {
        try {
            orderHeaderComboBox.removeAllItems();
            List<OrderHeader> orders = orderHeaderService.findAll();
            for (OrderHeader order : orders) {
                orderHeaderComboBox.addItem(order);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<Payment> payments = paymentService.findAll();
            for (Payment payment : payments) {
                OrderHeader order = payment.getOrderHeader();
                tableModel.addRow(new Object[]{
                    payment.getId(),
                    order.getId(),
                    order.getDiningTable().getTableNumber(),
                    payment.getAmount(),
                    payment.getPaymentMethod(),
                    payment.getStatus(),
                    payment.getCreatedAt().format(DATE_TIME_FORMATTER)
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        // Table selection listener
        paymentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = paymentTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    try {
                        Payment payment = paymentService.findById(selectedId);
                        if (payment != null) {
                            orderHeaderComboBox.setSelectedItem(payment.getOrderHeader());
                            amountField.setText(String.valueOf(payment.getAmount()));
                            paymentMethodComboBox.setSelectedItem(payment.getPaymentMethod());
                            statusComboBox.setSelectedItem(payment.getStatus());
                            createdAtField.setText(payment.getCreatedAt().format(DATE_TIME_FORMATTER));
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Error loading payment details: " + ex.getMessage());
                    }
                }
            }
        });

        // Add button listener
        addButton.addActionListener(e -> {
            try {
                if (validateForm()) {
                    Payment payment = new Payment();
                    payment.setOrderHeader((OrderHeader) orderHeaderComboBox.getSelectedItem());
                    payment.setAmount(Double.parseDouble(amountField.getText()));
                    payment.setPaymentMethod((String) paymentMethodComboBox.getSelectedItem());
                    payment.setStatus((String) statusComboBox.getSelectedItem());
                    payment.setCreatedAt(LocalDateTime.now());

                    paymentService.create(payment);
                    JOptionPane.showMessageDialog(this, "Payment added successfully!");
                    resetForm();
                    loadTableData();
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Error adding payment: " + ex.getMessage());
            }
        });

        // Update button listener
        updateButton.addActionListener(e -> {
            try {
                if (selectedId != null && validateForm()) {
                    Payment payment = paymentService.findById(selectedId);
                    if (payment != null) {
                        payment.setOrderHeader((OrderHeader) orderHeaderComboBox.getSelectedItem());
                        payment.setAmount(Double.parseDouble(amountField.getText()));
                        payment.setPaymentMethod((String) paymentMethodComboBox.getSelectedItem());
                        payment.setStatus((String) statusComboBox.getSelectedItem());

                        paymentService.update(payment);
                        JOptionPane.showMessageDialog(this, "Payment updated successfully!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a payment to update");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Error updating payment: " + ex.getMessage());
            }
        });

        // Delete button listener
        deleteButton.addActionListener(e -> {
            try {
                if (selectedId != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this payment?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        paymentService.deleteById(selectedId);
                        JOptionPane.showMessageDialog(this, "Payment deleted successfully!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a payment to delete");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting payment: " + ex.getMessage());
            }
        });

        // Refresh button listener
        refreshButton.addActionListener(e -> {
            resetForm();
            loadTableData();
        });
    }

    private boolean validateForm() {
        if (orderHeaderComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select an order");
            return false;
        }

        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount");
            return false;
        }

        return true;
    }

    private void resetForm() {
        selectedId = null;
        orderHeaderComboBox.setSelectedIndex(0);
        amountField.setText("");
        paymentMethodComboBox.setSelectedIndex(0);
        statusComboBox.setSelectedIndex(0);
        createdAtField.setText("");
        paymentTable.clearSelection();
    }
} 