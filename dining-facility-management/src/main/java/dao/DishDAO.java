package dao;

import jakarta.persistence.TypedQuery;
import model.Dish;

import java.util.List;

public class DishDAO extends GenericDAOImpl<Dish, Integer> {
    public DishDAO() {
        super(Dish.class);
    }

    // TODO: Custom methods here
    /**
     * Tìm danh sách món ăn theo loại (category)
     */
    public List<Dish> findByCategory(String category) {
        String jpql = "SELECT d FROM Dish d WHERE d.category = :category";
        TypedQuery<Dish> query = em.createQuery(jpql, Dish.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    /**
     * Tìm các món đang được bán (giả sử là các món có đơn giá > 0 và đang được dùng)
     */
    public List<Dish> findAvailableDishes() {
        String jpql = "SELECT d FROM Dish d WHERE d.unitPrice > 0";
        TypedQuery<Dish> query = em.createQuery(jpql, Dish.class);
        return query.getResultList();
    }

    /**
     * Tìm các món ăn trong khoảng giá cụ thể
     */
    public List<Dish> findDishesByPriceRange(double minPrice, double maxPrice) {
        String jpql = "SELECT d FROM Dish d WHERE d.unitPrice BETWEEN :minPrice AND :maxPrice";
        TypedQuery<Dish> query = em.createQuery(jpql, Dish.class);
        query.setParameter("minPrice", minPrice);
        query.setParameter("maxPrice", maxPrice);
        return query.getResultList();
    }
}
