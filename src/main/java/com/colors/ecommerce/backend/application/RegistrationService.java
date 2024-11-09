package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.User;
import com.colors.ecommerce.backend.domain.port.IUserRepository;

public class RegistrationService {
    private final IUserRepository iUserRepository;

    public RegistrationService(IUserRepository iUserRepository) {
        this.iUserRepository = iUserRepository;
    }
    public User register(User user) {
        return iUserRepository.save(user);
    }
}
