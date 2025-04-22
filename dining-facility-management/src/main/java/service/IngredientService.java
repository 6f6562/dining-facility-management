package service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Ingredient;
import utils.JPAUtil;

import java.util.List;

public interface IngredientService extends GenericService<Ingredient, Integer> {

}
