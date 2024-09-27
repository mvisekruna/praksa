package com.vega.praksa.controller;

import com.vega.praksa.dto.JwtAuthenticationRequest;
import com.vega.praksa.dto.UserRequest;
import com.vega.praksa.dto.UserTokenState;
import com.vega.praksa.exception.ResourceConflictException;
import com.vega.praksa.model.User;
import com.vega.praksa.model.VerificationToken;
import com.vega.praksa.service.EmailService;
import com.vega.praksa.service.UserService;
import com.vega.praksa.service.VerificationTokenService;
import com.vega.praksa.util.TokenUtils;
import jakarta.mail.MessagingException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final TokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public RoleController(TokenUtils tokenUtils, AuthenticationManager authenticationManager, UserService userService, EmailService emailService, VerificationTokenService verificationTokenService) {
        this.tokenUtils = tokenUtils;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserTokenState> loginUser(@RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user.getUsername());
        int expiresIn = tokenUtils.getExpiredIn();

        return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRequest userRequest, UriComponentsBuilder uriBuilder) {
        User existingUser = this.userService.findByUsername(userRequest.getUsername());

        if (existingUser != null) {
           throw new ResourceConflictException(userRequest.getId(), "Username already exists");
        }

        User user = this.userService.saveUser(userRequest);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpiryDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));

        verificationTokenService.saveVerificationToken(verificationToken);

        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());
        } catch (MessagingException e) {
            logger.error("Failed to send a verification email to: {}", user.getEmail(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
        VerificationToken verificationToken = verificationTokenService.findByToken(token);

        if(verificationToken == null || verificationToken.getExpiryDate().before(new Date())) {
            return new ResponseEntity<>("Invalid or expired verification token.", HttpStatus.BAD_REQUEST);
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        userService.saveUser(user);

        return new ResponseEntity<>("User verified successfully.", HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        try {
            this.userService.changePassword(oldPassword, newPassword);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Password changed.");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect.");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) throws MessagingException {
        User user = this.userService.findByEmail(email);

        if(user == null) {
            return new ResponseEntity<>("No user found with that email.", HttpStatus.NOT_FOUND);
        }

        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));

        this.verificationTokenService.saveVerificationToken(token);

        String resetUrl = "http://localhost:8080/auth/reset-password?token=" + token.getToken();
        this.emailService.sendResetPasswordEmail(email, resetUrl);

        return new ResponseEntity<>("Password reset link sent to your email.", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("newPassword") String newPassword) throws BadRequestException {
        VerificationToken resetToken = verificationTokenService.findByToken(token);

        this.userService.resetPassword(resetToken, newPassword);

        return new ResponseEntity<>("Password reset successfully.", HttpStatus.OK);
    }

}