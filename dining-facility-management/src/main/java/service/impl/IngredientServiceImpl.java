package service.impl;

import dao.IngredientDAO;
import model.Ingredient;
import service.IngredientService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Optional;

public class IngredientServiceImpl extends UnicastRemoteObject implements IngredientService {
    private final IngredientDAO ingredientDAO;

    public IngredientServiceImpl(IngredientDAO ingredientDAO) throws RemoteException {
        super();
        this.ingredientDAO = ingredientDAO;
    }

    @Override
    public void create(Ingredient ingredient) throws RemoteException {
        ingredientDAO.create(ingredient);
    }

    @Override
    public void update(Ingredient ingredient) throws RemoteException {
        ingredientDAO.update(ingredient);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        ingredientDAO.deleteById(id);
    }

    @Override
    public Ingredient findById(Integer id) throws RemoteException {
        return ingredientDAO.findById(id);
    }

    @Override
    public List<Ingredient> findAll() throws RemoteException {
        return ingredientDAO.findAll();
    }

    @Override
    public Ingredient getTopIngredientByModelIdOrderByStockQuantity(int modelId) throws RemoteException{
        return ingredientDAO.findTopByIngredientModelIdOrderByStockQuantityAsc(modelId)
                .orElse(null); // hoặc throw custom exception nếu thích
    }
}
