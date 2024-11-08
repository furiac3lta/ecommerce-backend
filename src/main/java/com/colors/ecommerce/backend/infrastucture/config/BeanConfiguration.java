package com.colors.ecommerce.backend.infrastucture.config;

import com.colors.ecommerce.backend.application.*;
import com.colors.ecommerce.backend.domain.port.ICategoryRepository;
import com.colors.ecommerce.backend.domain.port.IOrderRepository;
import com.colors.ecommerce.backend.domain.port.IProductRepository;
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
    public OrderService orderService(IOrderRepository iOrderRepository) {
        return new OrderService(iOrderRepository);
    }
    @Bean
    public UploadFile uploadFile(){
        return new UploadFile();
    }
}
