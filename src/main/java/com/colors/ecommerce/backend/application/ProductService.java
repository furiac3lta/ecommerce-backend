//package com.colors.ecommerce.backend.application;
//
//import com.colors.ecommerce.backend.domain.model.Product;
//import com.colors.ecommerce.backend.domain.port.IProductRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@Slf4j
//public class ProductService {
//    private final IProductRepository iProductRepository;
//    private final UploadFile uploadFile;
//
//    public ProductService(IProductRepository iProductRepository, UploadFile uploadFile) {
//        this.iProductRepository = iProductRepository;
//        this.uploadFile = uploadFile;
//    }
//
//    public Product save(Product product, MultipartFile multipartFile) throws IOException {
//        if (product.getId() != 0 && multipartFile != null && !multipartFile.isEmpty()) {
//
//                String newImageUrl = uploadFile.upload(multipartFile);
//                product.setUrlImage(newImageUrl);
//                System.out.println("Nueva imagen cargada: " + newImageUrl);
//
//
//        } else {
//            // Producto nuevo: asigna la imagen cargada o la imagen por defecto
//            if (multipartFile != null && !multipartFile.isEmpty()) {
//                String imageUrl = uploadFile.upload(multipartFile);
//                product.setUrlImage(imageUrl);
//                System.out.println("Imagen guardada para nuevo producto: " + imageUrl);
//            } else {
//                // Asigna la imagen por defecto en caso de que no se proporcione una nueva
//                String defaultImageUrl = "http://localhost:8085/images/default.jpg";
//                product.setUrlImage(defaultImageUrl);
//                System.out.println("Usando imagen por defecto para el nuevo producto: " + defaultImageUrl);
//            }
//        }
//        return iProductRepository.save(product);
//    }
//
//
//    public Iterable<Product> findAll() {
//        return iProductRepository.findAll();
//    }
//    public Product findById(Integer id) {
//        return iProductRepository.findById(id);
//    }
//    public void delete(Integer id) {
//        Product product = findById(id);
//        if(product.getUrlImage() == null || product.getUrlImage().isEmpty()) {
//            this.iProductRepository.deleteById(id);
//        }
//        String name = product.getUrlImage().substring(29);
//        log.info("se borra la imagen " + name);
//        if(!name.equals("default.jpg")){
//            uploadFile.delete(name);
//        }
//        this.iProductRepository.deleteById(id);
//    }
//}
//package com.colors.ecommerce.backend.application;
//
//import com.colors.ecommerce.backend.domain.model.Product;
//import com.colors.ecommerce.backend.domain.port.IProductRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@Slf4j
//public class ProductService {
//
//    private final IProductRepository iProductRepository;
//    private final CloudinaryUploadFile cloudinaryUploadFile;
//
//    public ProductService(IProductRepository iProductRepository, CloudinaryUploadFile cloudinaryUploadFile) {
//        this.iProductRepository = iProductRepository;
//        this.cloudinaryUploadFile = cloudinaryUploadFile;
//    }
//
//    public Product save(Product product, MultipartFile multipartFile) throws IOException {
//        if (multipartFile != null && !multipartFile.isEmpty()) {
//            String imageUrl = cloudinaryUploadFile.upload(multipartFile);
//            product.setUrlImage(imageUrl);
//            log.info("Imagen guardada para producto: {}", imageUrl);
//        } else if (product.getUrlImage() == null || product.getUrlImage().isEmpty()) {
//            product.setUrlImage("https://ecommerce-back-0cc9b90e39e5.herokuapp.com/images/default.jpg");
//            log.info("Usando imagen por defecto para el producto: {}", product.getName());
//        }
//
//        return iProductRepository.save(product);
//    }
//
//    public Iterable<Product> findAll() {
//        return iProductRepository.findAll();
//    }
//
//    public Product findById(Integer id) {
//        return iProductRepository.findById(id);
//    }
//
//    public void delete(Integer id) throws IOException {
//        Product product = findById(id);
//        if (product != null && product.getUrlImage() != null && !product.getUrlImage().contains("default.jpg")) {
//            String publicId = product.getUrlImage().substring(product.getUrlImage().lastIndexOf('/') + 1);
//            cloudinaryUploadFile.delete(publicId);
//            log.info("Imagen eliminada: {}", publicId);
//        }
//        this.iProductRepository.deleteById(id);
//    }
//}
package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.Product;
import com.colors.ecommerce.backend.domain.port.IProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class ProductService {
    private final IProductRepository iProductRepository;
    private final CloudinaryUploadFile cloudinaryUploadFile;

    public ProductService(IProductRepository iProductRepository, CloudinaryUploadFile cloudinaryUploadFile) {
        this.iProductRepository = iProductRepository;
        this.cloudinaryUploadFile = cloudinaryUploadFile;
    }

    public Product save(Product product, MultipartFile multipartFile) throws IOException {
        if (product.getBrand() == null || product.getBrand().isBlank()) {
            product.setBrand("LION'S BRAND");
        }
        if (product.getActive() == null) {
            product.setActive(true);
        }
        if (product.getSlug() == null || product.getSlug().isBlank()) {
            product.setSlug(toSlug(product.getName()));
        }

        if (multipartFile != null && !multipartFile.isEmpty()) {
            String uploadResult = cloudinaryUploadFile.upload(multipartFile);
            if (uploadResult != null) {
                product.setUrlImage(uploadResult);
                product.setPublicId(getPublicIdFromUrl(uploadResult));
                log.info("Imagen guardada para producto: {}", uploadResult);
            }
        } else if (product.getUrlImage() == null || product.getUrlImage().isEmpty()) {
            product.setUrlImage("https://ecommerce-back-0cc9b90e39e5.herokuapp.com/images/default.jpg");
            product.setPublicId("default");
            log.info("Usando imagen por defecto para el producto: {}", product.getName());
        }

        return iProductRepository.save(product);
    }

    public Iterable<Product> findAll() {
        return iProductRepository.findAll();
    }

    public Product findById(Integer id) {
        return iProductRepository.findById(id);
    }

    public void delete(Integer id) throws IOException {
        Product product = findById(id);
        if (product != null && product.getPublicId() != null && !product.getPublicId().equals("default")) {
            cloudinaryUploadFile.delete(product.getPublicId());
            log.info("Imagen eliminada: {}", product.getPublicId());
        }
        this.iProductRepository.deleteById(id);
    }

    private String getPublicIdFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
    }

    private String toSlug(String value) {
        if (value == null) {
            return null;
        }
        return value.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
