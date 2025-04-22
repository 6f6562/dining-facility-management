package dao;

import jakarta.persistence.TypedQuery;
import model.IngredientBatch;

import java.time.LocalDate;
import java.util.List;

public class IngredientBatchDAO extends GenericDAOImpl<IngredientBatch, Integer> {
    public IngredientBatchDAO() {
        super(IngredientBatch.class);
    }

    // TODO: Custom methods here
    /**
     * Tìm các lô nguyên liệu theo mã nguyên liệu (ingredientId)
     */
    public List<IngredientBatch> findByIngredientId(Integer ingredientId) {
        String jpql = "SELECT b FROM IngredientBatch b WHERE b.ingredient.id = :ingredientId";
        TypedQuery<IngredientBatch> query = em.createQuery(jpql, IngredientBatch.class);
        query.setParameter("ingredientId", ingredientId); // vì id là Integer
        return query.getResultList();
    }

    /**
     * Tìm các lô sắp hết hạn trong X ngày tới
     */
    public List<IngredientBatch> findExpiringSoon(int daysBeforeExpire) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysBeforeExpire);
        String jpql = "SELECT b FROM IngredientBatch b WHERE b.expiryDate <= :thresholdDate";
        TypedQuery<IngredientBatch> query = em.createQuery(jpql, IngredientBatch.class);
        query.setParameter("thresholdDate", thresholdDate);
        return query.getResultList();
    }

    /**
     * Tìm các lô có tồn kho thấp hơn ngưỡng cho trước
     */
    public List<IngredientBatch> findLowStock(int threshold) {
        String jpql = "SELECT b FROM IngredientBatch b WHERE b.stockQuantity < :threshold";
        TypedQuery<IngredientBatch> query = em.createQuery(jpql, IngredientBatch.class);
        query.setParameter("threshold", (double) threshold);
        return query.getResultList();
    }
}
