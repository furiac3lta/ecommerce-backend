package com.colors.ecommerce.backend.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UploadFile {
    private final String FOLDER = "src/main/resources/static/images/";
    private final String IMG_DEFAULT ="default.jpg";
    private final String URL = "http://localhost:8085/images/";


   public String upload(MultipartFile multipartFile) throws IOException {
       if (multipartFile != null && !multipartFile.isEmpty()) {
           String originalFileName = multipartFile.getOriginalFilename();
           String fileName = originalFileName != null ? originalFileName : "image";
           String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
           String baseName = fileName.replace(extension, "");

           Path filePath = Paths.get(FOLDER + fileName);
           int count = 1;

           // Asegura un nombre único añadiendo un número si el archivo ya existe
           while (Files.exists(filePath)) {
               fileName = baseName + "_" + count + extension;
               filePath = Paths.get(FOLDER + fileName);
               count++;
           }

           // Guarda el archivo en el directorio
           Files.write(filePath, multipartFile.getBytes());

           return URL + fileName;
       }

       // Si multipartFile es nulo o vacío, retorna la URL de la imagen por defecto
       return URL + IMG_DEFAULT;
   }

    public void delete(String nameFile){
        File file = new File(FOLDER + nameFile);
        file.delete();

    }
}
