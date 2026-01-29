package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.SizeGuide;
import com.colors.ecommerce.backend.domain.port.ISizeGuideRepository;
import org.springframework.stereotype.Service;

@Service
public class SizeGuideService {
    private final ISizeGuideRepository sizeGuideRepository;

    public SizeGuideService(ISizeGuideRepository sizeGuideRepository) {
        this.sizeGuideRepository = sizeGuideRepository;
    }

    public SizeGuide save(SizeGuide sizeGuide) {
        return sizeGuideRepository.save(sizeGuide);
    }

    public Iterable<SizeGuide> findAll() {
        return sizeGuideRepository.findAll();
    }

    public SizeGuide findById(Integer id) {
        return sizeGuideRepository.findById(id);
    }

    public void deleteById(Integer id) {
        sizeGuideRepository.deleteById(id);
    }
}
