package service.impl;

import dao.PurchaseOrderDetailDAO;
import model.PurchaseOrderDetail;
import service.PurchaseOrderDetailService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PurchaseOrderDetailServiceImpl extends UnicastRemoteObject implements PurchaseOrderDetailService {
    private final PurchaseOrderDetailDAO purchaseOrderDetailDAO;

    public PurchaseOrderDetailServiceImpl() throws RemoteException {
        super();
        this.purchaseOrderDetailDAO = new PurchaseOrderDetailDAO();
    }

    public PurchaseOrderDetailServiceImpl(PurchaseOrderDetailDAO purchaseOrderDetailDAO) throws RemoteException {
        super();
        this.purchaseOrderDetailDAO = purchaseOrderDetailDAO;
    }

    @Override
    public void create(PurchaseOrderDetail purchaseOrderDetail) throws RemoteException {
        purchaseOrderDetailDAO.create(purchaseOrderDetail);
    }

    @Override
    public void update(PurchaseOrderDetail purchaseOrderDetail) throws RemoteException {
        purchaseOrderDetailDAO.update(purchaseOrderDetail);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        purchaseOrderDetailDAO.deleteById(id);
    }

    @Override
    public PurchaseOrderDetail findById(Integer id) throws RemoteException {
        return purchaseOrderDetailDAO.findById(id);
    }

    @Override
    public List<PurchaseOrderDetail> findAll() throws RemoteException {
        return purchaseOrderDetailDAO.findAll();
    }

    @Override
    public List<PurchaseOrderDetail> findByPurchaseOrderHeaderId(Integer purchaseOrderHeaderId) throws RemoteException {
        return purchaseOrderDetailDAO.findByPurchaseOrderHeaderId(purchaseOrderHeaderId);
    }

    @Override
    public List<PurchaseOrderDetail> findByIngredientId(Integer ingredientId) throws RemoteException {
        return purchaseOrderDetailDAO.findByIngredientId(ingredientId);
    }
}
