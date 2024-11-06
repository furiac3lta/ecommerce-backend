package com.colors.ecommerce.backend.domain.port;

import com.colors.ecommerce.backend.domain.model.Order;


public interface IOrderRepository {
    Order save(Order order);
    Order findById(Integer id);
    Iterable<Order> findAll();
    Iterable<Order> findByUserId(Integer userId);
    void updateStateById(Integer id, String state);
}
