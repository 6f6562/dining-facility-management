package rmi;

import dao.*;
import service.*;
import service.impl.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.rmi.registry.LocateRegistry;

public class RMIServer {
    public static void main(String[] args) throws Exception {
        Context context = new InitialContext();
        LocateRegistry.createRegistry(9090);

        // DiningTableService
        DiningTableDAO diningTableDAO = new DiningTableDAO();
        DiningTableService diningTableService = new DiningTableServiceImpl(diningTableDAO);
        context.bind("rmi://localhost:9090/DiningTableService", diningTableService);

        // DishService
        DishDAO dishDAO = new DishDAO();
        DishService dishService = new DishServiceImpl(dishDAO);
        context.bind("rmi://localhost:9090/DishService", dishService);

        // DishPriceHistoryService
        DishPriceHistoryDAO dishPriceHistoryDAO = new DishPriceHistoryDAO();
        DishPriceHistoryService dishPriceHistoryService = new DishPriceHistoryServiceImpl(dishPriceHistoryDAO);
        context.bind("rmi://localhost:9090/DishPriceHistoryService", dishPriceHistoryService);

        // GenericService
//        GenericDAO genericDAO = new GenericDAO();
//        GenericService genericService = new GenericServiceImpl(genericDAO);
//        context.bind("rmi://localhost:9090/GenericService", genericService);

        // IngredientBatchService
        IngredientBatchDAO ingredientBatchDAO = new IngredientBatchDAO();
        IngredientBatchService ingredientBatchService = new IngredientBatchServiceImpl(ingredientBatchDAO);
        context.bind("rmi://localhost:9090/IngredientBatchService", ingredientBatchService);

        // IngredientModelService
        IngredientModelDAO ingredientModelDAO = new IngredientModelDAO();
        IngredientModelService ingredientModelService = new IngredientModelServiceImpl(ingredientModelDAO);
        context.bind("rmi://localhost:9090/IngredientModelService", ingredientModelService);

        // IngredientService
        IngredientDAO ingredientDAO = new IngredientDAO();
        IngredientService ingredientService = new IngredientServiceImpl(ingredientDAO);
        context.bind("rmi://localhost:9090/IngredientService", ingredientService);

        // OrderDetailService
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
        OrderDetailService orderDetailService = new OrderDetailServiceImpl(orderDetailDAO);
        context.bind("rmi://localhost:9090/OrderDetailService", orderDetailService);

        // OrderHeaderService
        OrderHeaderDAO orderHeaderDAO = new OrderHeaderDAO();
        OrderHeaderService orderHeaderService = new OrderHeaderServiceImpl(orderHeaderDAO);
        context.bind("rmi://localhost:9090/OrderHeaderService", orderHeaderService);

        // PaymentService
        PaymentDAO paymentDAO = new PaymentDAO();
        PaymentService paymentService = new PaymentServiceImpl(paymentDAO);
        context.bind("rmi://localhost:9090/PaymentService", paymentService);

        // PurchaseOrderDetailService
        PurchaseOrderDetailDAO purchaseOrderDetailDAO = new PurchaseOrderDetailDAO();
        PurchaseOrderDetailService purchaseOrderDetailService = new PurchaseOrderDetailServiceImpl(purchaseOrderDetailDAO);
        context.bind("rmi://localhost:9090/PurchaseOrderDetailService", purchaseOrderDetailService);

        // PurchaseOrderHeaderService
        PurchaseOrderHeaderDAO purchaseOrderHeaderDAO = new PurchaseOrderHeaderDAO();
        PurchaseOrderHeaderService purchaseOrderHeaderService = new PurchaseOrderHeaderServiceImpl(purchaseOrderHeaderDAO);
        context.bind("rmi://localhost:9090/PurchaseOrderHeaderService", purchaseOrderHeaderService);

        // RecipeService
        RecipeDAO recipeDAO = new RecipeDAO();
        RecipeService recipeService = new RecipeServiceImpl(recipeDAO);
        context.bind("rmi://localhost:9090/RecipeService", recipeService);

        // ReportService
        ReportDAO reportDAO = new ReportDAO();
        ReportService reportService = new ReportServiceImpl(reportDAO);
        context.bind("rmi://localhost:9090/ReportService", reportService);

        // VendorService
        VendorDAO vendorDAO = new VendorDAO();
        VendorService vendorService = new VendorServiceImpl(vendorDAO);
        context.bind("rmi://localhost:9090/VendorService", vendorService);

        System.out.println("RMI Server is running...");
    }
}
