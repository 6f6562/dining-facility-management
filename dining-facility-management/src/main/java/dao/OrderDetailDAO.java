package dao;

import entity.JPAUtil;
import entity.OrderDetail;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class OrderDetailDAO {

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

    public OrderDetail findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(OrderDetail.class, id); // Tìm OrderDetail theo ID
        } finally {
            em.close();
        }
    }

    public List<OrderDetail> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from OrderDetail", OrderDetail.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

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
