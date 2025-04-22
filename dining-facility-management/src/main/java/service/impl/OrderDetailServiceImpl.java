package service.impl;

import dao.OrderDetailDAO;
import model.OrderDetail;
import service.OrderDetailService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class OrderDetailServiceImpl extends UnicastRemoteObject implements OrderDetailService {
    private final OrderDetailDAO orderDetailDAO;

    public OrderDetailServiceImpl() throws RemoteException {
        super();
        this.orderDetailDAO = new OrderDetailDAO();
    }

    @Override
    public void create(OrderDetail orderDetail) throws RemoteException {
        orderDetailDAO.create(orderDetail);
    }

    @Override
    public void update(OrderDetail orderDetail) throws RemoteException {
        orderDetailDAO.update(orderDetail);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        orderDetailDAO.deleteById(id);
    }

    @Override
    public OrderDetail findById(Integer id) throws RemoteException {
        return orderDetailDAO.findById(id);
    }

    @Override
    public List<OrderDetail> findAll() throws RemoteException {
        return orderDetailDAO.findAll();
    }

    @Override
    public List<OrderDetail> findByOrderHeaderId(Integer orderHeaderId) throws RemoteException {
        return orderDetailDAO.findByOrderHeaderId(orderHeaderId);
    }

    @Override
    public List<OrderDetail> findByDishId(Integer dishId) throws RemoteException {
        return orderDetailDAO.findByDishId(dishId);
    }
}
