package dao;

import jakarta.persistence.TypedQuery;
import model.PurchaseOrderDetail;

import java.util.List;

public class PurchaseOrderDetailDAO extends GenericDAOImpl<PurchaseOrderDetail, Integer> {
    public PurchaseOrderDetailDAO() {
        super(PurchaseOrderDetail.class);
    }

    // TODO: Custom methods here
    public List<PurchaseOrderDetail> findByPurchaseOrderHeaderId(Integer headerId) {
        String jpql = "SELECT pod FROM PurchaseOrderDetail pod WHERE pod.purchaseOrderHeader.id = :headerId";
        TypedQuery<PurchaseOrderDetail> query = em.createQuery(jpql, PurchaseOrderDetail.class);
        query.setParameter("headerId", headerId);
        return query.getResultList();
    }

    public List<PurchaseOrderDetail> findByIngredientId(Integer ingredientId) {
        String jpql = "SELECT pod FROM PurchaseOrderDetail pod WHERE pod.ingredient.id = :ingredientId";
        TypedQuery<PurchaseOrderDetail> query = em.createQuery(jpql, PurchaseOrderDetail.class);
        query.setParameter("ingredientId", ingredientId);
        return query.getResultList();
    }
}
