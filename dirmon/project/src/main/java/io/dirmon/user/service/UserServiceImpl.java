package io.dirmon.user.service;

import io.dirmon.user.exception.UserNotFoundException;
import io.dirmon.user.dto.UpdateDetailsRequest;
import io.dirmon.user.dto.UpdatePasswordRequest;
import io.dirmon.user.mapper.UserMapper;
import io.dirmon.user.model.UserModel;
import io.dirmon.user.repository.UserRepository;
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
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserModel fetchUserByUserId(UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("UserDto not found with id: " + userId));
    }

    @Override
    @Transactional
    public UserModel updateDetailsByUserId(UUID userId, UpdateDetailsRequest updateDetailsRequest) {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("UserDto not found with id: " + userId));

        this.userMapper.updateUserDetailsToEntity(updateDetailsRequest, userEntity);

        return this.userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public UserModel updatePasswordByUserId(UUID userId, UpdatePasswordRequest updatePasswordRequest) {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("UserDto not found with id: " + userId));

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
                .orElseThrow(() -> new UserNotFoundException("UserDto not found with id: " + userId));

        userEntity.setEnabled(false);

        return this.userRepository.save(userEntity);
    }
}
