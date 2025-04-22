package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.OrderDetail;
import utils.JPAUtil;

import java.util.List;
import java.rmi.RemoteException;

public interface OrderDetailService extends GenericService<OrderDetail, Integer> {
    List<OrderDetail> findByOrderHeaderId(Integer orderHeaderId) throws RemoteException;
    List<OrderDetail> findByDishId(Integer dishId) throws RemoteException;
}
