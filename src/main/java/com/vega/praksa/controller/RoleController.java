package com.vega.praksa.controller;

import com.vega.praksa.dto.JwtAuthenticationRequest;
import com.vega.praksa.dto.UserRequest;
import com.vega.praksa.dto.UserTokenState;
import com.vega.praksa.exception.ResourceConflictException;
import com.vega.praksa.model.User;
import com.vega.praksa.service.UserService;
import com.vega.praksa.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleController {

    private final TokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    @Autowired
    public RoleController(TokenUtils tokenUtils, AuthenticationManager authenticationManager, UserService userService) {
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user.getUsername());
        int expiresIn = tokenUtils.getExpiredIn();

        return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
    }

    @PostMapping("/register")
    public ResponseEntity<User> addUser(@RequestBody UserRequest userRequest, UriComponentsBuilder uriBuilder) {
        User existingUser = this.userService.findByUsername(userRequest.getUsername());

        if (existingUser != null) {
           throw new ResourceConflictException(userRequest.getId(), "Username already exists");
        }

        User user = this.userService.saveUser(userRequest);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

}
