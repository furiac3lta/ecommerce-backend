package com.colors.ecommerce.backend.infrastucture.rest;

import com.colors.ecommerce.backend.application.UserService;
import com.colors.ecommerce.backend.domain.model.User;
import jakarta.persistence.GeneratedValue;
import org.springframework.web.bind.annotation.*;

@RestController
//http://localhost:8085/
@RequestMapping("/api/v1/users")
//http://localhost:8085/api/v1/users
@CrossOrigin(origins = {"http://localhost:4200", "https://www.lcosmeticadigital.com.ar", "https://ecommerce-angular-production.up.railway.app"})
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping
    public User save(@RequestBody User user) {
        return userService.save(user);
    }

    //http://localhost:8085/api/v1/users/4
    @GetMapping("/{id}")
    public User findById(@PathVariable("id") Integer id) {
        return userService.findById(id);
    }
}
