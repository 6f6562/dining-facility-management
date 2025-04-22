package dao;

import jakarta.persistence.TypedQuery;
import model.Recipe;

import java.util.List;

public class RecipeDAO extends GenericDAOImpl<Recipe, Integer> {
    public RecipeDAO() {
        super(Recipe.class);
    }

    // TODO: Custom methods here
    public List<Recipe> findByDishId(Integer dishId) {
        String jpql = "SELECT r FROM Recipe r WHERE r.dish.id = :dishId";
        TypedQuery<Recipe> query = em.createQuery(jpql, Recipe.class);
        query.setParameter("dishId", dishId);
        return query.getResultList();
    }

    public List<Recipe> findByIngredientId(Integer ingredientId) {
        String jpql = "SELECT r FROM Recipe r WHERE r.ingredient.id = :ingredientId";
        TypedQuery<Recipe> query = em.createQuery(jpql, Recipe.class);
        query.setParameter("ingredientId", ingredientId);
        return query.getResultList();
    }
}
