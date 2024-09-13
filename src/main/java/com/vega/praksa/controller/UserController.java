package com.vega.praksa.controller;

import com.vega.praksa.model.User;
import com.vega.praksa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return this.userService.findById(id);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return this.userService.getAllUsers();
    }

}
