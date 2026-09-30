package com.prince.materialportal.service;

import com.prince.materialportal.dto.LoginRequest;
import com.prince.materialportal.dto.LoginResponse;
import com.prince.materialportal.dto.RegisterRequest;
import com.prince.materialportal.dto.UserDto;
import com.prince.materialportal.entity.Role;
import com.prince.materialportal.entity.User;
import com.prince.materialportal.exception.InvalidRequestException;
import com.prince.materialportal.exception.ResourceNotFoundException;
import com.prince.materialportal.repository.RoleRepository;
import com.prince.materialportal.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       CustomUserDetailsService userDetailsService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername().trim(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password.");
        }

        User user = userRepository.findByUsernameIgnoreCase(request.getUsername().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + request.getUsername()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);

        String roleName = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("ROLE_STUDENT");

        if (roleName.startsWith("ROLE_")) {
            roleName = roleName.substring(5);
        }

        return LoginResponse.builder()
                .token(token)
                .role(roleName)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    @Transactional
    public UserDto register(RegisterRequest request) {
        String username = request.getUsername().trim();
        if (userRepository.existsByUsername(username)) {
            throw new InvalidRequestException("Username already exists: " + username);
        }

        String roleName = request.getRole() != null && !request.getRole().isBlank()
                ? request.getRole().toUpperCase().trim()
                : "STUDENT";

        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        final String targetRoleName = roleName;
        Role role = roleRepository.findByName(targetRoleName)
                .orElseGet(() -> roleRepository.save(new Role(targetRoleName)));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(roles)
                .build();

        User saved = userRepository.save(user);

        return UserDto.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .fullName(saved.getFullName())
                .roles(saved.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUser(String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }
}
