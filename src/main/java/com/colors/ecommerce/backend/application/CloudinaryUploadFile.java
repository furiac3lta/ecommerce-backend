package com.colors.ecommerce.backend.application;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryUploadFile {

    private final Cloudinary cloudinary;

    public CloudinaryUploadFile(CloudinaryConfig cloudinaryConfig) {
        this.cloudinary = cloudinaryConfig.getCloudinary();
    }

    public String upload(MultipartFile multipartFile) throws IOException {
        if (multipartFile != null && !multipartFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(multipartFile.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("url").toString();  // Devuelve la URL de la imagen subida
        }

        return "https://ecommerce-back-0cc9b90e39e5.herokuapp.com/images/default.jpg";
    }

    public void delete(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
