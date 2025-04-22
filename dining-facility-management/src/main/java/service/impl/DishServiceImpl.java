package service.impl;

import dao.DishDAO;
import model.Dish;
import service.DishService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class DishServiceImpl extends UnicastRemoteObject implements DishService {
    private final DishDAO dishDAO;

    public DishServiceImpl() throws RemoteException {
        super();
        this.dishDAO = new DishDAO();
    }

    @Override
    public void create(Dish dish) throws RemoteException {
        dishDAO.create(dish);
    }

    @Override
    public void update(Dish dish) throws RemoteException {
        dishDAO.update(dish);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        dishDAO.deleteById(id);
    }

    @Override
    public Dish findById(Integer id) throws RemoteException {
        return dishDAO.findById(id);
    }

    @Override
    public List<Dish> findAll() throws RemoteException {
        return dishDAO.findAll();
    }

    @Override
    public List<Dish> findByCategory(String category) throws RemoteException {
        return dishDAO.findByCategory(category);
    }

    @Override
    public List<Dish> findAvailableDishes() throws RemoteException {
        return dishDAO.findAvailableDishes();
    }

    @Override
    public List<Dish> findDishesByPriceRange(double minPrice, double maxPrice) throws RemoteException {
        return dishDAO.findDishesByPriceRange(minPrice, maxPrice);
    }
}
