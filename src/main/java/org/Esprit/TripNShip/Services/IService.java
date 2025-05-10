package org.Esprit.TripNShip.Services;

import java.util.List;

public interface IService<T> {
    void add(T t);
    void update(T t);
    boolean delete(T t);
    List<T> getAll();
}
