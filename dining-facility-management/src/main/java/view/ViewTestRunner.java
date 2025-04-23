package view;

import model.*;
import service.*;
import service.impl.*;
import view.staff.*;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ViewTestRunner {
    public static void main(String[] args) {
        try {
            // Initialize services
            DiningTableService diningTableService = new DiningTableServiceImpl();
            OrderHeaderService orderHeaderService = new OrderHeaderServiceImpl();
            OrderDetailService orderDetailService = new OrderDetailServiceImpl();
            DishService dishService = new DishServiceImpl();
            IngredientService ingredientService = new IngredientServiceImpl();
            IngredientModelService ingredientModelService = new IngredientModelServiceImpl();
            VendorServiceImpl vendorService = new VendorServiceImpl();
            RecipeService recipeService = new RecipeServiceImpl();
            PaymentService paymentService = new PaymentServiceImpl();
            PurchaseOrderHeaderService purchaseOrderHeaderService = new PurchaseOrderHeaderServiceImpl();
            PurchaseOrderDetailService purchaseOrderDetailService = new PurchaseOrderDetailServiceImpl();
            ReportService reportService = new ReportServiceImpl();

            // Create and show the main frame
            JFrame frame = new JFrame("Dining Facility Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);

            // Create tabbed pane
            JTabbedPane tabbedPane = new JTabbedPane();

            // Add views to tabbed pane
            tabbedPane.addTab("Dining Tables", new DiningTableManagementView(diningTableService));
            tabbedPane.addTab("Orders", new OrderManagementView(0, orderHeaderService, orderDetailService, dishService));
            tabbedPane.addTab("Kitchen", new KitchenView(orderHeaderService, orderDetailService));
            tabbedPane.addTab("Payment", new PaymentView(paymentService, orderHeaderService));
            tabbedPane.addTab("Dishes", new DishManagementView(dishService, recipeService));
            tabbedPane.addTab("Inventory", new InventoryView(ingredientService, ingredientModelService));
            tabbedPane.addTab("Vendors", new VendorManagementView(vendorService));
            tabbedPane.addTab("Reports", new ReportView(
                reportService,
                orderHeaderService,
                orderDetailService,
                dishService,
                ingredientService,
                vendorService,
                purchaseOrderHeaderService
            ));

            // Add tabbed pane to frame
            frame.add(tabbedPane);
            frame.setVisible(true);

        } catch (RemoteException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing services: " + e.getMessage());
        }
    }
} 