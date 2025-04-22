package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Dish;
import utils.JPAUtil;

import java.util.List;
import java.rmi.RemoteException;

public interface DishService extends GenericService<Dish, Integer> {

    List<Dish> findByCategory(String category) throws RemoteException;
    List<Dish> findAvailableDishes() throws RemoteException;
    List<Dish> findDishesByPriceRange(double minPrice, double maxPrice) throws RemoteException;
}
