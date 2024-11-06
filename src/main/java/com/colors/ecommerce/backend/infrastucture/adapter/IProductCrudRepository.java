package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.infrastucture.entity.ProductEntity;
import org.springframework.data.repository.CrudRepository;

public interface IProductCrudRepository  extends CrudRepository<ProductEntity, Integer> {

}

