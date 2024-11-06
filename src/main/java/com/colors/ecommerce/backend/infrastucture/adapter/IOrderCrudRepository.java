package com.colors.ecommerce.backend.infrastucture.adapter;


import com.colors.ecommerce.backend.domain.model.OrderState;
import com.colors.ecommerce.backend.infrastucture.entity.OrderEntity;
import com.colors.ecommerce.backend.infrastucture.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface IOrderCrudRepository extends CrudRepository<OrderEntity, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE OrderEntity o SET o.orderState = :state WHERE o.id = :id")
    void updateStateById(Integer id, OrderState state);

    Iterable<OrderEntity> findByUserEntity(UserEntity userEntity);
}
