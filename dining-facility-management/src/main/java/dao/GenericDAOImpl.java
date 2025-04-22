package dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import utils.JPAUtil;

import java.util.List;

public abstract class GenericDAOImpl<Entity, Key> implements GenericDAO<Entity, Key>{
    protected EntityManager em;
    private final Class<Entity> entityClass;

    public GenericDAOImpl(Class<Entity> entityClass) {
        this.em = JPAUtil.getEntityManager();
        this.entityClass = entityClass;
    }

    @Override
    public void create(Entity entity) {
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
    }

    @Override
    public void update(Entity entity) {
        try {
            em.getTransaction().begin();
            em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
    }

    @Override
    public void deleteById(Key key) {
        try {
            em.getTransaction().begin();
            Entity entity = em.find(entityClass, key);
            if (entity != null) em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
        }
    }

    @Override
    public Entity findById(Key key) {
        return em.find(entityClass, key);
    }

    @Override
    public List<Entity> findAll() {
        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        TypedQuery<Entity> query = em.createQuery(jpql, entityClass);
        return query.getResultList();
    }
}
