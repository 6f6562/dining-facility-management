package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.PurchaseOrderHeader;
import utils.JPAUtil;

import java.util.List;
import java.rmi.RemoteException;

public interface PurchaseOrderHeaderService extends GenericService<PurchaseOrderHeader, Integer> {
    List<PurchaseOrderHeader> findByVendorId(Integer vendorId) throws RemoteException;
    List<PurchaseOrderHeader> findByDateRange(String startDate, String endDate) throws RemoteException;
    List<PurchaseOrderHeader> findPendingOrders() throws RemoteException;
}
