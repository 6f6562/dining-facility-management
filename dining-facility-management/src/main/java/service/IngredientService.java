package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Ingredient;
import utils.JPAUtil;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;

public interface IngredientService extends GenericService<Ingredient, Integer> {
    Ingredient getTopIngredientByModelIdOrderByStockQuantity(int modelId) throws RemoteException;
}
