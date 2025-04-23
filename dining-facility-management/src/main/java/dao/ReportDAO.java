package dao;

import jakarta.persistence.TypedQuery;
import model.Report;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportDAO extends GenericDAOImpl<Report, Integer> {
    public ReportDAO() {
        super(Report.class);
    }

    /**
     * Tìm các báo cáo theo loại
     */
    public List<Report> findByType(String type) {
        String jpql = "SELECT r FROM Report r WHERE r.type = :type";
        TypedQuery<Report> query = em.createQuery(jpql, Report.class);
        query.setParameter("type", type);
        return query.getResultList();
    }

    /**
     * Tìm các báo cáo theo khoảng thời gian
     */
    public List<Report> findByDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        String jpql = "SELECT r FROM Report r WHERE r.generatedDate BETWEEN :start AND :end";
        TypedQuery<Report> query = em.createQuery(jpql, Report.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        return query.getResultList();
    }

    /**
     * Tìm các báo cáo theo trạng thái
     */
    public List<Report> findByStatus(String status) {
        String jpql = "SELECT r FROM Report r WHERE r.status = :status";
        TypedQuery<Report> query = em.createQuery(jpql, Report.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    /**
     * Tìm các báo cáo theo chu kỳ
     */
    public List<Report> findByPeriod(String period) {
        String jpql = "SELECT r FROM Report r WHERE r.period = :period";
        TypedQuery<Report> query = em.createQuery(jpql, Report.class);
        query.setParameter("period", period);
        return query.getResultList();
    }
} 