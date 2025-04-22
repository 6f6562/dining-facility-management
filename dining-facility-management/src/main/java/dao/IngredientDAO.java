package dao;

import model.Ingredient;
import utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class IngredientDAO {

    /**
     * Create or Save a new Ingredient
     */
    public void save(Ingredient ingredient) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(ingredient); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Read: Find an Ingredient by ID
     */
    public Ingredient findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Ingredient.class, id); // Tìm Ingredient theo ID
        } finally {
            em.close();
        }
    }

    /**
     * Read: Get all Ingredients
     */
    public List<Ingredient> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from Ingredient", Ingredient.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing Ingredient
     */
    public void update(Ingredient ingredient) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(ingredient); // Cập nhật thông tin
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Delete an Ingredient by ID
     */
    public void delete(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            Ingredient ingredient = em.find(Ingredient.class, id);
            if (ingredient != null) {
                em.remove(ingredient); // Xóa thông tin
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
