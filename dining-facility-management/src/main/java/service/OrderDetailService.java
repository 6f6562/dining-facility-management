package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.OrderDetail;
import utils.JPAUtil;

import java.util.List;

public class OrderDetailService {

    /**
     * Create or Save a new OrderDetail
     */
    public void save(OrderDetail orderDetail) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(orderDetail); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Read: Find an OrderDetail by ID
     */
    public OrderDetail findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(OrderDetail.class, id); // Tìm OrderDetail theo ID
        } finally {
            em.close();
        }
    }

    /**
     * Read: Get all OrderDetails
     */
    public List<OrderDetail> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from OrderDetail", OrderDetail.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing OrderDetail
     */
    public void update(OrderDetail orderDetail) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(orderDetail); // Cập nhật thông tin
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Delete an OrderDetail by ID
     */
    public void delete(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            OrderDetail orderDetail = em.find(OrderDetail.class, id);
            if (orderDetail != null) {
                em.remove(orderDetail); // Xóa thông tin
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
