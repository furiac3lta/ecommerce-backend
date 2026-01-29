package com.colors.ecommerce.backend.infrastucture.mapper;

import com.colors.ecommerce.backend.domain.model.SizeGuide;
import com.colors.ecommerce.backend.infrastucture.entity.SizeGuideEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface SizeGuideMapper {
    @Mappings(
            {
                    @Mapping(source = "id", target = "id"),
                    @Mapping(source = "size", target = "size"),
                    @Mapping(source = "heightRecommendedCm", target = "heightRecommendedCm"),
                    @Mapping(source = "weightRecommendedKg", target = "weightRecommendedKg"),
                    @Mapping(source = "description", target = "description")
            }
    )
    SizeGuide toSizeGuide(SizeGuideEntity entity);

    Iterable<SizeGuide> toSizeGuideList(Iterable<SizeGuideEntity> entities);

    @InheritInverseConfiguration
    SizeGuideEntity toSizeGuideEntity(SizeGuide model);
}
