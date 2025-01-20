package dao;

import entity.DishPriceHistory;
import entity.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class DishPriceHistoryDAO {

    public void save(DishPriceHistory dishPriceHistory) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(dishPriceHistory); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public DishPriceHistory findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(DishPriceHistory.class, id); // Tìm DishPriceHistory theo ID
        } finally {
            em.close();
        }
    }

    public List<DishPriceHistory> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from DishPriceHistory", DishPriceHistory.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

    public void update(DishPriceHistory dishPriceHistory) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(dishPriceHistory); // Cập nhật thông tin
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
            DishPriceHistory dishPriceHistory = em.find(DishPriceHistory.class, id);
            if (dishPriceHistory != null) {
                em.remove(dishPriceHistory); // Xóa thông tin
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
