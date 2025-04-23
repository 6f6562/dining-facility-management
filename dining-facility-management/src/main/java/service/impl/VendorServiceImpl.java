package service.impl;

import dao.VendorDAO;
import model.Vendor;
import service.VendorService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class VendorServiceImpl extends UnicastRemoteObject implements VendorService {
    private final VendorDAO vendorDAO;

    public VendorServiceImpl() throws RemoteException {
        super();
        this.vendorDAO = new VendorDAO();
    }

    public VendorServiceImpl(VendorDAO vendorDAO) throws RemoteException {
        super();
        this.vendorDAO = vendorDAO;
    }

    @Override
    public void create(Vendor vendor) throws RemoteException {
        vendorDAO.create(vendor);
    }

    @Override
    public void update(Vendor vendor) throws RemoteException {
        vendorDAO.update(vendor);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        vendorDAO.deleteById(id);
    }

    @Override
    public Vendor findById(Integer id) throws RemoteException {
        return vendorDAO.findById(id);
    }

    @Override
    public List<Vendor> findAll() throws RemoteException {
        return vendorDAO.findAll();
    }

    @Override
    public List<Vendor> findByIngredientId(Integer ingredientId) throws RemoteException {
        return vendorDAO.findByIngredientId(ingredientId);
    }

    @Override
    public List<Vendor> findActiveVendors() throws RemoteException {
        return vendorDAO.findActiveVendors();
    }
}
