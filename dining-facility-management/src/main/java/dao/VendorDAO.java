package dao;

import jakarta.persistence.TypedQuery;
import model.Vendor;

import java.util.List;

public class VendorDAO extends GenericDAOImpl<Vendor, Integer> {
    public VendorDAO() {
        super(Vendor.class);
    }

    // TODO: Custom methods here
    public List<Vendor> findByIngredientId(Integer ingredientId) {
        String jpql = "SELECT v FROM Vendor v JOIN v.ingredients i WHERE i.id = :ingredientId";
        TypedQuery<Vendor> query = em.createQuery(jpql, Vendor.class);
        query.setParameter("ingredientId", ingredientId);
        return query.getResultList();
    }

    public List<Vendor> findActiveVendors() {
        String jpql = "SELECT v FROM Vendor v WHERE v.status = 'ACTIVE'";
        TypedQuery<Vendor> query = em.createQuery(jpql, Vendor.class);
        return query.getResultList();
    }
}
