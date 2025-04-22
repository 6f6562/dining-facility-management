package runner;


import model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.List;

public class DataFakerTest4 {
    public static void main(String[] args) {
        // Khởi tạo Faker và EntityManager
        Faker faker = new Faker();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("mariadb-pu");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Lấy danh sách DiningTable và Dish đã tạo
        List<DiningTable> diningTables = em.createQuery("SELECT t FROM DiningTable t", DiningTable.class).getResultList();
        List<Dish> dishes = em.createQuery("SELECT d FROM Dish d", Dish.class).getResultList();

        // Tạo dữ liệu cho OrderHeader
        for (int i = 0; i < 10; i++) { // Tạo 10 hóa đơn
            OrderHeader orderHeader = new OrderHeader();
            orderHeader.setDiningTable(diningTables.get(faker.number().numberBetween(0, diningTables.size())));
            orderHeader.setOrderDate(LocalDateTime.now().minusHours(faker.number().numberBetween(1, 72))); // Ngày đặt
            orderHeader.setStatus(faker.options().option("Pending", "Completed", "Cancelled"));
            orderHeader.setSubTotal(0); // Tính lại sau khi thêm OrderDetail
            em.persist(orderHeader);

            double subTotal = 0;

            // Tạo dữ liệu cho OrderDetail
            int detailCount = faker.number().numberBetween(1, 5); // Mỗi hóa đơn có từ 1-5 món ăn
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

                subTotal += lineSubTotal; // Cộng dồn tổng giá trị
            }

            orderHeader.setSubTotal(subTotal); // Cập nhật tổng giá trị hóa đơn
            em.merge(orderHeader);
        }

        em.getTransaction().commit();
        em.close();
        emf.close();

        System.out.println("Dữ liệu giả cho OrderHeader và OrderDetail đã được tạo thành công!");
    }
}
