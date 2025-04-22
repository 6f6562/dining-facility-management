package dao;

import jakarta.persistence.TypedQuery;
import model.IngredientModel;

import java.util.List;

public class IngredientModelDAO extends GenericDAOImpl<IngredientModel, Integer> {
    public IngredientModelDAO() {
        super(IngredientModel.class);
    }

    // TODO: Custom methods here
    public List<IngredientModel> findByDishId(Integer dishId) {
        String jpql = "SELECT im FROM IngredientModel im JOIN im.recipes r WHERE r.dish.id = :dishId";
        TypedQuery<IngredientModel> query = em.createQuery(jpql, IngredientModel.class);
        query.setParameter("dishId", dishId);
        return query.getResultList();
    }
}
