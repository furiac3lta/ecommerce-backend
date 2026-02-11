package com.colors.ecommerce.backend.infrastucture.adapter;


import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.domain.model.SaleChannel;
import com.colors.ecommerce.backend.infrastucture.entity.OrderEntity;
import com.colors.ecommerce.backend.infrastucture.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface IOrderCrudRepository extends CrudRepository<OrderEntity, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE OrderEntity o SET o.orderState = :state WHERE o.id = :id")
    void updateStateById(Integer id, OrderState state);

    Iterable<OrderEntity> findByUserEntity(UserEntity userEntity);

    List<OrderEntity> findByOrderState(OrderState orderState);

    List<OrderEntity> findByOrderStateAndDateCreatedBetween(OrderState orderState, LocalDateTime from, LocalDateTime to);
    List<OrderEntity> findByOrderStateAndSaleChannelAndDateCreatedBetween(OrderState orderState, SaleChannel saleChannel, LocalDateTime from, LocalDateTime to);
    List<OrderEntity> findByDateCreatedBetween(LocalDateTime from, LocalDateTime to);
    long countBySaleChannel(SaleChannel saleChannel);
}
