package dao;

import jakarta.persistence.TypedQuery;
import model.Payment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentDAO extends GenericDAOImpl<Payment, Integer> {
    public PaymentDAO() {
        super(Payment.class);
    }

    /**
     * Tìm các thanh toán theo mã đơn hàng
     */
    public List<Payment> findByOrderId(Integer orderId) {
        String jpql = "SELECT p FROM Payment p WHERE p.orderHeader.id = :orderId";
        TypedQuery<Payment> query = em.createQuery(jpql, Payment.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }

    /**
     * Tìm các thanh toán theo khoảng ngày
     */
    public List<Payment> findByDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        String jpql = "SELECT p FROM Payment p WHERE p.createdAt BETWEEN :start AND :end";
        TypedQuery<Payment> query = em.createQuery(jpql, Payment.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

    /**
     * Tìm các thanh toán theo phương thức thanh toán
     */
    public List<Payment> findByPaymentMethod(String paymentMethod) {
        String jpql = "SELECT p FROM Payment p WHERE p.paymentMethod = :paymentMethod";
        TypedQuery<Payment> query = em.createQuery(jpql, Payment.class);
        query.setParameter("paymentMethod", paymentMethod);
        return query.getResultList();
    }

    /**
     * Tìm các thanh toán theo trạng thái
     */
    public List<Payment> findByStatus(String status) {
        String jpql = "SELECT p FROM Payment p WHERE p.status = :status";
        TypedQuery<Payment> query = em.createQuery(jpql, Payment.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
} 