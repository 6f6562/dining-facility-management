package service.impl;

import dao.DishPriceHistoryDAO;
import model.DishPriceHistory;
import service.DishPriceHistoryService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class DishPriceHistoryServiceImpl extends UnicastRemoteObject implements DishPriceHistoryService {

    private final DishPriceHistoryDAO dishPriceHistoryDAO;

    public DishPriceHistoryServiceImpl() throws RemoteException {
        super();
        this.dishPriceHistoryDAO = new DishPriceHistoryDAO();
    }

    @Override
    public void create(DishPriceHistory dishPriceHistory) throws RemoteException {
        dishPriceHistoryDAO.create(dishPriceHistory);
    }

    @Override
    public void update(DishPriceHistory dishPriceHistory) throws RemoteException {
        dishPriceHistoryDAO.update(dishPriceHistory);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        dishPriceHistoryDAO.deleteById(id);
    }

    @Override
    public DishPriceHistory findById(Integer id) throws RemoteException {
        return dishPriceHistoryDAO.findById(id);
    }

    @Override
    public List<DishPriceHistory> findAll() throws RemoteException {
        return dishPriceHistoryDAO.findAll();
    }

    @Override
    public List<DishPriceHistory> findByDishId(Integer dishId) throws RemoteException {
        return dishPriceHistoryDAO.findByDishId(dishId);
    }

    @Override
    public DishPriceHistory findLatestByDishId(Integer dishId) throws RemoteException {
        return dishPriceHistoryDAO.findLatestByDishId(dishId);
    }
}
