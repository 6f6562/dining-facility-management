package view.staff;

import javax.swing.*;
import java.awt.*;

public class Staff1Runner extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(30, 144, 255); // Dodger Blue
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font TAB_FONT = new Font("Segoe UI", Font.PLAIN, 16);

    public Staff1Runner() {
        initializeFrame();
        createComponents();
    }

    private void initializeFrame() {
        setTitle("Quản lý nhà hàng - Nhân viên phục vụ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);
    }

    private void createComponents() {
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create title label
        JLabel titleLabel = new JLabel("Quản lý nhà hàng - Nhân viên phục vụ");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TAB_FONT);
        tabbedPane.setBackground(BACKGROUND_COLOR);

        try {
            // Add FoodOrderingView tab
            FoodOrderingView foodOrderingView = new FoodOrderingView();
            tabbedPane.addTab("Đặt món", foodOrderingView);

            // Add TablePaymentView tab
            TablePaymentView tablePaymentView = new TablePaymentView();
            tabbedPane.addTab("Thanh toán", tablePaymentView);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi khởi tạo giao diện: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);
    }

    public static void main(String[] args) {
        try {
            // Set look and feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run the application
        SwingUtilities.invokeLater(() -> {
            Staff1Runner runner = new Staff1Runner();
            runner.setVisible(true);
        });
    }
} 