package com.vega.praksa.service;

import com.vega.praksa.dto.UserRequest;
import com.vega.praksa.model.User;
import com.vega.praksa.model.VerificationToken;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface UserService {

    User findById(Long id);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> getAllUsers();
    User saveUser(UserRequest userRequest);
    void saveUser(User user);
    void changePassword(String oldPassword, String newPassword);
    void resetPassword(VerificationToken token, String newPassword) throws BadRequestException;

}
