//package com.colors.ecommerce.backend.infrastucture.rest;
//
//import com.colors.ecommerce.backend.application.ProductService;
//import com.colors.ecommerce.backend.domain.model.Product;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.math.BigDecimal;
//
//@Controller
//@RequestMapping("api/v1/admin/products")
//@Slf4j
//@AllArgsConstructor
//@CrossOrigin(origins = {"http://localhost:4200", "https://ecommerce-angular-five.vercel.app"})
//public class ProductController {
//    private final ProductService productService;
//
//    @PostMapping
//    public ResponseEntity<Product> save(@RequestParam("id") Integer id,
//                                        @RequestParam("code") String code,
//                                        @RequestParam("name") String name,
//                                        @RequestParam("description") String description,
//                                        @RequestParam("price") BigDecimal price,
//                                        @RequestParam("urlImage") String urlImage,
//                                        @RequestParam("userId") Integer userId,
//                                        @RequestParam("categoryId") Integer categoryId,
//                                        @RequestParam(value = "image", required = false) MultipartFile multipartFile
//                                        ) throws IOException {
//        Product product = new Product();
//        product.setId(id);
//        product.setCode(code);
//        product.setName(name);
//        product.setDescription(description);
//        product.setPrice(price);
//        product.setUrlImage(urlImage);
//        product.setUserId(userId);
//        product.setCategoryId(categoryId);
//
//
//        log.info("Saving product {}", product.getName());
//        return new ResponseEntity<>(productService.save(product, multipartFile), HttpStatus.CREATED);
//    }
//    @GetMapping
//    public ResponseEntity<Iterable<Product>> findAll() {
//        log.info("Finding all products");
//        return new ResponseEntity<>(productService.findAll(), HttpStatus.OK);
//    }
//    @GetMapping("/{id}")
//    public ResponseEntity<Product> findById(@PathVariable("id") Integer id) {
//        log.info("Finding product by id {}", id);
//        return new ResponseEntity<>(productService.findById(id), HttpStatus.OK);
//    }
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Product> deleteById(@PathVariable("id") Integer id) {
//        log.info("Deleting product by id {}", id);
//        productService.delete(id);
//        return ResponseEntity.ok().build();
//    }
//}
package com.colors.ecommerce.backend.infrastructure.rest;

import com.colors.ecommerce.backend.application.ProductService;
import com.colors.ecommerce.backend.domain.model.Product;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@Controller
@RequestMapping("/api/v1/admin/products")
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar"})
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> save(@RequestParam("id") Integer id,
                                        @RequestParam("code") String code,
                                        @RequestParam("name") String name,
                                        @RequestParam("description") String description,
                                        @RequestParam("price") BigDecimal price,
                                        @RequestParam("urlImage") String urlImage,
                                        @RequestParam("userId") Integer userId,
                                        @RequestParam("categoryId") Integer categoryId,
                                        @RequestParam(value = "image", required = false) MultipartFile multipartFile) throws IOException {
        Product product = new Product();
        product.setId(id);
        product.setCode(code);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setUrlImage(urlImage);
        product.setUserId(userId);
        product.setCategoryId(categoryId);

        log.info("Saving product {}", product.getName());
        return new ResponseEntity<>(productService.save(product, multipartFile), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Iterable<Product>> findAll() {
        log.info("Finding all products");
        return new ResponseEntity<>(productService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable("id") Integer id) {
        log.info("Finding product by id {}", id);
        return new ResponseEntity<>(productService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteById(@PathVariable("id") Integer id) throws IOException {
        log.info("Deleting product by id {}", id);
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
