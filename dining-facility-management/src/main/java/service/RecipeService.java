package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Recipe;
import utils.JPAUtil;

import java.rmi.RemoteException;
import java.util.List;

public interface RecipeService extends GenericService<Recipe, Integer> {
    List<Recipe> findByDishId(Integer dishId) throws RemoteException;

    List<Recipe> findByIngredientId(Integer ingredientId) throws RemoteException;
}
