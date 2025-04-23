package service.impl;

import dao.PaymentDAO;
import model.Payment;
import service.PaymentService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class PaymentServiceImpl extends UnicastRemoteObject implements PaymentService {
    private final PaymentDAO paymentDAO;

    public PaymentServiceImpl() throws RemoteException {
        super();
        this.paymentDAO = new PaymentDAO();
    }

    @Override
    public void create(Payment payment) throws RemoteException {
        paymentDAO.create(payment);
    }

    @Override
    public void update(Payment payment) throws RemoteException {
        paymentDAO.update(payment);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        paymentDAO.deleteById(id);
    }

    @Override
    public Payment findById(Integer id) throws RemoteException {
        return paymentDAO.findById(id);
    }

    @Override
    public List<Payment> findAll() throws RemoteException {
        return paymentDAO.findAll();
    }

    @Override
    public List<Payment> findByOrderId(Integer orderId) throws RemoteException {
        return paymentDAO.findByOrderId(orderId);
    }

    @Override
    public List<Payment> findByDateRange(String startDate, String endDate) throws RemoteException {
        return paymentDAO.findByDateRange(startDate, endDate);
    }

    @Override
    public List<Payment> findByPaymentMethod(String paymentMethod) throws RemoteException {
        return paymentDAO.findByPaymentMethod(paymentMethod);
    }

    @Override
    public List<Payment> findByStatus(String status) throws RemoteException {
        return paymentDAO.findByStatus(status);
    }
} 