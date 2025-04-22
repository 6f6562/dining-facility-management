package service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.OrderHeader;
import utils.JPAUtil;

import java.util.List;

public class OrderHeaderServiceImpl {

    /**
     * Create or Save a new OrderHeader
     */
    public void save(OrderHeader orderHeader) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(orderHeader); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Read: Find an OrderHeader by ID
     */
    public OrderHeader findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(OrderHeader.class, id); // Tìm OrderHeader theo ID
        } finally {
            em.close();
        }
    }

    /**
     * Read: Get all OrderHeaders
     */
    public List<OrderHeader> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from OrderHeader", OrderHeader.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing OrderHeader
     */
    public void update(OrderHeader orderHeader) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(orderHeader); // Cập nhật thông tin
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Delete an OrderHeader by ID
     */
    public void delete(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            OrderHeader orderHeader = em.find(OrderHeader.class, id);
            if (orderHeader != null) {
                em.remove(orderHeader); // Xóa thông tin
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
