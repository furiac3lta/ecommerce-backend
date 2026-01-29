package com.colors.ecommerce.backend.infrastucture.adapter;

import com.colors.ecommerce.backend.domain.model.SizeGuide;
import com.colors.ecommerce.backend.domain.port.ISizeGuideRepository;
import com.colors.ecommerce.backend.infrastucture.mapper.SizeGuideMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SizeGuideCrudRepositoryImpl implements ISizeGuideRepository {
    private final SizeGuideMapper sizeGuideMapper;
    private final ISizeGuideCrudRepository sizeGuideCrudRepository;

    public SizeGuideCrudRepositoryImpl(SizeGuideMapper sizeGuideMapper, ISizeGuideCrudRepository sizeGuideCrudRepository) {
        this.sizeGuideMapper = sizeGuideMapper;
        this.sizeGuideCrudRepository = sizeGuideCrudRepository;
    }

    @Override
    public SizeGuide save(SizeGuide sizeGuide) {
        return sizeGuideMapper.toSizeGuide(sizeGuideCrudRepository.save(sizeGuideMapper.toSizeGuideEntity(sizeGuide)));
    }

    @Override
    public Iterable<SizeGuide> findAll() {
        return sizeGuideMapper.toSizeGuideList(sizeGuideCrudRepository.findAll());
    }

    @Override
    public SizeGuide findById(Integer id) {
        return sizeGuideMapper.toSizeGuide(sizeGuideCrudRepository.findById(id).orElseThrow(
                () -> new RuntimeException("SizeGuide with id " + id + " not found")
        ));
    }

    @Override
    public void deleteById(Integer id) {
        sizeGuideCrudRepository.deleteById(id);
    }
}
