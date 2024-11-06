package com.colors.ecommerce.backend.infrastucture.adapter;


import com.colors.ecommerce.backend.infrastucture.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface IUserCrudRepository extends CrudRepository<UserEntity, Integer> {

}
