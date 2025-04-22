package dao;

import model.PurchaseOrderHeader;
import utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class PurchaseOrderHeaderDAO {

    /**
     * Create or Save a new PurchaseOrderHeader
     */
    public void save(PurchaseOrderHeader purchaseOrderHeader) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(purchaseOrderHeader); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Read: Find a PurchaseOrderHeader by ID
     */
    public PurchaseOrderHeader findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(PurchaseOrderHeader.class, id); // Tìm PurchaseOrderHeader theo ID
        } finally {
            em.close();
        }
    }

    /**
     * Read: Get all PurchaseOrderHeaders
     */
    public List<PurchaseOrderHeader> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from PurchaseOrderHeader", PurchaseOrderHeader.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing PurchaseOrderHeader
     */
    public void update(PurchaseOrderHeader purchaseOrderHeader) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(purchaseOrderHeader); // Cập nhật thông tin
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Delete a PurchaseOrderHeader by ID
     */
    public void delete(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            PurchaseOrderHeader purchaseOrderHeader = em.find(PurchaseOrderHeader.class, id);
            if (purchaseOrderHeader != null) {
                em.remove(purchaseOrderHeader); // Xóa thông tin
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
