package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface GenericService<Entity, Key> extends Remote {
    void create(Entity entity) throws RemoteException;
    void update(Entity entity) throws RemoteException;
    void deleteById(Key key) throws RemoteException;
    Entity findById(Key key) throws RemoteException;
    List<Entity> findAll() throws RemoteException;
}
