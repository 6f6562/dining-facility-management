package dao;

import jakarta.persistence.TypedQuery;
import model.DishPriceHistory;

import java.util.List;

public class DishPriceHistoryDAO extends GenericDAOImpl<DishPriceHistory, Integer> {
    public DishPriceHistoryDAO() {
        super(DishPriceHistory.class);
    }

    // TODO: Custom methods here
    public List<DishPriceHistory> findByDishId(Integer dishId) {
        String jpql = "SELECT d FROM DishPriceHistory d WHERE d.dish.id = :dishId ORDER BY d.startDate DESC";
        TypedQuery<DishPriceHistory> query = em.createQuery(jpql, DishPriceHistory.class);
        query.setParameter("dishId", dishId);
        return query.getResultList();
    }

    public DishPriceHistory findLatestByDishId(Integer dishId) {
        String jpql = "SELECT d FROM DishPriceHistory d WHERE d.dish.id = :dishId ORDER BY d.startDate DESC";
        TypedQuery<DishPriceHistory> query = em.createQuery(jpql, DishPriceHistory.class);
        query.setParameter("dishId", dishId);
        query.setMaxResults(1);
        List<DishPriceHistory> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
