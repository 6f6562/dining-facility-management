package service.impl;

import dao.DiningTableDAO;
import model.DiningTable;
import service.DiningTableService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class DiningTableServiceImpl extends UnicastRemoteObject implements DiningTableService {
    private final DiningTableDAO diningTableDAO;

    public DiningTableServiceImpl() throws RemoteException {
        super();
        this.diningTableDAO = new DiningTableDAO();
    }

    public DiningTableServiceImpl(DiningTableDAO diningTableDAO) throws RemoteException {
        super();
        this.diningTableDAO = diningTableDAO;
    }

    @Override
    public void create(DiningTable diningTable) throws RemoteException {
        diningTableDAO.create(diningTable);
    }

    @Override
    public void update(DiningTable diningTable) throws RemoteException {
        diningTableDAO.update(diningTable);
    }

    @Override
    public void deleteById(Integer id) throws RemoteException {
        diningTableDAO.deleteById(id);
    }

    @Override
    public DiningTable findById(Integer id) throws RemoteException {
        return diningTableDAO.findById(id);
    }

    @Override
    public List<DiningTable> findAll() throws RemoteException {
        return diningTableDAO.findAll();
    }
}
