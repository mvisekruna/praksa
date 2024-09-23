package com.vega.praksa.service.implementation;

import com.vega.praksa.dto.UserRequest;
import com.vega.praksa.model.Role;
import com.vega.praksa.model.User;
import com.vega.praksa.repository.UserRepository;
import com.vega.praksa.service.RoleService;
import com.vega.praksa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(UserRequest userRequest) {
        User user = new User();

        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEnabled(false);

        List<Role> roles = this.roleService.findByName("ROLE_CONSUMER");
        user.setRoles(roles);

        return this.userRepository.save(user);
    }

    @Override
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

}
