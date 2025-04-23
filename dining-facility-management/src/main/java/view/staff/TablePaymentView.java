package view.staff;

import model.*;
import service.*;
import service.impl.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class TablePaymentView extends JPanel {
    // UI Components
    private JComboBox<String> filterStatusComboBox;
    private JTable diningTableTable;
    private JButton selectTableButton;
    private JTable orderDetailTable;
    private JLabel totalAmountLabel;
    private JLabel orderStatusLabel;
    private JComboBox<String> paymentMethodComboBox;
    private JButton confirmPaymentButton;
    private JButton exportInvoiceButton;

    // Data
    private DiningTable selectedTable;
    private OrderHeader currentOrder;
    private List<OrderDetail> orderDetails = new ArrayList<>();
    private double totalAmount = 0.0;

    // Services
    private final DiningTableService diningTableService = new DiningTableServiceImpl();
    private final OrderHeaderService orderHeaderService = new OrderHeaderServiceImpl();
    private final OrderDetailService orderDetailService = new OrderDetailServiceImpl();
    private final PaymentService paymentService = new PaymentServiceImpl();

    // UI Constants
    private static final Color PRIMARY_COLOR = new Color(30, 144, 255); // Dodger Blue
    private static final Color HOVER_COLOR = new Color(0, 191, 255); // Deep Sky Blue
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color HEADER_COLOR = new Color(240, 240, 240);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 40;
    private static final int INPUT_HEIGHT = 40;
    private static final int TABLE_ROW_HEIGHT = 35;

    public TablePaymentView() throws RemoteException {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        loadData();
        setupListeners();
    }

    private void initComponents() {
        // North Panel - Filter and Table Selection
        JPanel northPanel = createNorthPanel();
        add(northPanel, BorderLayout.NORTH);

        // Center Panel - Order Details
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // South Panel - Payment Actions
        JPanel southPanel = createSouthPanel();
        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel createNorthPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel filterLabel = new JLabel("Lọc theo trạng thái:");
        filterLabel.setFont(MAIN_FONT);
        filterStatusComboBox = new JComboBox<>(new String[]{"Tất cả", "Đang sử dụng", "Đã đặt trước", "Trống"});
        filterStatusComboBox.setFont(MAIN_FONT);
        filterStatusComboBox.setPreferredSize(new Dimension(200, INPUT_HEIGHT));
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterStatusComboBox);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(BACKGROUND_COLOR);

        String[] columnNames = {"Số bàn", "Trạng thái", "Sức chứa", "Vị trí"};
        diningTableTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        diningTableTable.setFont(MAIN_FONT);
        diningTableTable.setRowHeight(TABLE_ROW_HEIGHT);
        diningTableTable.getTableHeader().setFont(HEADER_FONT);
        diningTableTable.getTableHeader().setBackground(HEADER_COLOR);
        diningTableTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(diningTableTable);
        tableScrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY));

        selectTableButton = createStyledButton("Chọn bàn", BUTTON_WIDTH, BUTTON_HEIGHT);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(selectTableButton);

        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Thông tin đơn hàng",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            HEADER_FONT,
            PRIMARY_COLOR
        ));

        // Order Details Table
        String[] columnNames = {"Tên món", "Số lượng", "Đơn giá", "Thành tiền"};
        orderDetailTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        orderDetailTable.setFont(MAIN_FONT);
        orderDetailTable.setRowHeight(TABLE_ROW_HEIGHT);
        orderDetailTable.getTableHeader().setFont(HEADER_FONT);
        orderDetailTable.getTableHeader().setBackground(HEADER_COLOR);
        orderDetailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(orderDetailTable);
        tableScrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY));

        // Order Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        infoPanel.setBackground(BACKGROUND_COLOR);

        // Total Amount Label
        totalAmountLabel = new JLabel("Tổng tiền: 0 VNĐ");
        totalAmountLabel.setFont(HEADER_FONT);
        totalAmountLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // Order Status Label
        orderStatusLabel = new JLabel("Trạng thái đơn hàng: Chưa chọn bàn");
        orderStatusLabel.setFont(MAIN_FONT);
        orderStatusLabel.setForeground(Color.GRAY);

        infoPanel.add(totalAmountLabel);
        infoPanel.add(orderStatusLabel);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSouthPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR),
            "Thanh toán",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            HEADER_FONT,
            PRIMARY_COLOR
        ));

        // Payment Method Panel
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel methodLabel = new JLabel("Phương thức thanh toán:");
        methodLabel.setFont(MAIN_FONT);
        paymentMethodComboBox = new JComboBox<>(new String[]{"Tiền mặt", "Ví điện tử"});
        paymentMethodComboBox.setFont(MAIN_FONT);
        paymentMethodComboBox.setPreferredSize(new Dimension(200, INPUT_HEIGHT));
        
        methodPanel.add(methodLabel);
        methodPanel.add(paymentMethodComboBox);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        confirmPaymentButton = createStyledButton("Xác nhận thanh toán", BUTTON_WIDTH, BUTTON_HEIGHT);
        exportInvoiceButton = createStyledButton("Xuất hóa đơn", BUTTON_WIDTH, BUTTON_HEIGHT);

        buttonPanel.add(confirmPaymentButton);
        buttonPanel.add(exportInvoiceButton);

        panel.add(methodPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private void loadData() {
        try {
            // Load dining tables
            List<DiningTable> tables = diningTableService.findAll();
            DefaultTableModel tableModel = (DefaultTableModel) diningTableTable.getModel();
            tableModel.setRowCount(0);
            
            for (DiningTable table : tables) {
                tableModel.addRow(new Object[]{
                    table.getTableNumber(),
                    table.getStatus(),
                    table.getSeatingCapacity(),
                    table.getLocation()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupListeners() {
        // Filter status change
        filterStatusComboBox.addActionListener(e -> {
            String selectedStatus = (String) filterStatusComboBox.getSelectedItem();
            DefaultTableModel model = (DefaultTableModel) diningTableTable.getModel();
            model.setRowCount(0);

            try {
                List<DiningTable> tables = diningTableService.findAll();
                for (DiningTable table : tables) {
                    if (selectedStatus.equals("Tất cả") || 
                        table.getStatus().equals(selectedStatus)) {
                        model.addRow(new Object[]{
                            table.getTableNumber(),
                            table.getStatus(),
                            table.getSeatingCapacity(),
                            table.getLocation()
                        });
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi lọc bàn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Select table
        selectTableButton.addActionListener(e -> {
            int selectedRow = diningTableTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một bàn",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String tableNumber = (String) diningTableTable.getValueAt(selectedRow, 0);
                List<DiningTable> tables = diningTableService.findAll();
                for (DiningTable table : tables) {
                    if (String.valueOf(table.getTableNumber()).equals(tableNumber)) {
                        selectedTable = table;
                        break;
                    }
                }

                if (selectedTable != null) {
                    // Find active order for this table
                    List<OrderHeader> orders = orderHeaderService.findByTableId(selectedTable.getId());
                    currentOrder = null;
                    
                    for (OrderHeader order : orders) {
                        if (order.getStatus().equals("Đang sử dụng")) {
                            currentOrder = order;
                            break;
                        }
                    }

                    if (currentOrder != null) {
                        // Load order details
                        orderDetails = orderDetailService.findByOrderHeaderId(currentOrder.getId());
                        
                        // Update order detail table
                        DefaultTableModel model = (DefaultTableModel) orderDetailTable.getModel();
                        model.setRowCount(0);
                        
                        totalAmount = 0.0;
                        for (OrderDetail detail : orderDetails) {
                            model.addRow(new Object[]{
                                detail.getDish().getName(),
                                detail.getOrderQty(),
                                detail.getPrice(),
                                detail.getOrderQty() * detail.getPrice()
                            });
                            totalAmount += detail.getOrderQty() * detail.getPrice();
                        }
                        
                        // Update labels
                        totalAmountLabel.setText(String.format("Tổng tiền: %.0f VNĐ", totalAmount));
                        orderStatusLabel.setText("Trạng thái đơn hàng: " + currentOrder.getStatus());
                        
                        JOptionPane.showMessageDialog(this,
                            "Đã tải thông tin đơn hàng của bàn " + selectedTable.getTableNumber(),
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Bàn " + selectedTable.getTableNumber() + " không có đơn hàng đang sử dụng",
                            "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                        
                        // Clear order details
                        orderDetails.clear();
                        ((DefaultTableModel) orderDetailTable.getModel()).setRowCount(0);
                        totalAmount = 0.0;
                        totalAmountLabel.setText("Tổng tiền: 0 VNĐ");
                        orderStatusLabel.setText("Trạng thái đơn hàng: Không có đơn hàng");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi chọn bàn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Confirm payment
        confirmPaymentButton.addActionListener(e -> {
            if (selectedTable == null) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn bàn trước khi thanh toán",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (currentOrder == null) {
                JOptionPane.showMessageDialog(this,
                    "Không có đơn hàng nào để thanh toán",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Create payment
                Payment payment = new Payment();
                payment.setOrderHeader(currentOrder);
                payment.setCreatedAt(LocalDateTime.now());
                payment.setAmount(totalAmount);
                payment.setPaymentMethod((String) paymentMethodComboBox.getSelectedItem());
                payment.setStatus("Đã thanh toán");

                // Save payment
                paymentService.create(payment);

                // Update order status
                currentOrder.setStatus("Đã thanh toán");
                orderHeaderService.update(currentOrder);

                // Update table status
                selectedTable.setStatus("Trống");
                diningTableService.update(selectedTable);

                // Clear form
                selectedTable = null;
                currentOrder = null;
                orderDetails.clear();
                ((DefaultTableModel) orderDetailTable.getModel()).setRowCount(0);
                totalAmount = 0.0;
                totalAmountLabel.setText("Tổng tiền: 0 VNĐ");
                orderStatusLabel.setText("Trạng thái đơn hàng: Chưa chọn bàn");

                JOptionPane.showMessageDialog(this,
                    "Đã thanh toán thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

                // Reload data
                loadData();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi thanh toán: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Export invoice
        exportInvoiceButton.addActionListener(e -> {
            if (currentOrder == null) {
                JOptionPane.showMessageDialog(this,
                    "Không có đơn hàng nào để xuất hóa đơn",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Create invoice content
                StringBuilder invoiceContent = new StringBuilder();
                invoiceContent.append("HÓA ĐƠN THANH TOÁN\n");
                invoiceContent.append("--------------------------------\n");
                invoiceContent.append("Số bàn: ").append(selectedTable.getTableNumber()).append("\n");
                invoiceContent.append("Ngày: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
                invoiceContent.append("--------------------------------\n");
                invoiceContent.append("STT | Tên món | Số lượng | Đơn giá | Thành tiền\n");
                invoiceContent.append("--------------------------------\n");

                int stt = 1;
                for (OrderDetail detail : orderDetails) {
                    invoiceContent.append(String.format("%d | %s | %d | %.0f | %.0f\n",
                        stt++,
                        detail.getDish().getName(),
                        detail.getOrderQty(),
                        detail.getPrice(),
                        detail.getOrderQty() * detail.getPrice()
                    ));
                }

                invoiceContent.append("--------------------------------\n");
                invoiceContent.append(String.format("Tổng tiền: %.0f VNĐ\n", totalAmount));
                invoiceContent.append("Phương thức thanh toán: ").append(paymentMethodComboBox.getSelectedItem()).append("\n");
                invoiceContent.append("--------------------------------\n");
                invoiceContent.append("Cảm ơn quý khách đã ghé thăm!\n");

                // Show invoice in dialog
                JTextArea textArea = new JTextArea(invoiceContent.toString());
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                textArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(500, 400));

                JOptionPane.showMessageDialog(this,
                    scrollPane,
                    "Hóa đơn thanh toán",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất hóa đơn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
} 