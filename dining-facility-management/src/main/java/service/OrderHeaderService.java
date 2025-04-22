package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.OrderHeader;
import utils.JPAUtil;

import java.util.List;
import java.rmi.RemoteException;

public interface OrderHeaderService extends GenericService<OrderHeader, Integer> {
    List<OrderHeader> findByTableId(Integer tableId) throws RemoteException;
    List<OrderHeader> findByDateRange(String startDate, String endDate) throws RemoteException;
    List<OrderHeader> findPendingOrders() throws RemoteException;
}
