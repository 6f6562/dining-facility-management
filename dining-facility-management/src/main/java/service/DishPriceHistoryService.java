package service;

import model.DishPriceHistory;
import java.rmi.RemoteException;
import java.util.List;

public interface DishPriceHistoryService extends GenericService<DishPriceHistory, Integer> {
    List<DishPriceHistory> findByDishId(Integer dishId) throws RemoteException;
    DishPriceHistory findLatestByDishId(Integer dishId) throws RemoteException;
}
