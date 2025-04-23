package view.admin;

import model.DiningTable;
import service.DiningTableService;
import service.impl.DiningTableServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.rmi.RemoteException;

public class AdminViewRunner extends JFrame {
    private JTabbedPane tabbedPane;
    private JMenuBar menuBar;
    
    // UI Constants
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    
    // Font sizes
    private static final int TITLE_FONT_SIZE = 24;
    private static final int MENU_FONT_SIZE = 16;
    private static final int TAB_FONT_SIZE = 16;
    private static final int LABEL_FONT_SIZE = 16;
    private static final int INPUT_FONT_SIZE = 16;
    private static final int BUTTON_FONT_SIZE = 16;
    private static final int TABLE_HEADER_FONT_SIZE = 18;
    private static final int TABLE_ROW_FONT_SIZE = 16;
    
    // Fonts
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, TITLE_FONT_SIZE);
    private static final Font MENU_FONT = new Font("Arial", Font.PLAIN, MENU_FONT_SIZE);
    private static final Font TAB_FONT = new Font("Arial", Font.PLAIN, TAB_FONT_SIZE);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, LABEL_FONT_SIZE);
    private static final Font INPUT_FONT = new Font("Arial", Font.PLAIN, INPUT_FONT_SIZE);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, BUTTON_FONT_SIZE);
    private static final Font TABLE_HEADER_FONT = new Font("Arial", Font.BOLD, TABLE_HEADER_FONT_SIZE);
    private static final Font TABLE_ROW_FONT = new Font("Arial", Font.PLAIN, TABLE_ROW_FONT_SIZE);
    
    // Dimensions
    private static final int PADDING = 15;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 40;
    private static final int INPUT_HEIGHT = 40;
    private static final int TABLE_ROW_HEIGHT = 35;
    
    // Borders
    private static final Border PADDING_BORDER = new EmptyBorder(PADDING, PADDING, PADDING, PADDING);
    private static final Border INPUT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 10, 5, 10)
    );

    public AdminViewRunner() {
        setTitle("Quản lý nhà hàng - Giao diện Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);

        initMenuBar();
        initComponents();
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY_COLOR);
        
        // Menu File
        JMenu fileMenu = new JMenu("Tệp");
        fileMenu.setForeground(Color.WHITE);
        fileMenu.setFont(MENU_FONT);
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.setFont(MENU_FONT);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Menu View
        JMenu viewMenu = new JMenu("Xem");
        viewMenu.setForeground(Color.WHITE);
        viewMenu.setFont(MENU_FONT);
        JMenuItem refreshItem = new JMenuItem("Làm mới");
        refreshItem.setFont(MENU_FONT);
        refreshItem.addActionListener(e -> refreshCurrentView());
        viewMenu.add(refreshItem);
        
        // Menu Help
        JMenu helpMenu = new JMenu("Trợ giúp");
        helpMenu.setForeground(Color.WHITE);
        helpMenu.setFont(MENU_FONT);
        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
        aboutItem.setFont(MENU_FONT);
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void refreshCurrentView() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        if (selectedIndex >= 0) {
            try {
                // Lưu lại tab hiện tại
                String currentTabName = tabbedPane.getTitleAt(selectedIndex);
                
                // Xóa tab hiện tại
                tabbedPane.removeTabAt(selectedIndex);
                
                // Tạo lại tab với cùng tên
                JPanel newPanel = createPanelForTab(currentTabName);
                tabbedPane.insertTab(currentTabName, null, newPanel, null, selectedIndex);
                
                // Chọn lại tab đã làm mới
                tabbedPane.setSelectedIndex(selectedIndex);
                
                JOptionPane.showMessageDialog(this, 
                    "Đã làm mới thành công!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi làm mới: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private JPanel createPanelForTab(String tabName) throws RemoteException {
        JPanel panel;
        switch (tabName) {
            case "Quản lý bàn": panel = new DiningTableAdminView(); break;
            case "Quản lý món ăn": panel = new DishAdminView(); break;
            case "Quản lý lịch sử giá món": panel = new DishPriceHistoryAdminView(); break;
            case "Quản lý nguyên liệu": panel = new IngredientAdminView(); break;
            case "Quản lý loại nguyên liệu": panel = new IngredientModelAdminView(); break;
            case "Quản lý lô nguyên liệu": panel = new IngredientBatchAdminView(); break;
            case "Quản lý công thức": panel = new RecipeAdminView(); break;
            case "Quản lý đơn hàng": panel = new OrderHeaderAdminView(); break;
            case "Quản lý chi tiết đơn hàng": panel = new OrderDetailAdminView(); break;
            case "Quản lý đơn mua hàng": panel = new PurchaseOrderHeaderAdminView(); break;
            case "Quản lý chi tiết đơn mua": panel = new PurchaseOrderDetailAdminView(); break;
            case "Quản lý thanh toán": panel = new PaymentAdminView(); break;
            case "Quản lý báo cáo": panel = new ReportAdminView(); break;
            case "Quản lý nhà cung cấp": panel = new VendorAdminView(); break;
            default: throw new IllegalArgumentException("Không tìm thấy tab: " + tabName);
        }
        
        // Áp dụng style cho panel
        applyPanelStyle(panel);
        return panel;
    }
    
    private void applyPanelStyle(JPanel panel) {
        // Tìm tất cả các nút trong panel và áp dụng style
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
                button.setFont(BUTTON_FONT);
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(Color.WHITE);
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                
                // Chuyển text sang tiếng Việt
                String buttonText = button.getText().toLowerCase();
                switch (buttonText) {
                    case "add": button.setText("Thêm"); break;
                    case "update": button.setText("Cập nhật"); break;
                    case "delete": button.setText("Xóa"); break;
                    case "refresh": button.setText("Làm mới"); break;
                    case "clear": button.setText("Xóa trắng"); break;
                }
            } else if (comp instanceof JTextField) {
                JTextField textField = (JTextField) comp;
                textField.setFont(INPUT_FONT);
                textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, INPUT_HEIGHT));
                textField.setBorder(INPUT_BORDER);
            } else if (comp instanceof JComboBox) {
                JComboBox<?> comboBox = (JComboBox<?>) comp;
                comboBox.setFont(INPUT_FONT);
                comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, INPUT_HEIGHT));
                comboBox.setBorder(INPUT_BORDER);
            } else if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                label.setFont(LABEL_FONT);
                
                // Chuyển text sang tiếng Việt
                String labelText = label.getText().toLowerCase();
                switch (labelText) {
                    case "id": label.setText("Mã"); break;
                    case "name": label.setText("Tên"); break;
                    case "description": label.setText("Mô tả"); break;
                    case "price": label.setText("Giá"); break;
                    case "status": label.setText("Trạng thái"); break;
                    case "date": label.setText("Ngày"); break;
                    case "time": label.setText("Thời gian"); break;
                    case "quantity": label.setText("Số lượng"); break;
                    case "unit": label.setText("Đơn vị"); break;
                    case "type": label.setText("Loại"); break;
                    case "active": label.setText("Hoạt động"); break;
                    case "priority": label.setText("Ưu tiên"); break;
                    case "address": label.setText("Địa chỉ"); break;
                    case "phone": label.setText("Số điện thoại"); break;
                    case "email": label.setText("Email"); break;
                    case "total": label.setText("Tổng tiền"); break;
                    case "payment method": label.setText("Phương thức thanh toán"); break;
                    case "payment status": label.setText("Trạng thái thanh toán"); break;
                    case "order date": label.setText("Ngày đặt"); break;
                    case "ship date": label.setText("Ngày giao"); break;
                    case "expiry date": label.setText("Ngày hết hạn"); break;
                    case "safety stock": label.setText("Tồn kho an toàn"); break;
                    case "reorder point": label.setText("Điểm đặt hàng"); break;
                    case "stock quantity": label.setText("Số lượng tồn"); break;
                    case "received date": label.setText("Ngày nhận"); break;
                    case "vendor": label.setText("Nhà cung cấp"); break;
                    case "ingredient": label.setText("Nguyên liệu"); break;
                    case "dish": label.setText("Món ăn"); break;
                    case "recipe": label.setText("Công thức"); break;
                    case "table": label.setText("Bàn"); break;
                    case "capacity": label.setText("Sức chứa"); break;
                    case "location": label.setText("Vị trí"); break;
                }
            } else if (comp instanceof JTable) {
                JTable table = (JTable) comp;
                table.setFont(TABLE_ROW_FONT);
                table.setRowHeight(TABLE_ROW_HEIGHT);
                table.getTableHeader().setFont(TABLE_HEADER_FONT);
                table.getTableHeader().setBackground(HEADER_COLOR);
                table.getTableHeader().setForeground(Color.BLACK);
                
                // Chuyển tên cột sang tiếng Việt
                for (int i = 0; i < table.getColumnCount(); i++) {
                    String columnName = table.getColumnName(i).toLowerCase();
                    switch (columnName) {
                        case "id": table.getColumnModel().getColumn(i).setHeaderValue("Mã"); break;
                        case "name": table.getColumnModel().getColumn(i).setHeaderValue("Tên"); break;
                        case "description": table.getColumnModel().getColumn(i).setHeaderValue("Mô tả"); break;
                        case "price": table.getColumnModel().getColumn(i).setHeaderValue("Giá"); break;
                        case "status": table.getColumnModel().getColumn(i).setHeaderValue("Trạng thái"); break;
                        case "date": table.getColumnModel().getColumn(i).setHeaderValue("Ngày"); break;
                        case "time": table.getColumnModel().getColumn(i).setHeaderValue("Thời gian"); break;
                        case "quantity": table.getColumnModel().getColumn(i).setHeaderValue("Số lượng"); break;
                        case "unit": table.getColumnModel().getColumn(i).setHeaderValue("Đơn vị"); break;
                        case "type": table.getColumnModel().getColumn(i).setHeaderValue("Loại"); break;
                        case "active": table.getColumnModel().getColumn(i).setHeaderValue("Hoạt động"); break;
                        case "priority": table.getColumnModel().getColumn(i).setHeaderValue("Ưu tiên"); break;
                        case "address": table.getColumnModel().getColumn(i).setHeaderValue("Địa chỉ"); break;
                        case "phone": table.getColumnModel().getColumn(i).setHeaderValue("Số điện thoại"); break;
                        case "email": table.getColumnModel().getColumn(i).setHeaderValue("Email"); break;
                        case "total": table.getColumnModel().getColumn(i).setHeaderValue("Tổng tiền"); break;
                        case "payment method": table.getColumnModel().getColumn(i).setHeaderValue("Phương thức thanh toán"); break;
                        case "payment status": table.getColumnModel().getColumn(i).setHeaderValue("Trạng thái thanh toán"); break;
                        case "order date": table.getColumnModel().getColumn(i).setHeaderValue("Ngày đặt"); break;
                        case "ship date": table.getColumnModel().getColumn(i).setHeaderValue("Ngày giao"); break;
                        case "expiry date": table.getColumnModel().getColumn(i).setHeaderValue("Ngày hết hạn"); break;
                        case "safety stock": table.getColumnModel().getColumn(i).setHeaderValue("Tồn kho an toàn"); break;
                        case "reorder point": table.getColumnModel().getColumn(i).setHeaderValue("Điểm đặt hàng"); break;
                        case "stock quantity": table.getColumnModel().getColumn(i).setHeaderValue("Số lượng tồn"); break;
                        case "received date": table.getColumnModel().getColumn(i).setHeaderValue("Ngày nhận"); break;
                        case "vendor": table.getColumnModel().getColumn(i).setHeaderValue("Nhà cung cấp"); break;
                        case "ingredient": table.getColumnModel().getColumn(i).setHeaderValue("Nguyên liệu"); break;
                        case "dish": table.getColumnModel().getColumn(i).setHeaderValue("Món ăn"); break;
                        case "recipe": table.getColumnModel().getColumn(i).setHeaderValue("Công thức"); break;
                        case "table": table.getColumnModel().getColumn(i).setHeaderValue("Bàn"); break;
                        case "capacity": table.getColumnModel().getColumn(i).setHeaderValue("Sức chứa"); break;
                        case "location": table.getColumnModel().getColumn(i).setHeaderValue("Vị trí"); break;
                    }
                }
            } else if (comp instanceof JPanel) {
                // Đệ quy áp dụng style cho các panel con
                applyPanelStyle((JPanel) comp);
            }
        }
        
        // Tìm panel chứa các nút CRUD
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel buttonPanel = (JPanel) comp;
                // Kiểm tra xem panel có chứa các nút CRUD không
                boolean hasCrudButtons = false;
                for (Component button : buttonPanel.getComponents()) {
                    if (button instanceof JButton) {
                        String buttonText = ((JButton) button).getText().toLowerCase();
                        if (buttonText.equals("thêm") || buttonText.equals("cập nhật") || 
                            buttonText.equals("xóa") || buttonText.equals("làm mới")) {
                            hasCrudButtons = true;
                            break;
                        }
                    }
                }
                
                // Nếu tìm thấy panel chứa nút CRUD, thêm nút Xóa trắng
                if (hasCrudButtons) {
                    JButton clearButton = new JButton("Xóa trắng");
                    clearButton.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
                    clearButton.setFont(BUTTON_FONT);
                    clearButton.setBackground(PRIMARY_COLOR);
                    clearButton.setForeground(Color.WHITE);
                    clearButton.setFocusPainted(false);
                    clearButton.setBorderPainted(false);
                    clearButton.addActionListener(e -> clearForm(panel));
                    buttonPanel.add(clearButton);
                }
            }
        }
    }
    
    private void clearForm(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setText("");
            } else if (comp instanceof JTextArea) {
                ((JTextArea) comp).setText("");
            } else if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setSelectedIndex(0);
            } else if (comp instanceof JCheckBox) {
                ((JCheckBox) comp).setSelected(false);
            } else if (comp instanceof JSpinner) {
                ((JSpinner) comp).setValue(0);
            } else if (comp instanceof JPanel) {
                clearForm((JPanel) comp);
            }
        }
        
        JOptionPane.showMessageDialog(this,
            "Đã xóa trắng form thành công!",
            "Thông báo",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
            "Hệ thống quản lý nhà hàng\nPhiên bản 1.0\n\n" +
            "Phát triển bởi nhóm Java Phân Tán\n" +
            "Đại học Công nghiệp TP.HCM",
            "Giới thiệu",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void initComponents() {
        // Tạo panel chính
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(PADDING_BORDER);
        
        // Tạo tiêu đề
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ NHÀ HÀNG", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(PADDING, 0, PADDING * 2, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Tạo tabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TAB_FONT);
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(Color.BLACK);
        
        // Thiết lập style cho tabbedPane
        UIManager.put("TabbedPane.selected", PRIMARY_COLOR);
        UIManager.put("TabbedPane.contentAreaColor", BACKGROUND_COLOR);
        UIManager.put("TabbedPane.focus", new Color(0, 0, 0, 0));
        
        try {
            // Thêm các tab cho từng view admin
            tabbedPane.addTab("Quản lý bàn", new DiningTableAdminView());
            tabbedPane.addTab("Quản lý món ăn", new DishAdminView());
            tabbedPane.addTab("Quản lý lịch sử giá món", new DishPriceHistoryAdminView());
            tabbedPane.addTab("Quản lý nguyên liệu", new IngredientAdminView());
            tabbedPane.addTab("Quản lý loại nguyên liệu", new IngredientModelAdminView());
            tabbedPane.addTab("Quản lý lô nguyên liệu", new IngredientBatchAdminView());
            tabbedPane.addTab("Quản lý công thức", new RecipeAdminView());
            tabbedPane.addTab("Quản lý đơn hàng", new OrderHeaderAdminView());
            tabbedPane.addTab("Quản lý chi tiết đơn hàng", new OrderDetailAdminView());
            tabbedPane.addTab("Quản lý đơn mua hàng", new PurchaseOrderHeaderAdminView());
            tabbedPane.addTab("Quản lý chi tiết đơn mua", new PurchaseOrderDetailAdminView());
            tabbedPane.addTab("Quản lý thanh toán", new PaymentAdminView());
            tabbedPane.addTab("Quản lý báo cáo", new ReportAdminView());
            tabbedPane.addTab("Quản lý nhà cung cấp", new VendorAdminView());
            
            // Áp dụng style cho tất cả các panel trong tabbedPane
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component component = tabbedPane.getComponentAt(i);
                if (component instanceof JPanel) {
                    applyPanelStyle((JPanel) component);
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi khởi tạo giao diện: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    public static void main(String[] args) {
        try {
            // Thiết lập giao diện look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Thiết lập style chung cho toàn bộ ứng dụng
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", BUTTON_FONT);
            UIManager.put("Button.focus", new Color(0, 0, 0, 0));
            UIManager.put("Button.border", BorderFactory.createEmptyBorder(5, 15, 5, 15));
            
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", Color.BLACK);
            UIManager.put("TextField.font", INPUT_FONT);
            UIManager.put("TextField.border", INPUT_BORDER);
            
            UIManager.put("ComboBox.background", Color.WHITE);
            UIManager.put("ComboBox.foreground", Color.BLACK);
            UIManager.put("ComboBox.font", INPUT_FONT);
            UIManager.put("ComboBox.border", INPUT_BORDER);
            
            UIManager.put("Table.background", Color.WHITE);
            UIManager.put("Table.foreground", Color.BLACK);
            UIManager.put("Table.font", TABLE_ROW_FONT);
            UIManager.put("TableHeader.background", HEADER_COLOR);
            UIManager.put("TableHeader.foreground", Color.BLACK);
            UIManager.put("TableHeader.font", TABLE_HEADER_FONT);
            
            // Thiết lập kích thước mặc định cho các thành phần
            UIManager.put("Button.preferredSize", new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
            UIManager.put("TextField.preferredSize", new Dimension(200, INPUT_HEIGHT));
            UIManager.put("ComboBox.preferredSize", new Dimension(200, INPUT_HEIGHT));
            UIManager.put("Table.rowHeight", TABLE_ROW_HEIGHT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new AdminViewRunner().setVisible(true);
        });
    }
} 