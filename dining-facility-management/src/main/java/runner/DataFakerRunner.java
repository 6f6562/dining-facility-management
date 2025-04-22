package runner;

import model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataFakerRunner {
    public static void main(String[] args) {
        // Khởi tạo Faker và EntityManager
        Faker faker = new Faker();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("mariadb-pu");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // --- STEP 1: Tạo dữ liệu DiningTable ---
        for (int i = 0; i < 10; i++) {
            DiningTable table = new DiningTable();
            table.setTableNumber(i + 1);
            table.setSeatingCapacity(faker.number().numberBetween(2, 10));
            table.setStatus(faker.bool().bool() ? "Available" : "Occupied");
            em.persist(table);
        }

        // --- STEP 2: Tạo dữ liệu Dish ---
        List<Dish> dishes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Dish dish = new Dish();
            dish.setName(faker.food().dish());
            dish.setDescription(faker.lorem().sentence());
            dish.setCalories(faker.number().numberBetween(100, 800));
            dish.setCategory(faker.food().ingredient());
            dish.setPreparationTime(faker.number().numberBetween(10, 60)); // Thời gian nấu (phút)
            dish.setUnitPrice(faker.number().randomDouble(2, 5, 50));
            dishes.add(dish);
            em.persist(dish);
        }

        // --- STEP 3: Tạo dữ liệu IngredientModel ---
        List<IngredientModel> ingredientModels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            IngredientModel ingredientModel = new IngredientModel();
            ingredientModel.setName(faker.food().ingredient());
            ingredientModel.setDescription(faker.lorem().sentence());
            ingredientModels.add(ingredientModel);
            em.persist(ingredientModel);
        }

        // --- STEP 4: Tạo dữ liệu Ingredient ---
        List<Ingredient> ingredients = new ArrayList<>();
        for (IngredientModel ingredientModel : ingredientModels) {
            for (int i = 0; i < 3; i++) {
                Ingredient ingredient = new Ingredient();
                ingredient.setName(faker.food().ingredient() + " - " + faker.number().digits(4)); // Tạo tên ingredient
                ingredient.setIngredientModel(ingredientModel);
                ingredient.setUnitOfMeasure(faker.options().option("kg", "liters", "pieces"));
                ingredient.setStockQuantity(faker.number().randomDouble(2, 1, 100));
                ingredient.setSafetyStockLevel(faker.number().randomDouble(2, 5, 10));
                ingredient.setReorderPoint(faker.number().randomDouble(2, 10, 20));
                ingredient.setExpiryDate(LocalDate.now().plusDays(faker.number().numberBetween(10, 365)));
                ingredients.add(ingredient);
                em.persist(ingredient);
            }
        }

        // --- STEP 5: Tạo dữ liệu Vendor ---
        List<Vendor> vendors = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Vendor vendor = new Vendor();
            vendor.setName(faker.company().name());
            vendor.setAddress(faker.address().fullAddress());
            vendor.setActiveFlag(faker.bool().bool());
            vendor.setPreferredVendorStatus(faker.bool().bool());
            vendors.add(vendor);
            em.persist(vendor);
        }

        // --- STEP 6: Tạo dữ liệu DishPriceHistory ---
        for (Dish dish : dishes) {
            int historyCount = faker.number().numberBetween(1, 3); // Số lần thay đổi giá
            LocalDateTime startDate = LocalDateTime.now().minusMonths(historyCount);

            for (int i = 0; i < historyCount; i++) {
                DishPriceHistory priceHistory = new DishPriceHistory();
                priceHistory.setDish(dish);
                priceHistory.setPrice(faker.number().randomDouble(2, 5, 50)); // Giá món ăn
                priceHistory.setStartDate(startDate);
                priceHistory.setEndDate(startDate.plusMonths(1));
                em.persist(priceHistory);

                startDate = startDate.plusMonths(1);
            }
        }

        // --- STEP 7: Tạo dữ liệu Recipe ---
        for (Dish dish : dishes) {
            int ingredientCount = faker.number().numberBetween(1, 5); // Số nguyên liệu cho mỗi món ăn
            for (int i = 0; i < ingredientCount; i++) {
                Recipe recipe = new Recipe();
                recipe.setDish(dish);
                recipe.setIngredientModel(ingredientModels.get(faker.number().numberBetween(0, ingredientModels.size())));
                recipe.setRequiredQuantity(faker.number().randomDouble(2, (int) 0.1, 5)); // Số lượng nguyên liệu cần thiết
                em.persist(recipe);
            }
        }

        // --- STEP 8: Tạo dữ liệu PurchaseOrderHeader và PurchaseOrderDetail ---
        for (int i = 0; i < 10; i++) { // Tạo 10 đơn hàng
            PurchaseOrderHeader purchaseOrderHeader = new PurchaseOrderHeader();
            purchaseOrderHeader.setVendor(vendors.get(faker.number().numberBetween(0, vendors.size())));
            purchaseOrderHeader.setOrderDate(LocalDateTime.now().minusDays(faker.number().numberBetween(10, 100)));
            purchaseOrderHeader.setShipDate(LocalDateTime.now().plusDays(faker.number().numberBetween(1, 20)));
            purchaseOrderHeader.setStatus(faker.options().option("Pending", "Completed", "Cancelled"));
            purchaseOrderHeader.setSubTotal(faker.number().randomDouble(2, 100, 5000));
            em.persist(purchaseOrderHeader);

            int detailCount = faker.number().numberBetween(1, 5);
            for (int j = 0; j < detailCount; j++) {
                PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
                purchaseOrderDetail.setPurchaseOrderHeader(purchaseOrderHeader);
                purchaseOrderDetail.setUnitPrice(faker.number().randomDouble(2, 5, 100));
                purchaseOrderDetail.setOrderQty(faker.number().randomDouble(2, 1, 20));
                purchaseOrderDetail.setReceivedQty(faker.number().randomDouble(2, 0, 20));
                purchaseOrderDetail.setRejectedQty(faker.number().randomDouble(2, 0, 5));
                purchaseOrderDetail.setLineTotal(purchaseOrderDetail.getUnitPrice() * purchaseOrderDetail.getOrderQty());
                em.persist(purchaseOrderDetail);

                // Tạo dữ liệu cho IngredientBatch
                int batchCount = faker.number().numberBetween(1, 3); // Mỗi chi tiết có từ 1-3 lô nguyên liệu
                for (int k = 0; k < batchCount; k++) {
                    IngredientBatch ingredientBatch = new IngredientBatch();
                    ingredientBatch.setIngredient(ingredients.get(faker.number().numberBetween(0, ingredients.size())));
                    ingredientBatch.setPurchaseOrderDetail(purchaseOrderDetail);
                    ingredientBatch.setStockQuantity(faker.number().randomDouble(2, 1, 20)); // Số lượng tồn kho
                    ingredientBatch.setReceivedDate(LocalDate.now().minusDays(faker.number().numberBetween(0, 30))); // Ngày nhận hàng
                    ingredientBatch.setExpiryDate(LocalDate.now().plusDays(faker.number().numberBetween(30, 365))); // Ngày hết hạn
                    em.persist(ingredientBatch);
                }
            }


        }

        // --- STEP 9: Tạo dữ liệu OrderHeader và OrderDetail ---
        List<DiningTable> diningTables = em.createQuery("SELECT t FROM DiningTable t", DiningTable.class).getResultList();
        for (int i = 0; i < 10; i++) { // Tạo 10 hóa đơn
            OrderHeader orderHeader = new OrderHeader();
            orderHeader.setDiningTable(diningTables.get(faker.number().numberBetween(0, diningTables.size())));
            orderHeader.setOrderDate(LocalDateTime.now().minusHours(faker.number().numberBetween(1, 72)));
            orderHeader.setStatus(faker.options().option("Pending", "Completed", "Cancelled"));
            orderHeader.setSubTotal(0);
            em.persist(orderHeader);

            double subTotal = 0;
            int detailCount = faker.number().numberBetween(1, 5);
            for (int j = 0; j < detailCount; j++) {
                Dish dish = dishes.get(faker.number().numberBetween(0, dishes.size()));
                int orderQty = faker.number().numberBetween(1, 10);
                double price = dish.getUnitPrice();
                double lineSubTotal = orderQty * price;

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderHeader(orderHeader);
                orderDetail.setDish(dish);
                orderDetail.setOrderQty(orderQty);
                orderDetail.setPrice(price);
                orderDetail.setOrderDate(LocalDateTime.now().minusHours(faker.number().numberBetween(1, 72)));
                orderDetail.setDeliveryTime(LocalDateTime.now().plusHours(faker.number().numberBetween(1, 5)));
                orderDetail.setSubTotal(lineSubTotal);
                orderDetail.setSpecialInstructions(faker.lorem().sentence());
                em.persist(orderDetail);

                subTotal += lineSubTotal;
            }
            orderHeader.setSubTotal(subTotal);
            em.merge(orderHeader);
        }

        em.getTransaction().commit();
        em.close();
        emf.close();

        System.out.println("Dữ liệu giả đã được tạo thành công!");
    }
}
