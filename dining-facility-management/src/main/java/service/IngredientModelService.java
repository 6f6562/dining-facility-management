package service;

import model.IngredientModel;
import java.rmi.RemoteException;
import java.util.List;

public interface IngredientModelService extends GenericService<IngredientModel, Integer> {
    List<IngredientModel> findByDishId(Integer dishId) throws RemoteException;
}
