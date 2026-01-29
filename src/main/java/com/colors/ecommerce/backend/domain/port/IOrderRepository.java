package com.colors.ecommerce.backend.domain.port;

import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.model.OrderState;


public interface IOrderRepository {
    Order save(Order order);
    Order findById(Integer id);
    Iterable<Order> findAll();
    Iterable<Order> findByUserId(Integer userId);
    Order updateStateById(Integer id, OrderState state);
}
