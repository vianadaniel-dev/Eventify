package com.example.eventify.service;

import com.example.eventify.dto.AuthRequestDTO;
import com.example.eventify.dto.AuthResponseDTO;
import com.example.eventify.dto.RegisterRequestDTO;
import com.example.eventify.exception.DuplicateResourceException;
import com.example.eventify.model.Role;
import com.example.eventify.model.User;
import com.example.eventify.repository.UserRepository;
import com.example.eventify.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("El nombre de usuario ya está en uso: " + request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("El correo electrónico ya está registrado: " + request.email());
        }

        Role assignedRole = Role.ROLE_COORDINATOR;
        if (request.role() != null && !request.role().isBlank()) {
            try {
                assignedRole = Role.valueOf(request.role().toUpperCase());
            } catch (IllegalArgumentException ignored) {
                assignedRole = Role.ROLE_COORDINATOR;
            }
        }

        User user = User.builder()
                .username(request.username().trim())
                .email(request.email().trim())
                .password(passwordEncoder.encode(request.password()))
                .role(assignedRole)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);

        return new AuthResponseDTO(token, user.getUsername(), user.getRole().name());
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userRepository.findByUsername(request.username()).orElseThrow();
        String token = jwtService.generateToken(user);

        return new AuthResponseDTO(token, user.getUsername(), user.getRole().name());
    }
}