package view.staff;

import model.OrderHeader;
import model.Payment;
import service.OrderHeaderService;
import service.PaymentService;

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
import java.util.List;

public class PaymentView extends JPanel {
    private final PaymentService paymentService;
    private final OrderHeaderService orderHeaderService;
    
    private JTable paymentsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton processButton;
    private JButton refundButton;
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

    public PaymentView(PaymentService paymentService, OrderHeaderService orderHeaderService) {
        this.paymentService = paymentService;
        this.orderHeaderService = orderHeaderService;
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
        searchField.setToolTipText("Search payments by order ID or amount");
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

        processButton = createStyledButton("💳 Process", "Process new payment");
        refundButton = createStyledButton("↩️ Refund", "Refund selected payment");
        refreshButton = createStyledButton("🔄 Refresh", "Refresh payment list");
        viewDetailsButton = createStyledButton("📋 Details", "View payment details");

        buttonPanel.add(processButton);
        buttonPanel.add(refundButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewDetailsButton);

        topPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Order ID", "Amount", "Payment Method", "Status", "Created At"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentsTable = new JTable(tableModel);
        paymentsTable.setFillsViewportHeight(true);
        paymentsTable.setRowHeight(25);
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentsTable.setGridColor(Color.LIGHT_GRAY);
        paymentsTable.setShowGrid(true);
        paymentsTable.setBackground(Color.WHITE);
        paymentsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = paymentsTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(paymentsTable);
        tableScrollPane.setBorder(new TitledBorder("Payments List"));
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
        processButton.addActionListener(e -> showProcessPaymentDialog());
        refundButton.addActionListener(e -> processRefund());
        refreshButton.addActionListener(e -> loadData());
        viewDetailsButton.addActionListener(e -> viewPaymentDetails());
        searchField.addActionListener(e -> searchPayments());
    }

    private void loadData() {
        try {
            tableModel.setRowCount(0);
            List<Payment> payments = paymentService.findAll();
            for (Payment payment : payments) {
                tableModel.addRow(new Object[]{
                    payment.getId(),
                    payment.getOrderHeader().getId(),
                    String.format("$%.2f", payment.getAmount()),
                    payment.getPaymentMethod(),
                    payment.getStatus(),
                    payment.getCreatedAt().format(DATE_FORMATTER)
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading payments: " + e.getMessage());
        }
    }

    private void showProcessPaymentDialog() {
        try {
            List<OrderHeader> pendingOrders = orderHeaderService.findPendingOrders();
            if (pendingOrders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No pending orders available for payment");
                return;
            }

            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            JComboBox<OrderHeader> orderCombo = new JComboBox<>(pendingOrders.toArray(new OrderHeader[0]));
            JTextField amountField = new JTextField();
            JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card"});

            panel.add(new JLabel("Order:"));
            panel.add(orderCombo);
            panel.add(new JLabel("Amount:"));
            panel.add(amountField);
            panel.add(new JLabel("Payment Method:"));
            panel.add(paymentMethodCombo);

            int result = JOptionPane.showConfirmDialog(this, panel, "Process Payment",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    OrderHeader selectedOrder = (OrderHeader) orderCombo.getSelectedItem();
                    Payment payment = new Payment();
                    payment.setOrderHeader(selectedOrder);
                    payment.setAmount(Double.parseDouble(amountField.getText()));
                    payment.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
                    payment.setStatus("Completed");
                    payment.setCreatedAt(LocalDateTime.now());

                    paymentService.create(payment);
                    
                    // Update order status
                    selectedOrder.setStatus("COMPLETED");
                    orderHeaderService.update(selectedOrder);
                    
                    loadData();
                    JOptionPane.showMessageDialog(this, "Payment processed successfully!");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error processing payment: " + e.getMessage());
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }

    private void processRefund() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
                Payment payment = paymentService.findById(paymentId);
                
                int result = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to refund this payment?",
                        "Confirm Refund",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    payment.setStatus("Refunded");
                    paymentService.update(payment);
                    
                    // Update order status
                    OrderHeader order = payment.getOrderHeader();
                    order.setStatus("REFUNDED");
                    orderHeaderService.update(order);
                    
                    loadData();
                    JOptionPane.showMessageDialog(this, "Payment refunded successfully!");
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error processing refund: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a payment to refund");
        }
    }

    private void viewPaymentDetails() {
        int selectedRow = paymentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            try {
                int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
                Payment payment = paymentService.findById(paymentId);
                OrderHeader order = payment.getOrderHeader();
                
                JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
                panel.setBorder(new EmptyBorder(10, 10, 10, 10));

                panel.add(new JLabel("Payment ID:"));
                panel.add(new JLabel(String.valueOf(payment.getId())));
                panel.add(new JLabel("Order ID:"));
                panel.add(new JLabel(String.valueOf(order.getId())));
                panel.add(new JLabel("Table Number:"));
                panel.add(new JLabel(String.valueOf(order.getDiningTable().getTableNumber())));
                panel.add(new JLabel("Amount:"));
                panel.add(new JLabel(String.format("$%.2f", payment.getAmount())));
                panel.add(new JLabel("Payment Method:"));
                panel.add(new JLabel(payment.getPaymentMethod()));
                panel.add(new JLabel("Status:"));
                panel.add(new JLabel(payment.getStatus()));
                panel.add(new JLabel("Created At:"));
                panel.add(new JLabel(payment.getCreatedAt().format(DATE_FORMATTER)));
                panel.add(new JLabel("Order Status:"));
                panel.add(new JLabel(order.getStatus()));

                JOptionPane.showMessageDialog(this, panel, "Payment Details",
                        JOptionPane.PLAIN_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Error viewing payment details: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a payment to view details");
        }
    }

    private void searchPayments() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        try {
            List<Payment> payments = paymentService.findAll();
            for (Payment payment : payments) {
                if (String.valueOf(payment.getId()).contains(searchText) ||
                    String.valueOf(payment.getOrderHeader().getId()).contains(searchText) ||
                    String.format("$%.2f", payment.getAmount()).contains(searchText)) {
                    tableModel.addRow(new Object[]{
                        payment.getId(),
                        payment.getOrderHeader().getId(),
                        String.format("$%.2f", payment.getAmount()),
                        payment.getPaymentMethod(),
                        payment.getStatus(),
                        payment.getCreatedAt().format(DATE_FORMATTER)
                    });
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error searching payments: " + e.getMessage());
        }
    }
} 