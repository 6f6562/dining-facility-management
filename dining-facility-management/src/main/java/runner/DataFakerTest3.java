package runner;


import model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DataFakerTest3 {
    public static void main(String[] args) {
        // Khởi tạo Faker và EntityManager
        Faker faker = new Faker();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("mariadb-pu");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Lấy danh sách Vendor đã tạo
        List<Vendor> vendors = em.createQuery("SELECT v FROM Vendor v", Vendor.class).getResultList();

        // Lấy danh sách Ingredient đã tạo
        List<Ingredient> ingredients = em.createQuery("SELECT i FROM Ingredient i", Ingredient.class).getResultList();

        // Tạo dữ liệu cho PurchaseOrderHeader
        for (int i = 0; i < 10; i++) { // Tạo 10 đơn hàng
            PurchaseOrderHeader purchaseOrderHeader = new PurchaseOrderHeader();
            purchaseOrderHeader.setVendor(vendors.get(faker.number().numberBetween(0, vendors.size())));
            purchaseOrderHeader.setOrderDate(LocalDateTime.now().minusDays(faker.number().numberBetween(10, 100))); // Ngày đặt hàng
            purchaseOrderHeader.setShipDate(LocalDateTime.now().plusDays(faker.number().numberBetween(1, 20))); // Ngày giao hàng
            purchaseOrderHeader.setStatus(faker.options().option("Pending", "Completed", "Cancelled"));
            purchaseOrderHeader.setSubTotal(faker.number().randomDouble(2, 100, 5000)); // Tổng tiền
            em.persist(purchaseOrderHeader);

            // Tạo dữ liệu cho PurchaseOrderDetail
            int detailCount = faker.number().numberBetween(1, 5); // Mỗi đơn hàng có từ 1-5 dòng chi tiết
            for (int j = 0; j < detailCount; j++) {
                PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail();
                purchaseOrderDetail.setPurchaseOrderHeader(purchaseOrderHeader);
                purchaseOrderDetail.setUnitPrice(faker.number().randomDouble(2, 5, 100)); // Giá đơn vị
                purchaseOrderDetail.setOrderQty(faker.number().randomDouble(2, 1, 20)); // Số lượng đặt
                purchaseOrderDetail.setReceivedQty(faker.number().randomDouble(2, 0, 20)); // Số lượng nhận
                purchaseOrderDetail.setRejectedQty(faker.number().randomDouble(2, 0, 5)); // Số lượng bị trả
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

        em.getTransaction().commit();
        em.close();
        emf.close();

        System.out.println("Dữ liệu giả cho PurchaseOrderHeader, PurchaseOrderDetail và IngredientBatch đã được tạo thành công!");
    }
}
