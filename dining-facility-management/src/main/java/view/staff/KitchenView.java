package view.staff;

import model.OrderHeader;
import model.OrderDetail;
import service.OrderHeaderService;
import service.OrderDetailService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class KitchenView extends JPanel {
    private final OrderHeaderService orderHeaderService;
    private final OrderDetailService orderDetailService;
    
    private JTable orderTable;
    private DefaultTableModel orderModel;
    private JComboBox<String> statusFilter;
    private JComboBox<String> tableFilter;
    private JButton updateStatusButton;

    public KitchenView(OrderHeaderService orderHeaderService, OrderDetailService orderDetailService) {
        this.orderHeaderService = orderHeaderService;
        this.orderDetailService = orderDetailService;
        initComponents();
        setupEventListeners();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusFilter = new JComboBox<>(new String[]{"All", "PENDING", "PROCESSING", "COMPLETED"});
        tableFilter = new JComboBox<>(new String[]{"All Tables"});
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("Table:"));
        filterPanel.add(tableFilter);
        add(filterPanel, BorderLayout.NORTH);

        // Order table
        String[] columnNames = {"Order ID", "Table", "Dish", "Quantity", "Status", "Time"};
        orderModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderModel);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        updateStatusButton = new JButton("Update Status");
        buttonPanel.add(updateStatusButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventListeners() {
        statusFilter.addActionListener(e -> loadData());
        tableFilter.addActionListener(e -> loadData());
        updateStatusButton.addActionListener(e -> updateSelectedOrderStatus());
    }

    private void loadData() {
        try {
            orderModel.setRowCount(0);
            String selectedStatus = (String) statusFilter.getSelectedItem();
            String selectedTable = (String) tableFilter.getSelectedItem();

            List<OrderHeader> orders = orderHeaderService.findAll();
            for (OrderHeader order : orders) {
                if ((selectedStatus.equals("All") || order.getStatus().equals(selectedStatus)) &&
                    (selectedTable.equals("All Tables") || Integer.toString(order.getDiningTable().getId()).equals(selectedTable))) {
                    List<OrderDetail> details = orderDetailService.findByOrderHeaderId(order.getId());
                    for (OrderDetail detail : details) {
                        orderModel.addRow(new Object[]{
                                order.getId(),
                                order.getDiningTable().getId(),
                                detail.getDish().getId(),
                                detail.getOrderQty(),
                                detail.getStatus(),
                                order.getOrderDate()
                        });
                    }
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void updateSelectedOrderStatus() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow >= 0) {
            String[] options = {"PENDING", "PROCESSING", "COMPLETED"};
            String newStatus = (String) JOptionPane.showInputDialog(
                    this,
                    "Select new status:",
                    "Update Status",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (newStatus != null) {
                try {
                    int orderId = (int) orderModel.getValueAt(selectedRow, 0);
                    int dishId = (int) orderModel.getValueAt(selectedRow, 2);
                    // TODO: Update order detail status
                    loadData();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error updating status: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order to update");
        }
    }
} 