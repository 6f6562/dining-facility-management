package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Vendor;
import utils.JPAUtil;

import java.rmi.RemoteException;
import java.util.List;

public interface VendorService extends GenericService<Vendor, Integer> {
    List<Vendor> findByIngredientId(Integer ingredientId) throws RemoteException;

    List<Vendor> findActiveVendors() throws RemoteException;
}
