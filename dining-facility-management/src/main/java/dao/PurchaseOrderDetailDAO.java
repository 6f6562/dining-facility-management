package dao;

import model.PurchaseOrderDetail;
import utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class PurchaseOrderDetailDAO {

    /**
     * Create or Save a new PurchaseOrderDetail
     */
    public void save(PurchaseOrderDetail purchaseOrderDetail) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(purchaseOrderDetail); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Read: Find a PurchaseOrderDetail by ID
     */
    public PurchaseOrderDetail findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(PurchaseOrderDetail.class, id); // Tìm PurchaseOrderDetail theo ID
        } finally {
            em.close();
        }
    }

    /**
     * Read: Get all PurchaseOrderDetails
     */
    public List<PurchaseOrderDetail> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from PurchaseOrderDetail", PurchaseOrderDetail.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

    /**
     * Update an existing PurchaseOrderDetail
     */
    public void update(PurchaseOrderDetail purchaseOrderDetail) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(purchaseOrderDetail); // Cập nhật thông tin
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Delete a PurchaseOrderDetail by ID
     */
    public void delete(int id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            PurchaseOrderDetail purchaseOrderDetail = em.find(PurchaseOrderDetail.class, id);
            if (purchaseOrderDetail != null) {
                em.remove(purchaseOrderDetail); // Xóa thông tin
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
