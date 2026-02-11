package com.colors.ecommerce.backend.infrastucture.config;

import com.colors.ecommerce.backend.application.*;
import com.colors.ecommerce.backend.domain.port.ICategoryRepository;
import com.colors.ecommerce.backend.domain.port.IOrderRepository;
import com.colors.ecommerce.backend.domain.port.IProductRepository;
import com.colors.ecommerce.backend.domain.port.IProductVariantRepository;
import com.colors.ecommerce.backend.domain.port.IShipmentRepository;
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
    public CategoryService categoryService(ICategoryRepository iCategoryRepository, IProductRepository iProductRepository) {
        return new CategoryService(iCategoryRepository, iProductRepository);
    }
    @Bean
    public ProductService productService(IProductRepository iProductRepository, CloudinaryUploadFile cloudinaryUploadFile, CategoryService categoryService) {
        return new ProductService(iProductRepository, cloudinaryUploadFile, categoryService);
    }
    @Bean
    public OrderService orderService(IOrderRepository iOrderRepository,
                                     IProductVariantRepository productVariantRepository,
                                     IProductRepository productRepository,
                                     CategoryService categoryService,
                                     IStockReservationRepository stockReservationRepository,
                                     IStockMovementRepository stockMovementRepository,
                                     IShipmentRepository shipmentRepository) {
        return new OrderService(iOrderRepository, productVariantRepository, productRepository, categoryService, stockReservationRepository, stockMovementRepository, shipmentRepository);
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
