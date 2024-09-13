package com.example.mailapp1.service;

import com.example.mailapp1.config.EncryptionUtil;
import com.example.mailapp1.entity.AuthenticationResponse;
import com.example.mailapp1.entity.User;
import com.example.mailapp1.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository repository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(User request) {
        try {
            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());


            String encryptedPassword = EncryptionUtil.encrypt(request.getPassword());
            user.setPassword(encryptedPassword);

            user.setRole(request.getRole());

            user = repository.save(user);

            String token = jwtService.generateToken(user);
            return new AuthenticationResponse(token);
        } catch (Exception e) {
            throw new RuntimeException("Error during registration", e);
        }
    }

    public AuthenticationResponse authenticate(User request) {
        try {

            User user = repository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));


            String decryptedPassword = EncryptionUtil.decrypt(user.getPassword());
            if (!decryptedPassword.equals(request.getPassword())) {
                throw new RuntimeException("Geçersiz kimlik bilgileri");
            }


            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String token = jwtService.generateToken(user);
            return new AuthenticationResponse(token);
        } catch (Exception e) {
            throw new RuntimeException("Error during authentication", e);
        }
    }
}