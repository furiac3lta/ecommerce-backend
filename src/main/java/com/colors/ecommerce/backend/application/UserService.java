package com.colors.ecommerce.backend.application;

import com.colors.ecommerce.backend.domain.model.User;
import com.colors.ecommerce.backend.domain.port.IUserRepository;

public class UserService {
    private final IUserRepository iUserRepository;

    public UserService(IUserRepository iUserRepository) {
        this.iUserRepository = iUserRepository;
    }

    public User save(User user) {
        return this.iUserRepository.save(user);
    }
    public User findById(Integer id) {
        return this.iUserRepository.findById(id);
    }
    public User findByEmail(String email) {
        return this.iUserRepository.findByEmail(email);
    }
}
