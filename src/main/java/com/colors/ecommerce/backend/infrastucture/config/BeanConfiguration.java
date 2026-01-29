package com.colors.ecommerce.backend.infrastucture.config;

import com.colors.ecommerce.backend.application.*;
import com.colors.ecommerce.backend.domain.port.ICategoryRepository;
import com.colors.ecommerce.backend.domain.port.IOrderRepository;
import com.colors.ecommerce.backend.domain.port.IProductRepository;
import com.colors.ecommerce.backend.domain.port.IProductVariantRepository;
import com.colors.ecommerce.backend.domain.port.IStockMovementRepository;
import com.colors.ecommerce.backend.domain.port.IStockReservationRepository;
import com.colors.ecommerce.backend.domain.port.IUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public UserService userService(IUserRepository iUserRepository) {
        return new UserService(iUserRepository);
    }
    @Bean
    public CategoryService categoryService(ICategoryRepository  iCategoryRepository) {
        return new CategoryService(iCategoryRepository);
    }
    @Bean
    public ProductService productService(IProductRepository iProductRepository,CloudinaryUploadFile cloudinaryUploadFile) {
        return new ProductService(iProductRepository,cloudinaryUploadFile);
    }
    @Bean
    public OrderService orderService(IOrderRepository iOrderRepository,
                                     IProductVariantRepository productVariantRepository,
                                     IStockReservationRepository stockReservationRepository,
                                     IStockMovementRepository stockMovementRepository) {
        return new OrderService(iOrderRepository, productVariantRepository, stockReservationRepository, stockMovementRepository);
    }

    @Bean
    public UploadFile uploadFile(){
        return new UploadFile();
    }

    @Bean
    public RegistrationService registrationService(IUserRepository iUserRepository){
        return new RegistrationService(iUserRepository);
    }
}
