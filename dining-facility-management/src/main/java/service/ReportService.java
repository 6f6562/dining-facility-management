package service;

import model.Report;
import java.rmi.RemoteException;
import java.util.List;

public interface ReportService extends GenericService<Report, Integer> {
    List<Report> findByType(String type) throws RemoteException;
    List<Report> findByDateRange(String startDate, String endDate) throws RemoteException;
    List<Report> findByStatus(String status) throws RemoteException;
    List<Report> findByPeriod(String period) throws RemoteException;
    void exportReport(Integer reportId, String filePath) throws RemoteException;
} 