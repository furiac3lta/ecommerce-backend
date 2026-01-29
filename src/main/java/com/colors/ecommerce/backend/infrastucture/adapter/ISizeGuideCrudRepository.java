package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.infrastucture.entity.SizeGuideEntity;
import org.springframework.data.repository.CrudRepository;

public interface ISizeGuideCrudRepository extends CrudRepository<SizeGuideEntity, Integer> {
}
