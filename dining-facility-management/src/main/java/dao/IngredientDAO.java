package dao;

import model.Ingredient;

import java.util.Optional;

public class IngredientDAO extends GenericDAOImpl<Ingredient, Integer> {
    public IngredientDAO() {
        super(Ingredient.class);
    }

    // TODO: Custom methods here
    public Optional<Ingredient> findTopByIngredientModelIdOrderByStockQuantityAsc(int modelId) {
        try {
            Ingredient result = em
                    .createQuery("SELECT i FROM Ingredient i WHERE i.ingredientModel.id = :modelId ORDER BY i.stockQuantity ASC", Ingredient.class)
                    .setParameter("modelId", modelId)
                    .setMaxResults(1)
                    .getSingleResult();

            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
