package runner;

import model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataFakerTest1 {
    public static void main(String[] args) {
        // Khởi tạo Faker và EntityManager
        Faker faker = new Faker();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("mariadb-pu");
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();

        // Tạo dữ liệu DiningTable
        for (int i = 0; i < 10; i++) {
            DiningTable table = new DiningTable();
            table.setTableNumber(i + 1);
            table.setSeatingCapacity(faker.number().numberBetween(2, 10));
            table.setStatus(faker.bool().bool() ? "Available" : "Occupied");
            em.persist(table);
        }

        // Tạo dữ liệu Dish
        for (int i = 0; i < 10; i++) {
            Dish dish = new Dish();
            dish.setName(faker.food().dish());
            dish.setDescription(faker.lorem().sentence());
            dish.setCalories(faker.number().numberBetween(100, 800));
            dish.setCategory(faker.food().ingredient());
            dish.setPreparationTime(faker.number().numberBetween(10, 60)); // Thời gian nấu (phút)
            dish.setUnitPrice(faker.number().randomDouble(2, 5, 50));
            em.persist(dish);
        }

        // Tạo dữ liệu IngredientModel
        List<IngredientModel> ingredientModels = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            IngredientModel ingredientModel = new IngredientModel();
            ingredientModel.setName(faker.food().ingredient());
            ingredientModel.setDescription(faker.lorem().sentence());
            ingredientModels.add(ingredientModel);
            em.persist(ingredientModel);
        }

        // Tạo dữ liệu Ingredient
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
                em.persist(ingredient);
            }
        }

        // Tạo dữ liệu Vendor
        for (int i = 0; i < 10; i++) {
            Vendor vendor = new Vendor();
            vendor.setName(faker.company().name());
            vendor.setAddress(faker.address().fullAddress());
            vendor.setActiveFlag(faker.bool().bool());
            vendor.setPreferredVendorStatus(faker.bool().bool());
            em.persist(vendor);
        }

        em.getTransaction().commit();
        em.close();
        emf.close();

        System.out.println("Dữ liệu giả đã được tạo thành công!");
    }
}
