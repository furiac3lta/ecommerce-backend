package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.domain.model.Order;
import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.port.IOrderRepository;
import com.colors.ecommerce.backend.infrastucture.entity.OrderEntity;
import com.colors.ecommerce.backend.infrastucture.entity.UserEntity;
import com.colors.ecommerce.backend.infrastucture.mapper.IOrderMapper;
import org.springframework.stereotype.Repository;

@Repository
public class OrderCrudRepositoryImpl implements IOrderRepository {
   private final IOrderMapper iOrderMapper;
   private final IOrderCrudRepository iOrderCrudRepository;

    public OrderCrudRepositoryImpl(IOrderMapper iOrderMapper, IOrderCrudRepository iOrderCrudRepository) {
        this.iOrderMapper = iOrderMapper;
        this.iOrderCrudRepository = iOrderCrudRepository;
    }


    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = iOrderMapper.toOrderEntity(order);
        orderEntity.getOrderProducts().forEach(
                orderProductEntity -> orderProductEntity.setOrderEntity(orderEntity)
        );
        return iOrderMapper.toOrder(iOrderCrudRepository.save(orderEntity));
    }

    @Override
    public Order findById(Integer id) {
        return iOrderMapper.toOrder(iOrderCrudRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Order with id: " + id + " not found")
        ));
    }

    @Override
    public Iterable<Order> findAll() {
        return iOrderMapper.toOrderList((iOrderCrudRepository.findAll()));
    }

    @Override
    public Iterable<Order> findByUserId(Integer userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        return iOrderMapper.toOrderList(iOrderCrudRepository.findByUserEntity(userEntity));
    }

    @Override
    public void updateStateById(Integer id, String state) {
        if(state.equals(OrderState.CANCELLED)){
            iOrderCrudRepository.updateStateById(id, OrderState.CANCELLED);

        }else{
            iOrderCrudRepository.updateStateById(id, OrderState.CONFIRMED);
        }
    }
}
