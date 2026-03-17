package com.dirmon.project.auth.service;

import com.dirmon.project.auth.dto.SignInRequest;
import com.dirmon.project.auth.dto.SignUpRequest;
import com.dirmon.project.common.exception.EmailAlreadyExistException;
import com.dirmon.project.user.model.UserModel;
import com.dirmon.project.user.model.UserRole;
import com.dirmon.project.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.EnumSet;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserModel signUp(SignUpRequest signUpRequest) {
        if (this.userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExistException("User with email " + signUpRequest.getEmail() + " already exists");
        }

        UserModel userEntity = UserModel.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .email(signUpRequest.getEmail())
                .password(this.passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(EnumSet.of(UserRole.ROLE_USER))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        return this.userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public UserModel signIn(SignInRequest signInRequest) {
        UserModel userEntity = this.userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!this.passwordEncoder.matches(signInRequest.getPassword(), userEntity.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!userEntity.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        this.userRepository.updateLastLogin(userEntity.getUserId(), Instant.now());

        return userEntity;
    }
}
