package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Product;
import com.colors.ecommerce.backend.domain.port.IProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
public class ProductService {
    private final IProductRepository iProductRepository;
    private final UploadFile uploadFile;

    public ProductService(IProductRepository iProductRepository, UploadFile uploadFile) {
        this.iProductRepository = iProductRepository;
        this.uploadFile = uploadFile;
    }

    public Product save(Product product, MultipartFile multipartFile) throws IOException {
        if (product.getId() != 0 && multipartFile != null && !multipartFile.isEmpty()) {

                String newImageUrl = uploadFile.upload(multipartFile);
                product.setUrlImage(newImageUrl);
                System.out.println("Nueva imagen cargada: " + newImageUrl);


        } else {
            // Producto nuevo: asigna la imagen cargada o la imagen por defecto
            if (multipartFile != null && !multipartFile.isEmpty()) {
                String imageUrl = uploadFile.upload(multipartFile);
                product.setUrlImage(imageUrl);
                System.out.println("Imagen guardada para nuevo producto: " + imageUrl);
            } else {
                // Asigna la imagen por defecto en caso de que no se proporcione una nueva
                String defaultImageUrl = "http://localhost:8085/images/default.jpg";
                product.setUrlImage(defaultImageUrl);
                System.out.println("Usando imagen por defecto para el nuevo producto: " + defaultImageUrl);
            }
        }
        return iProductRepository.save(product);
    }


    public Iterable<Product> findAll() {
        return iProductRepository.findAll();
    }
    public Product findById(Integer id) {
        return iProductRepository.findById(id);
    }
    public void delete(Integer id) {
        Product product = findById(id);
        if(product.getUrlImage() == null || product.getUrlImage().isEmpty()) {
            this.iProductRepository.deleteById(id);
        }
        String name = product.getUrlImage().substring(29);
        log.info("se borra la imagen " + name);
        if(!name.equals("default.jpg")){
            uploadFile.delete(name);
        }
        this.iProductRepository.deleteById(id);
    }
}
