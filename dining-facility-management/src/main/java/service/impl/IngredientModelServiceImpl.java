package service.impl;

import dao.IngredientModelDAO;
import model.IngredientModel;
import service.IngredientModelService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class IngredientModelServiceImpl extends UnicastRemoteObject implements IngredientModelService {

    private final IngredientModelDAO ingredientModelDAO;

    public IngredientModelServiceImpl(IngredientModelDAO ingredientModelDAO) throws RemoteException {
        super();
        this.ingredientModelDAO = ingredientModelDAO;
    }

    @Override
    public void create(IngredientModel ingredientModel) throws RemoteException {
        ingredientModelDAO.create(ingredientModel);
    }

    @Override
    public void update(IngredientModel ingredientModel) throws RemoteException {
        ingredientModelDAO.update(ingredientModel);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        ingredientModelDAO.deleteById(id);
    }

    @Override
    public IngredientModel findById(Integer id) throws RemoteException {
        return ingredientModelDAO.findById(id);
    }

    @Override
    public List<IngredientModel> findAll() throws RemoteException {
        return ingredientModelDAO.findAll();
    }

    @Override
    public List<IngredientModel> findByDishId(Integer dishId) throws RemoteException {
        return ingredientModelDAO.findByDishId(dishId);
    }
}
