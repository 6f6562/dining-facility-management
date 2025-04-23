package service.impl;

import dao.RecipeDAO;
import model.Recipe;
import service.RecipeService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class RecipeServiceImpl extends UnicastRemoteObject implements RecipeService {
    private final RecipeDAO recipeDAO;

    public RecipeServiceImpl(RecipeDAO recipeDAO) throws RemoteException {
        super();
        this.recipeDAO = recipeDAO;
    }

    @Override
    public void create(Recipe recipe) throws RemoteException {
        recipeDAO.create(recipe);
    }

    @Override
    public void update(Recipe recipe) throws RemoteException {
        recipeDAO.update(recipe);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        recipeDAO.deleteById(id);
    }

    @Override
    public Recipe findById(Integer id) throws RemoteException {
        return recipeDAO.findById(id);
    }

    @Override
    public List<Recipe> findAll() throws RemoteException {
        return recipeDAO.findAll();
    }

    @Override
    public List<Recipe> findByDishId(Integer dishId) throws RemoteException {
        return recipeDAO.findByDishId(dishId);
    }

    @Override
    public List<Recipe> findByIngredientId(Integer ingredientId) throws RemoteException {
        return recipeDAO.findByIngredientId(ingredientId);
    }
}
