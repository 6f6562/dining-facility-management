package view.admin;

import model.DiningTable;
import service.DiningTableService;
import service.impl.DiningTableServiceImpl;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;

public class AdminViewRunner extends JFrame {
    private JTabbedPane tabbedPane;
    private JMenuBar menuBar;

    public AdminViewRunner() {
        setTitle("Quản lý nhà hàng - Giao diện Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        initMenuBar();
        initComponents();
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();
        
        // Menu File
        JMenu fileMenu = new JMenu("Tệp");
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        // Menu View
        JMenu viewMenu = new JMenu("Xem");
        JMenuItem refreshItem = new JMenuItem("Làm mới");
        refreshItem.addActionListener(e -> refreshCurrentView());
        viewMenu.add(refreshItem);
        
        // Menu Help
        JMenu helpMenu = new JMenu("Trợ giúp");
        JMenuItem aboutItem = new JMenuItem("Giới thiệu");
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
        switch (tabName) {
            case "Quản lý bàn": return new DiningTableAdminView();
            case "Quản lý món ăn": return new DishAdminView();
            case "Quản lý lịch sử giá món": return new DishPriceHistoryAdminView();
            case "Quản lý nguyên liệu": return new IngredientAdminView();
            case "Quản lý loại nguyên liệu": return new IngredientModelAdminView();
            case "Quản lý lô nguyên liệu": return new IngredientBatchAdminView();
            case "Quản lý công thức": return new RecipeAdminView();
            case "Quản lý đơn hàng": return new OrderHeaderAdminView();
            case "Quản lý chi tiết đơn hàng": return new OrderDetailAdminView();
            case "Quản lý đơn mua hàng": return new PurchaseOrderHeaderAdminView();
            case "Quản lý chi tiết đơn mua": return new PurchaseOrderDetailAdminView();
            case "Quản lý thanh toán": return new PaymentAdminView();
            case "Quản lý báo cáo": return new ReportAdminView();
            case "Quản lý nhà cung cấp": return new VendorAdminView();
            default: throw new IllegalArgumentException("Không tìm thấy tab: " + tabName);
        }
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
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        try {
            // Thêm các tab cho từng view admin
            DiningTableService diningTableService = new DiningTableServiceImpl();
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
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi khởi tạo giao diện: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }

        add(tabbedPane);
    }

    public static void main(String[] args) {
        try {
            // Thiết lập giao diện look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new AdminViewRunner().setVisible(true);
        });
    }
} 