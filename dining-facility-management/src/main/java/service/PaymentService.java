package service;

import model.Payment;
import java.rmi.RemoteException;
import java.util.List;

public interface PaymentService extends GenericService<Payment, Integer> {
    List<Payment> findByOrderId(Integer orderId) throws RemoteException;
    List<Payment> findByDateRange(String startDate, String endDate) throws RemoteException;
    List<Payment> findByPaymentMethod(String paymentMethod) throws RemoteException;
    List<Payment> findByStatus(String status) throws RemoteException;
} 