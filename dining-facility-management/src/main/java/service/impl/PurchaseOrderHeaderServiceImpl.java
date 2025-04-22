package service.impl;

import dao.PurchaseOrderHeaderDAO;
import model.PurchaseOrderHeader;
import service.PurchaseOrderHeaderService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PurchaseOrderHeaderServiceImpl extends UnicastRemoteObject implements PurchaseOrderHeaderService {
    private final PurchaseOrderHeaderDAO purchaseOrderHeaderDAO;

    public PurchaseOrderHeaderServiceImpl() throws RemoteException {
        super();
        this.purchaseOrderHeaderDAO = new PurchaseOrderHeaderDAO();
    }

    @Override
    public void create(PurchaseOrderHeader purchaseOrderHeader) throws RemoteException {
        purchaseOrderHeaderDAO.create(purchaseOrderHeader);
    }

    @Override
    public void update(PurchaseOrderHeader purchaseOrderHeader) throws RemoteException {
        purchaseOrderHeaderDAO.update(purchaseOrderHeader);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        purchaseOrderHeaderDAO.deleteById(id);
    }

    @Override
    public PurchaseOrderHeader findById(Integer id) throws RemoteException {
        return purchaseOrderHeaderDAO.findById(id);
    }

    @Override
    public List<PurchaseOrderHeader> findAll() throws RemoteException {
        return purchaseOrderHeaderDAO.findAll();
    }

    @Override
    public List<PurchaseOrderHeader> findByVendorId(Integer vendorId) throws RemoteException {
        return purchaseOrderHeaderDAO.findByVendorId(vendorId);
    }

    @Override
    public List<PurchaseOrderHeader> findByDateRange(String startDate, String endDate) throws RemoteException {
        return purchaseOrderHeaderDAO.findByDateRange(startDate, endDate);
    }

    @Override
    public List<PurchaseOrderHeader> findPendingOrders() throws RemoteException {
        return purchaseOrderHeaderDAO.findPendingOrders();
    }
}
