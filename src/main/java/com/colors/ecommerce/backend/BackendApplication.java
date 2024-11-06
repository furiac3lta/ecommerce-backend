package com.colors.ecommerce.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.mercadopago.MercadoPagoConfig;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		MercadoPagoConfig.setAccessToken("TEST-4941031144210285-021319-fda61d9e23aa19ab267ac409335d3db9-48037141");
		SpringApplication.run(BackendApplication.class, args);
	}

}