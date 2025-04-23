package view.admin;

import model.IngredientModel;
import service.IngredientModelService;
import service.impl.IngredientModelServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

public class IngredientModelAdminView extends JPanel {
    private final IngredientModelService ingredientModelService;
    
    // UI Components
    private JTable modelTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextArea descriptionArea;
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

    public IngredientModelAdminView() throws RemoteException {
        this.ingredientModelService = new IngredientModelServiceImpl();
        initComponents();
        loadTableData();
        setupEventListeners();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(PADDING_BORDER);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Thông tin loại nguyên liệu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên loại:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        formPanel.add(descriptionScrollPane, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addButton = createStyledButton("Thêm", "Thêm loại nguyên liệu mới");
        updateButton = createStyledButton("Cập nhật", "Cập nhật loại nguyên liệu đã chọn");
        deleteButton = createStyledButton("Xóa", "Xóa loại nguyên liệu đã chọn");
        refreshButton = createStyledButton("Làm mới", "Làm mới danh sách loại nguyên liệu");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        // Create table
        String[] columnNames = {"ID", "Tên loại", "Mô tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modelTable = new JTable(tableModel);
        modelTable.setFillsViewportHeight(true);
        modelTable.setRowHeight(25);
        modelTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        modelTable.setGridColor(Color.LIGHT_GRAY);
        modelTable.setShowGrid(true);
        modelTable.setBackground(Color.WHITE);
        modelTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Style table header
        JTableHeader header = modelTable.getTableHeader();
        header.setBackground(HEADER_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane tableScrollPane = new JScrollPane(modelTable);
        tableScrollPane.setBorder(new TitledBorder("Danh sách loại nguyên liệu"));
        tableScrollPane.setBackground(Color.WHITE);

        // Add components to main panel
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
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
        // Table selection listener
        modelTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = modelTable.getSelectedRow();
                if (selectedRow >= 0) {
                    selectedId = (Integer) tableModel.getValueAt(selectedRow, 0);
                    
                    // Get the model details
                    try {
                        IngredientModel model = ingredientModelService.findById(selectedId);
                        if (model != null) {
                            nameField.setText(model.getName());
                            descriptionArea.setText(model.getDescription());
                        }
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin loại nguyên liệu: " + ex.getMessage(), 
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addModel());
        updateButton.addActionListener(e -> updateModel());
        deleteButton.addActionListener(e -> deleteModel());
        refreshButton.addActionListener(e -> loadTableData());
    }

    private void loadTableData() {
        try {
            tableModel.setRowCount(0);
            List<IngredientModel> models = ingredientModelService.findAll();
            for (IngredientModel model : models) {
                tableModel.addRow(new Object[]{
                    model.getId(),
                    model.getName(),
                    model.getDescription()
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách loại nguyên liệu: " + e.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addModel() {
        if (validateForm()) {
            try {
                IngredientModel model = new IngredientModel();
                model.setName(nameField.getText().trim());
                model.setDescription(descriptionArea.getText().trim());

                ingredientModelService.create(model);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Thêm loại nguyên liệu thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm loại nguyên liệu: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateModel() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại nguyên liệu cần cập nhật", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validateForm()) {
            try {
                IngredientModel model = ingredientModelService.findById(selectedId);
                if (model != null) {
                    model.setName(nameField.getText().trim());
                    model.setDescription(descriptionArea.getText().trim());

                    ingredientModelService.update(model);
                    loadTableData();
                    resetForm();
                    JOptionPane.showMessageDialog(this, "Cập nhật loại nguyên liệu thành công!", 
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật loại nguyên liệu: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteModel() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại nguyên liệu cần xóa", 
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa loại nguyên liệu này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                ingredientModelService.deleteById(selectedId);
                loadTableData();
                resetForm();
                JOptionPane.showMessageDialog(this, "Xóa loại nguyên liệu thành công!", 
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa loại nguyên liệu: " + e.getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetForm() {
        selectedId = null;
        nameField.setText("");
        descriptionArea.setText("");
        modelTable.clearSelection();
    }

    private boolean validateForm() {
        // Validate name
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên loại nguyên liệu không được để trống", 
                    "Lỗi xác thực", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
} 