package io.dirmon.project.user.service;

import io.dirmon.project.user.exception.UserNotFoundException;
import io.dirmon.project.user.dto.UpdateDetailsRequest;
import io.dirmon.project.user.dto.UpdatePasswordRequest;
import io.dirmon.project.user.model.UserModel;
import io.dirmon.project.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserModel fetchUserByUserId(UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Override
    @Transactional
    public UserModel updateDetailsByUserId(UUID userId, UpdateDetailsRequest updateDetailsRequest) {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (updateDetailsRequest.getFirstName() != null && !updateDetailsRequest.getFirstName().isEmpty()) {
            userEntity.setFirstName(updateDetailsRequest.getFirstName());
        }

        if (updateDetailsRequest.getLastName() != null && !updateDetailsRequest.getLastName().isEmpty()) {
            userEntity.setLastName(updateDetailsRequest.getLastName());
        }

        return this.userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public UserModel updatePasswordByUserId(UUID userId, UpdatePasswordRequest updatePasswordRequest) {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!this.passwordEncoder.matches(updatePasswordRequest.getPassword(), userEntity.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        userEntity.setPassword(this.passwordEncoder.encode(updatePasswordRequest.getNewPassword()));

        return this.userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public UserModel deActivateUserByUserId(UUID userId) {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        userEntity.setEnabled(false);

        return this.userRepository.save(userEntity);
    }
}
