package service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Dish;
import utils.JPAUtil;

import java.util.List;

public class DishServiceImpl {

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

    /**
     * Read: Find a Dish by ID
     */
    public Dish findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Dish.class, id); // Tìm Dish theo ID
        } finally {
            em.close();
        }
    }

    /**
     * Read: Get all Dishes
     */
    public List<Dish> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from Dish", Dish.class).getResultList(); // Lấy tất cả các món
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing Dish
     */
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

    /**
     * Delete a Dish by ID
     */
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
