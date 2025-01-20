package dao;

import entity.IngredientModel;
import entity.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class IngredientModelDAO {

    public void save(IngredientModel ingredientModel) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(ingredientModel); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public IngredientModel findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(IngredientModel.class, id); // Tìm IngredientModel theo ID
        } finally {
            em.close();
        }
    }

    public List<IngredientModel> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from IngredientModel", IngredientModel.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

    public void update(IngredientModel ingredientModel) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(ingredientModel); // Cập nhật thông tin
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
            IngredientModel ingredientModel = em.find(IngredientModel.class, id);
            if (ingredientModel != null) {
                em.remove(ingredientModel); // Xóa thông tin
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
