package dao;

import jakarta.persistence.TypedQuery;
import model.OrderDetail;

import java.util.List;

public class OrderDetailDAO extends GenericDAOImpl<OrderDetail, Integer> {
    public OrderDetailDAO() {
        super(OrderDetail.class);
    }

    // TODO: Custom methods here
    /**
     * Tìm danh sách OrderDetail theo OrderHeader (ID đơn hàng)
     */
    public List<OrderDetail> findByOrderHeaderId(Integer orderHeaderId) {
        String jpql = "SELECT od FROM OrderDetail od WHERE od.orderHeader.id = :orderHeaderId";
        TypedQuery<OrderDetail> query = em.createQuery(jpql, OrderDetail.class);
        query.setParameter("orderHeaderId", orderHeaderId);
        return query.getResultList();
    }

    /**
     * Tìm danh sách OrderDetail theo Dish (món ăn)
     */
    public List<OrderDetail> findByDishId(Integer dishId) {
        String jpql = "SELECT od FROM OrderDetail od WHERE od.dish.id = :dishId";
        TypedQuery<OrderDetail> query = em.createQuery(jpql, OrderDetail.class);
        query.setParameter("dishId", dishId);
        return query.getResultList();
    }
}
