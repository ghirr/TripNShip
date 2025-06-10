package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.User;

import java.util.List;

public interface IService<T> {
    void add(T t);
    void update(T t);
    void delete(T t);
    List<T> getAll();
}
