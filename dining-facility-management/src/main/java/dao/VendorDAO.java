package dao;

import entity.JPAUtil;
import entity.Vendor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class VendorDAO {

    public void save(Vendor vendor) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(vendor); // Lưu mới
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Vendor findById(int id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Vendor.class, id); // Tìm Vendor theo ID
        } finally {
            em.close();
        }
    }

    public List<Vendor> findAll() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery("from Vendor", Vendor.class).getResultList(); // Lấy tất cả
        } finally {
            em.close();
        }
    }

    public void update(Vendor vendor) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.merge(vendor); // Cập nhật thông tin
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
            Vendor vendor = em.find(Vendor.class, id);
            if (vendor != null) {
                em.remove(vendor); // Xóa thông tin
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
