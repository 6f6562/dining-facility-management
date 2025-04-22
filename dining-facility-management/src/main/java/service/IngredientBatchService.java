package service;

import model.IngredientBatch;
import java.rmi.RemoteException;
import java.util.List;

public interface IngredientBatchService extends GenericService<IngredientBatch, Integer> {
    List<IngredientBatch> findByIngredientId(Integer ingredientId) throws RemoteException;
    List<IngredientBatch> findExpiringSoon(int daysBeforeExpire) throws RemoteException;
    List<IngredientBatch> findLowStock(int threshold) throws RemoteException;
}
