package com.colors.ecommerce.backend.application;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class CloudinaryConfig {

    private final Cloudinary cloudinary;

    public CloudinaryConfig() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dkc3u2u1p",
                "api_key", "534232258365678",
                "api_secret", "ALoX7ORh_JhBBHlyRQohnUPoQpw"
        ));
    }

    public Cloudinary getCloudinary() {
        return cloudinary;
    }
}
