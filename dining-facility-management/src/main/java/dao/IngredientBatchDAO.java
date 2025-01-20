package dao;

import entity.IngredientBatch;
import entity.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class IngredientBatchDAO {

    public void save(IngredientBatch ingredientBatch) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(ingredientBatch); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public IngredientBatch findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(IngredientBatch.class, id); // Tìm IngredientBatch theo ID
        } finally {
            em.close();
        }
    }

    public List<IngredientBatch> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from IngredientBatch", IngredientBatch.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

    public void update(IngredientBatch ingredientBatch) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(ingredientBatch); // Cập nhật thông tin
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
            IngredientBatch ingredientBatch = em.find(IngredientBatch.class, id);
            if (ingredientBatch != null) {
                em.remove(ingredientBatch); // Xóa thông tin
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
