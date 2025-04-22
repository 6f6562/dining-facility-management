package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.PurchaseOrderDetail;
import utils.JPAUtil;

import java.rmi.RemoteException;
import java.util.List;

public interface PurchaseOrderDetailService extends GenericService<PurchaseOrderDetail, Integer> {
    List<PurchaseOrderDetail> findByPurchaseOrderHeaderId(Integer purchaseOrderHeaderId) throws RemoteException;
    List<PurchaseOrderDetail> findByIngredientId(Integer ingredientId) throws RemoteException;
}
