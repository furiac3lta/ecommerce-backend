package com.colors.ecommerce.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.mercadopago.MercadoPagoConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = {"com.colors.ecommerce"})
public class BackendApplication {

	public static void main(String[] args) {
		MercadoPagoConfig.setAccessToken("TEST-4941031144210285-021319-fda61d9e23aa19ab267ac409335d3db9-48037141");
		SpringApplication.run(BackendApplication.class, args);


	}
	@Configuration
	public class WebConfiguration implements WebMvcConfigurer {

		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/**").allowedMethods("*");
		}
	}

}