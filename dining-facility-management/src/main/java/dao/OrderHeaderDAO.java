package dao;

import jakarta.persistence.TypedQuery;
import model.OrderHeader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderHeaderDAO extends GenericDAOImpl<OrderHeader, Integer> {
    public OrderHeaderDAO() {
        super(OrderHeader.class);
    }

    // TODO: Custom methods here
    public List<OrderHeader> findByTableId(Integer tableId) {
        String jpql = "SELECT o FROM OrderHeader o WHERE o.diningTable.id = :tableId";
        TypedQuery<OrderHeader> query = em.createQuery(jpql, OrderHeader.class);
        query.setParameter("tableId", tableId);
        return query.getResultList();
    }

    public List<OrderHeader> findByDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        String jpql = "SELECT o FROM OrderHeader o WHERE o.orderDate BETWEEN :start AND :end";
        TypedQuery<OrderHeader> query = em.createQuery(jpql, OrderHeader.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

    public List<OrderHeader> findPendingOrders() {
        String jpql = "SELECT o FROM OrderHeader o WHERE o.status = 'PENDING'";
        TypedQuery<OrderHeader> query = em.createQuery(jpql, OrderHeader.class);
        return query.getResultList();
    }
}
