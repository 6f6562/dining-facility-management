package runner;

import model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.List;

public class DataFakerTest2 {
    public static void main(String[] args) {
        // Khởi tạo Faker và EntityManager
        Faker faker = new Faker();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("mariadb-pu");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Lấy danh sách các món ăn (Dish) đã tạo
        List<Dish> dishes = em.createQuery("SELECT d FROM Dish d", Dish.class).getResultList();

        // Tạo dữ liệu DishPriceHistory
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

                // Cập nhật thời gian bắt đầu cho lần tiếp theo
                startDate = startDate.plusMonths(1);
            }
        }

        // Lấy danh sách các IngredientModel đã tạo
        List<IngredientModel> ingredientModels = em.createQuery("SELECT im FROM IngredientModel im", IngredientModel.class).getResultList();

        // Tạo dữ liệu Recipe
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

        em.getTransaction().commit();
        em.close();
        emf.close();

        System.out.println("Dữ liệu giả cho DishPriceHistory và Recipe đã được tạo thành công!");
    }
}
