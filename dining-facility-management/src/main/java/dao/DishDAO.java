package dao;

import entity.Dish;
import entity.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class DishDAO {

    public void save(Dish dish) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(dish); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Dish findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Dish.class, id); // Tìm Dish theo ID
        } finally {
            em.close();
        }
    }

    public List<Dish> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from Dish", Dish.class).getResultList(); // Lấy tất cả các món
        } finally {
            em.close();
        }
    }

    public void update(Dish dish) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(dish); // Cập nhật món ăn
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
            Dish dish = em.find(Dish.class, id);
            if (dish != null) {
                em.remove(dish); // Xóa món ăn
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
