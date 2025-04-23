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
import java.util.*;
import java.util.List;

public class FoodOrderingView extends JPanel {
    // UI Components
    private JComboBox<String> filterStatusComboBox;
    private JTable diningTableTable;
    private JButton selectTableButton;
    private JComboBox<Dish> dishComboBox;
    private JTextField quantityField;
    private JButton addDishButton;
    private JButton removeDishButton;
    private JTable orderTable;
    private JLabel totalLabel;
    private JButton checkIngredientButton;
    private JLabel ingredientStatusLabel;
    private JButton confirmOrderButton;

    // Data
    private DiningTable selectedTable;
    private List<OrderDetail> orderDetails = new ArrayList<>();
    private double totalAmount = 0.0;

    // Services
    private final DiningTableService diningTableService = new DiningTableServiceImpl();
    private final DishService dishService = new DishServiceImpl();
    private final OrderHeaderService orderHeaderService = new OrderHeaderServiceImpl();
    private final OrderDetailService orderDetailService = new OrderDetailServiceImpl();
    private final RecipeService recipeService = new RecipeServiceImpl();
    private final IngredientService ingredientService = new IngredientServiceImpl();

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

    public FoodOrderingView() throws RemoteException {
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

        // Center Panel - Order Table
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // West Panel - Dish Selection
        JPanel westPanel = createWestPanel();
        add(westPanel, BorderLayout.WEST);

        // South Panel - Actions
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
        filterStatusComboBox = new JComboBox<>(new String[]{"Tất cả", "Trống", "Đang sử dụng", "Đã đặt trước"});
        filterStatusComboBox.setFont(MAIN_FONT);
        filterStatusComboBox.setPreferredSize(new Dimension(200, INPUT_HEIGHT));

        filterPanel.add(filterLabel);
        filterPanel.add(filterStatusComboBox);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(BACKGROUND_COLOR);

        String[] columnNames = {"Số bàn", "Trạng thái", "Sức chứa"};
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

    private JPanel createWestPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Chọn món",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                HEADER_FONT,
                PRIMARY_COLOR
        ));

        // Dish Selection
        JLabel dishLabel = new JLabel("Món ăn:");
        dishLabel.setFont(MAIN_FONT);
        dishComboBox = new JComboBox<>();
        dishComboBox.setFont(MAIN_FONT);
        dishComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_HEIGHT));

        // Quantity
        JLabel quantityLabel = new JLabel("Số lượng:");
        quantityLabel.setFont(MAIN_FONT);
        quantityField = new JTextField();
        quantityField.setFont(MAIN_FONT);
        quantityField.setMaximumSize(new Dimension(Integer.MAX_VALUE, INPUT_HEIGHT));

        // Buttons
        addDishButton = createStyledButton("Thêm món", BUTTON_WIDTH, BUTTON_HEIGHT);
        removeDishButton = createStyledButton("Xóa món", BUTTON_WIDTH, BUTTON_HEIGHT);

        // Add components with spacing
        panel.add(Box.createVerticalStrut(10));
        panel.add(dishLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(dishComboBox);
        panel.add(Box.createVerticalStrut(10));
        panel.add(quantityLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(quantityField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(addDishButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(removeDishButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR),
                "Đơn hàng",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                HEADER_FONT,
                PRIMARY_COLOR
        ));

        // Order Table
        String[] columnNames = {"Tên món", "Số lượng", "Đơn giá", "Thành tiền"};
        orderTable = new JTable(new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        orderTable.setFont(MAIN_FONT);
        orderTable.setRowHeight(TABLE_ROW_HEIGHT);
        orderTable.getTableHeader().setFont(HEADER_FONT);
        orderTable.getTableHeader().setBackground(HEADER_COLOR);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane tableScrollPane = new JScrollPane(orderTable);
        tableScrollPane.setBorder(new LineBorder(Color.LIGHT_GRAY));

        // Total Label
        totalLabel = new JLabel("Tổng tiền: 0 VNĐ");
        totalLabel.setFont(HEADER_FONT);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(totalLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSouthPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);

        checkIngredientButton = createStyledButton("Kiểm tra nguyên liệu", BUTTON_WIDTH, BUTTON_HEIGHT);
        confirmOrderButton = createStyledButton("Xác nhận đơn hàng", BUTTON_WIDTH, BUTTON_HEIGHT);

        ingredientStatusLabel = new JLabel(" ");
        ingredientStatusLabel.setFont(MAIN_FONT);
        ingredientStatusLabel.setForeground(Color.RED);

        panel.add(checkIngredientButton);
        panel.add(confirmOrderButton);
        panel.add(ingredientStatusLabel);

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
                        table.getSeatingCapacity()
                });
            }

            // Load dishes
            List<Dish> dishes = dishService.findAll();
            dishComboBox.removeAllItems();
            for (Dish dish : dishes) {
                dishComboBox.addItem(dish);
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
                                table.getSeatingCapacity()
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
                String tableNumber = String.valueOf(diningTableTable.getValueAt(selectedRow, 0));
                List<DiningTable> tables = diningTableService.findAll();
                for (DiningTable table : tables) {
                    if (String.valueOf(table.getTableNumber()).equals(tableNumber)) {
                        selectedTable = table;
                        break;
                    }
                }

                if (selectedTable != null) {
                    JOptionPane.showMessageDialog(this,
                            "Đã chọn bàn " + selectedTable.getTableNumber(),
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi chọn bàn: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add dish
        addDishButton.addActionListener(e -> {
            if (selectedTable == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn bàn trước khi thêm món",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Dish selectedDish = (Dish) dishComboBox.getSelectedItem();
            if (selectedDish == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn món ăn",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Số lượng phải lớn hơn 0",
                            "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Add to order details
                OrderDetail detail = new OrderDetail();
                detail.setDish(selectedDish);
                detail.setOrderQty(quantity);
                detail.setPrice(selectedDish.getUnitPrice());
                orderDetails.add(detail);

                // Update table
                DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
                model.addRow(new Object[]{
                        selectedDish.getName(),
                        quantity,
                        selectedDish.getUnitPrice(),
                        quantity * selectedDish.getUnitPrice()
                });

                // Update total
                totalAmount += quantity * selectedDish.getUnitPrice();
                totalLabel.setText(String.format("Tổng tiền: %.0f VNĐ", totalAmount));

                // Clear quantity field
                quantityField.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng không hợp lệ",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Remove dish
        removeDishButton.addActionListener(e -> {
            int selectedRow = orderTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn món cần xóa",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Remove from order details
            orderDetails.remove(selectedRow);

            // Update table
            DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
            double removedAmount = (double) model.getValueAt(selectedRow, 3);
            totalAmount -= removedAmount;
            model.removeRow(selectedRow);

            // Update total
            totalLabel.setText(String.format("Tổng tiền: %.0f VNĐ", totalAmount));
        });

        // Check ingredients
        checkIngredientButton.addActionListener(e -> {
            if (orderDetails.isEmpty()) {
                ingredientStatusLabel.setText("Chưa có món nào trong đơn hàng");
                return;
            }

            try {
                boolean hasEnoughIngredients = true;
                StringBuilder missingIngredients = new StringBuilder();

                for (OrderDetail detail : orderDetails) {
                    List<Recipe> recipes = recipeService.findByDishId(detail.getDish().getId());
                    for (Recipe recipe : recipes) {
                        IngredientModel ingredientModel = recipe.getIngredientModel();
                        Ingredient ingredient = ingredientService.getTopIngredientByModelIdOrderByStockQuantity(ingredientModel.getId());

                        double requiredQuantity = recipe.getRequiredQuantity() * detail.getOrderQty();
                        double availableQuantity = ingredient != null ? ingredient.getStockQuantity() : 0;

                        if (availableQuantity < requiredQuantity) {
                            hasEnoughIngredients = false;
                            missingIngredients.append(String.format(
                                    "\n- %s: Cần %.2f, Còn %.2f",
                                    ingredientModel.getName(),
                                    requiredQuantity,
                                    availableQuantity
                            ));
                        }
                    }
                }

                if (hasEnoughIngredients) {
                    ingredientStatusLabel.setText("Đủ nguyên liệu để chế biến");
                    ingredientStatusLabel.setForeground(new Color(0, 150, 0));
                } else {
                    ingredientStatusLabel.setText("Thiếu nguyên liệu:" + missingIngredients.toString());
                    ingredientStatusLabel.setForeground(Color.RED);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi kiểm tra nguyên liệu: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Confirm order
        confirmOrderButton.addActionListener(e -> {
            if (selectedTable == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn bàn trước khi xác nhận đơn hàng",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (orderDetails.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng thêm món vào đơn hàng",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Check ingredients again
                boolean hasEnoughIngredients = true;
                for (OrderDetail detail : orderDetails) {
                    List<Recipe> recipes = recipeService.findByDishId(detail.getDish().getId());
                    for (Recipe recipe : recipes) {
                        IngredientModel ingredientModel = recipe.getIngredientModel();
                        Ingredient ingredient = ingredientService.getTopIngredientByModelIdOrderByStockQuantity(ingredientModel.getId());

                        double requiredQuantity = recipe.getRequiredQuantity() * detail.getOrderQty();
                        double availableQuantity = ingredient != null ? ingredient.getStockQuantity() : 0;

                        if (availableQuantity < requiredQuantity) {
                            hasEnoughIngredients = false;
                            break;
                        }
                    }
                    if (!hasEnoughIngredients) break;
                }

                if (!hasEnoughIngredients) {
                    JOptionPane.showMessageDialog(this,
                            "Không đủ nguyên liệu để chế biến. Vui lòng kiểm tra lại.",
                            "Thông báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Create order header
                OrderHeader orderHeader = new OrderHeader();
                orderHeader.setDiningTable(selectedTable);
                orderHeader.setOrderDate(LocalDateTime.now());
                orderHeader.setStatus("Đang sử dụng");
                orderHeader.setSubTotal(totalAmount);
                orderHeader.setOrderDetails(orderDetails);

                // Save order
                orderHeaderService.create(orderHeader);

                // Update table status
                selectedTable.setStatus("Đang sử dụng");
                diningTableService.update(selectedTable);

                // Clear form
                orderDetails.clear();
                ((DefaultTableModel) orderTable.getModel()).setRowCount(0);
                totalAmount = 0;
                totalLabel.setText("Tổng tiền: 0 VNĐ");
                ingredientStatusLabel.setText(" ");
                selectedTable = null;

                JOptionPane.showMessageDialog(this,
                        "Đã tạo đơn hàng thành công",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);

                // Reload data
                loadData();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi tạo đơn hàng: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
} 