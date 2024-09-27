package com.vega.praksa.service.implementation;

import com.vega.praksa.dto.UserRequest;
import com.vega.praksa.model.Role;
import com.vega.praksa.model.User;
import com.vega.praksa.model.VerificationToken;
import com.vega.praksa.repository.UserRepository;
import com.vega.praksa.repository.VerificationTokenRepository;
import com.vega.praksa.service.RoleService;
import com.vega.praksa.service.UserService;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImplementation.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    private final AuthenticationManager authenticationManager;

    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService, AuthenticationManager authenticationManager, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.authenticationManager = authenticationManager;
        this.verificationTokenRepository = verificationTokenRepository;
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
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
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

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        String username = currentUser.getName();

        if(authenticationManager != null) {
            logger.debug("Re-authenticating user '{}' for password change request.", username);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
        } else {
            logger.debug("No authentication manager set. Can't change the password.");
        }

        logger.debug("Changing password for user: {}.", username);
        User user = this.findByUsername(username);

        user.setPassword(passwordEncoder.encode(newPassword));
        this.userRepository.save(user);
    }

    @Override
    public void resetPassword(VerificationToken token, String newPassword) throws BadRequestException {
        if(token == null || token.getExpiryDate().before(new Date())) {
            throw new BadRequestException("Invalid or expired token");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        this.userRepository.save(user);

        verificationTokenRepository.delete(token);
    }

}
