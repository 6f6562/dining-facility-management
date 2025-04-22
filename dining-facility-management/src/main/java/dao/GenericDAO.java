package dao;


import java.util.List;

public interface GenericDAO<Entity, Key> {

    void create(Entity entity);

    void update(Entity entity);

    void deleteById(Key key);

    List<Entity> findAll();

    Entity findById(Key key);
}