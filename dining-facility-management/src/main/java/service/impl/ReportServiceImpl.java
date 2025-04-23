package service.impl;

import dao.ReportDAO;
import model.Report;
import service.ReportService;

import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ReportServiceImpl extends UnicastRemoteObject implements ReportService {
    private final ReportDAO reportDAO;

    public ReportServiceImpl() throws RemoteException {
        super();
        this.reportDAO = new ReportDAO();
    }

    public ReportServiceImpl(ReportDAO reportDAO) throws RemoteException {
        super();
        this.reportDAO = reportDAO;
    }

    @Override
    public void create(Report report) throws RemoteException {
        reportDAO.create(report);
    }

    @Override
    public void update(Report report) throws RemoteException {
        reportDAO.update(report);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        reportDAO.deleteById(id);
    }

    @Override
    public Report findById(Integer id) throws RemoteException {
        return reportDAO.findById(id);
    }

    @Override
    public List<Report> findAll() throws RemoteException {
        return reportDAO.findAll();
    }

    @Override
    public List<Report> findByType(String type) throws RemoteException {
        return reportDAO.findByType(type);
    }

    @Override
    public List<Report> findByDateRange(String startDate, String endDate) throws RemoteException {
        return reportDAO.findByDateRange(startDate, endDate);
    }

    @Override
    public List<Report> findByStatus(String status) throws RemoteException {
        return reportDAO.findByStatus(status);
    }

    @Override
    public List<Report> findByPeriod(String period) throws RemoteException {
        return reportDAO.findByPeriod(period);
    }

    @Override
    public void exportReport(Integer reportId, String filePath) throws RemoteException {
        try {
            Report report = findById(reportId);
            if (report == null) {
                throw new RemoteException("Report not found");
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("Report Title: " + report.getTitle() + "\n");
                writer.write("Type: " + report.getType() + "\n");
                writer.write("Period: " + report.getPeriod() + "\n");
                writer.write("Generated Date: " + report.getGeneratedDate() + "\n");
                writer.write("Status: " + report.getStatus() + "\n\n");
                writer.write(report.getContent());
            }
        } catch (IOException e) {
            throw new RemoteException("Error exporting report: " + e.getMessage());
        }
    }
} 