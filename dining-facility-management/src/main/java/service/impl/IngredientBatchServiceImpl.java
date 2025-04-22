package service.impl;

import dao.IngredientBatchDAO;
import model.IngredientBatch;
import service.IngredientBatchService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class IngredientBatchServiceImpl extends UnicastRemoteObject implements IngredientBatchService {

    private final IngredientBatchDAO ingredientBatchDAO;

    public IngredientBatchServiceImpl() throws RemoteException {
        super();
        this.ingredientBatchDAO = new IngredientBatchDAO();
    }

    @Override
    public void create(IngredientBatch ingredientBatch) throws RemoteException {
        ingredientBatchDAO.create(ingredientBatch);
    }

    @Override
    public void update(IngredientBatch ingredientBatch) throws RemoteException {
        ingredientBatchDAO.update(ingredientBatch);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        ingredientBatchDAO.deleteById(id);
    }

    @Override
    public IngredientBatch findById(Integer id) throws RemoteException {
        return ingredientBatchDAO.findById(id);
    }

    @Override
    public List<IngredientBatch> findAll() throws RemoteException {
        return ingredientBatchDAO.findAll();
    }

    @Override
    public List<IngredientBatch> findByIngredientId(Integer ingredientId) throws RemoteException {
        return ingredientBatchDAO.findByIngredientId(ingredientId);
    }

    @Override
    public List<IngredientBatch> findExpiringSoon(int daysBeforeExpire) throws RemoteException {
        return ingredientBatchDAO.findExpiringSoon(daysBeforeExpire);
    }

    @Override
    public List<IngredientBatch> findLowStock(int threshold) throws RemoteException {
        return ingredientBatchDAO.findLowStock(threshold);
    }
}
