package org.resourcebridge.api.service;

import java.util.List;

public interface GenericService<T> {

    List<T> getAll();

    T getById(Long id);

    T save(T entity);

    void deleteById(Long id);
}
