package dao;

import jakarta.persistence.TypedQuery;
import model.OrderHeader;
import model.PurchaseOrderHeader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PurchaseOrderHeaderDAO extends GenericDAOImpl<PurchaseOrderHeader, Integer> {
    public PurchaseOrderHeaderDAO() {
        super(PurchaseOrderHeader.class);
    }

    // TODO: Custom methods here
    /**
     * Tìm các phiếu nhập hàng theo nhà cung cấp
     */
    public List<PurchaseOrderHeader> findByVendorId(Integer vendorId) {
        String jpql = "SELECT p FROM PurchaseOrderHeader p WHERE p.vendor.id = :vendorId";
        TypedQuery<PurchaseOrderHeader> query = em.createQuery(jpql, PurchaseOrderHeader.class);
        query.setParameter("vendorId", vendorId);
        return query.getResultList();
    }

    /**
     * Tìm các phiếu nhập hàng theo khoảng ngày đặt hàng
     */
    public List<PurchaseOrderHeader> findByDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        String jpql = "SELECT p FROM PurchaseOrderHeader p WHERE p.orderDate BETWEEN :start AND :end";
        TypedQuery<PurchaseOrderHeader> query = em.createQuery(jpql, PurchaseOrderHeader.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

    /**
     * Tìm các phiếu đang ở trạng thái chờ xử lý (giả định là 'PENDING')
     */
    public List<PurchaseOrderHeader> findPendingOrders() {
        String jpql = "SELECT p FROM PurchaseOrderHeader p WHERE p.status = 'PENDING'";
        TypedQuery<PurchaseOrderHeader> query = em.createQuery(jpql, PurchaseOrderHeader.class);
        return query.getResultList();
    }
}
