package dao;

import entity.JPAUtil;
import entity.Recipe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class RecipeDAO {

    public void save(Recipe recipe) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(recipe); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Recipe findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Recipe.class, id); // Tìm Recipe theo ID
        } finally {
            em.close();
        }
    }

    public List<Recipe> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from Recipe", Recipe.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

    public void update(Recipe recipe) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(recipe); // Cập nhật thông tin
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
            Recipe recipe = em.find(Recipe.class, id);
            if (recipe != null) {
                em.remove(recipe); // Xóa thông tin
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
