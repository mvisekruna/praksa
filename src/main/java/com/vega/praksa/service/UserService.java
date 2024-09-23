package com.vega.praksa.service;

import com.vega.praksa.dto.UserRequest;
import com.vega.praksa.model.User;

import java.util.List;

public interface UserService {

    User findById(Long id);
    User findByUsername(String username);
    List<User> getAllUsers();
    User saveUser(UserRequest userRequest);
    void saveUser(User user);

}
