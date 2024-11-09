package com.colors.ecommerce.backend.infrastucture.adapter;


import com.colors.ecommerce.backend.infrastucture.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IUserCrudRepository extends CrudRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);

}
