package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.port.IOrderRepository;

public class OrderService {
    private final IOrderRepository IOrderRepository;

    public OrderService(IOrderRepository iOrderRepository) {
        this.IOrderRepository = iOrderRepository;
    }
    public Order save(Order order) {
        System.out.println("Guardando orden: " + order);
        return this.IOrderRepository.save(order);
    }
    public Iterable<Order> findAll() {
        return this.IOrderRepository.findAll();
    }
    public Iterable<Order> findByUserId(Integer userId) {
        return this.IOrderRepository.findByUserId(userId);
    }
    public void updateStateById(Integer id, String state) {
        this.IOrderRepository.updateStateById(id,state);
    }
    public Order findById(Integer id) {
        return this.IOrderRepository.findById(id);
    }
}
