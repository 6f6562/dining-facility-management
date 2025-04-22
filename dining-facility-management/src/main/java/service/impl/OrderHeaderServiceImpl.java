package service.impl;

import dao.OrderHeaderDAO;
import model.OrderHeader;
import service.OrderHeaderService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class OrderHeaderServiceImpl extends UnicastRemoteObject implements OrderHeaderService {
    private final OrderHeaderDAO orderHeaderDAO;

    public OrderHeaderServiceImpl() throws RemoteException {
        super();
        this.orderHeaderDAO = new OrderHeaderDAO();
    }

    @Override
    public void create(OrderHeader orderHeader) throws RemoteException {
        orderHeaderDAO.create(orderHeader);
    }

    @Override
    public void update(OrderHeader orderHeader) throws RemoteException {
        orderHeaderDAO.update(orderHeader);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        orderHeaderDAO.deleteById(id);
    }

    @Override
    public OrderHeader findById(Integer id) throws RemoteException {
        return orderHeaderDAO.findById(id);
    }

    @Override
    public List<OrderHeader> findAll() throws RemoteException {
        return orderHeaderDAO.findAll();
    }

    @Override
    public List<OrderHeader> findByTableId(Integer tableId) throws RemoteException {
        return orderHeaderDAO.findByTableId(tableId);
    }

    @Override
    public List<OrderHeader> findByDateRange(String startDate, String endDate) throws RemoteException {
        return orderHeaderDAO.findByDateRange(startDate, endDate);
    }

    @Override
    public List<OrderHeader> findPendingOrders() throws RemoteException {
        return orderHeaderDAO.findPendingOrders();
    }
}
