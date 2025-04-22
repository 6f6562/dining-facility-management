package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.DiningTable;
import utils.JPAUtil;

import java.util.List;

public class DiningTableService {

    public void save(DiningTable diningTable) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(diningTable); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public DiningTable findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(DiningTable.class, id); // Tìm DiningTable theo ID
        } finally {
            em.close();
        }
    }

    public List<DiningTable> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from DiningTable", DiningTable.class).getResultList(); // Lấy tất cả các bàn
        } finally {
            em.close();
        }
    }

    public void update(DiningTable diningTable) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(diningTable); // Cập nhật bàn
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
            DiningTable diningTable = em.find(DiningTable.class, id);
            if (diningTable != null) {
                em.remove(diningTable); // Xóa bàn
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
