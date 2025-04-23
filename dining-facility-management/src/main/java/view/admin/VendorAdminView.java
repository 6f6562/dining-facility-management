package view.admin;

import model.Vendor;
import service.VendorService;
import service.impl.VendorServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class VendorAdminView extends JPanel {
    private final VendorService vendorService;
    
    // UI Components
    private JTable vendorTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField addressField;
    private JCheckBox activeFlagCheckBox;
    private JCheckBox preferredVendorStatusCheckBox;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Data
    private Integer selectedId = null;
    
    // Constants
    private static final Color PRIMARY_COLOR = new Color(0, 120, 212);
    private static final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private static final Color HEADER_COLOR = new Color(230, 230, 230);
    private static final Border PADDING_BORDER = new EmptyBorder(10, 10, 10, 10);
    private static final Border COMPONENT_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        new EmptyBorder(5, 5, 5, 5)
    );

    public VendorAdminView() throws RemoteException {
        this.vendorService = new VendorServiceImpl();
        initComponents();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Quản lý nhà cung cấp"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name Field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tên nhà cung cấp:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField();
        nameField.setBorder(COMPONENT_BORDER);
        formPanel.add(nameField, gbc);

        // Address Field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField();
        addressField.setBorder(COMPONENT_BORDER);
        formPanel.add(addressField, gbc);

        // Checkboxes Panel
        JPanel checkboxesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        activeFlagCheckBox = new JCheckBox("Còn hoạt động");
        preferredVendorStatusCheckBox = new JCheckBox("Ưu tiên");
        
        // Set default values
        activeFlagCheckBox.setSelected(true);
        preferredVendorStatusCheckBox.setSelected(false);
        
        checkboxesPanel.add(activeFlagCheckBox);
        checkboxesPanel.add(preferredVendorStatusCheckBox);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(checkboxesPanel, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = new JButton("Thêm");
        updateButton = new JButton("Cập nhật");
        deleteButton = new JButton("Xoá");
        refreshButton = new JButton("Làm mới");

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

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Table
        String[] columnNames = {"ID", "Tên", "Địa chỉ", "Còn hoạt động", "Ưu tiên"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vendorTable = new JTable(tableModel);
        vendorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader header = vendorTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setForeground(Color.BLACK);
        JScrollPane scrollPane = new JScrollPane(vendorTable);

        // Add components to panel
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<Vendor> vendors = vendorService.findAll();
            for (Vendor vendor : vendors) {
                tableModel.addRow(new Object[]{
                    vendor.getId(),
                    vendor.getName(),
                    vendor.getAddress(),
                    vendor.isActiveFlag() ? "Có" : "Không",
                    vendor.isPreferredVendorStatus() ? "Có" : "Không"
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void setupEventListeners() {
        // Table selection listener
        vendorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = vendorTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    try {
                        Vendor vendor = vendorService.findById(selectedId);
                        if (vendor != null) {
                            nameField.setText(vendor.getName());
                            addressField.setText(vendor.getAddress());
                            activeFlagCheckBox.setSelected(vendor.isActiveFlag());
                            preferredVendorStatusCheckBox.setSelected(vendor.isPreferredVendorStatus());
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin nhà cung cấp: " + ex.getMessage());
                    }
                }
            }
        });

        // Add button listener
        addButton.addActionListener(e -> {
            try {
                if (validateForm()) {
                    Vendor vendor = new Vendor();
                    vendor.setName(nameField.getText().trim());
                    vendor.setAddress(addressField.getText().trim());
                    vendor.setActiveFlag(activeFlagCheckBox.isSelected());
                    vendor.setPreferredVendorStatus(preferredVendorStatusCheckBox.isSelected());

                    vendorService.create(vendor);
                    JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp thành công!");
                    resetForm();
                    loadTableData();
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm nhà cung cấp: " + ex.getMessage());
            }
        });

        // Update button listener
        updateButton.addActionListener(e -> {
            try {
                if (selectedId != null && validateForm()) {
                    Vendor vendor = vendorService.findById(selectedId);
                    if (vendor != null) {
                        vendor.setName(nameField.getText().trim());
                        vendor.setAddress(addressField.getText().trim());
                        vendor.setActiveFlag(activeFlagCheckBox.isSelected());
                        vendor.setPreferredVendorStatus(preferredVendorStatusCheckBox.isSelected());

                        vendorService.update(vendor);
                        JOptionPane.showMessageDialog(this, "Cập nhật nhà cung cấp thành công!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần cập nhật");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật nhà cung cấp: " + ex.getMessage());
            }
        });

        // Delete button listener
        deleteButton.addActionListener(e -> {
            try {
                if (selectedId != null) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa nhà cung cấp này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        vendorService.deleteById(selectedId);
                        JOptionPane.showMessageDialog(this, "Xóa nhà cung cấp thành công!");
                        resetForm();
                        loadTableData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần xóa");
                }
            } catch (RemoteException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa nhà cung cấp: " + ex.getMessage());
            }
        });

        // Refresh button listener
        refreshButton.addActionListener(e -> {
            resetForm();
            loadTableData();
        });
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhà cung cấp");
            return false;
        }

        if (addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ nhà cung cấp");
            return false;
        }

        return true;
    }

    private void resetForm() {
        selectedId = null;
        nameField.setText("");
        addressField.setText("");
        activeFlagCheckBox.setSelected(true);
        preferredVendorStatusCheckBox.setSelected(false);
        vendorTable.clearSelection();
    }
} 