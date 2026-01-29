package com.colors.ecommerce.backend.domain.port;

import com.colors.ecommerce.backend.domain.model.SizeGuide;

public interface ISizeGuideRepository {
    SizeGuide save(SizeGuide sizeGuide);
    Iterable<SizeGuide> findAll();
    SizeGuide findById(Integer id);
    void deleteById(Integer id);
}
