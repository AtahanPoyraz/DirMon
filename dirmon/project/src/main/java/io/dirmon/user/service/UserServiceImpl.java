package io.dirmon.user.service;

import io.dirmon.user.dto.UserDto;
import io.dirmon.user.exception.UserNotFoundException;
import io.dirmon.user.dto.UpdateDetailsRequest;
import io.dirmon.user.dto.UpdatePasswordRequest;
import io.dirmon.user.mapper.UserMapper;
import io.dirmon.user.model.UserModel;
import io.dirmon.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public UserDto fetchUserByUserId(UUID userId) throws UserNotFoundException {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        return this.userMapper.toDto(userEntity);
    }

    @Override
    @Transactional
    public UserDto updateDetailsByUserId(UUID userId, UpdateDetailsRequest updateDetailsRequest) throws UserNotFoundException {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        this.userMapper.updateUserDetailsToEntity(updateDetailsRequest, userEntity);

        userEntity = this.userRepository.save(userEntity);
        return this.userMapper.toDto(userEntity);
    }

    @Override
    @Transactional
    public UserDto updatePasswordByUserId(UUID userId, UpdatePasswordRequest updatePasswordRequest) throws UserNotFoundException, BadCredentialsException {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        if (!this.passwordEncoder.matches(updatePasswordRequest.getPassword(), userEntity.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        if (updatePasswordRequest.getPassword().equals(updatePasswordRequest.getNewPassword())) {
            throw new BadCredentialsException("New password must differ from the current password");
        }

        userEntity.setPassword(this.passwordEncoder.encode(updatePasswordRequest.getNewPassword()));

        userEntity = this.userRepository.save(userEntity);
        return this.userMapper.toDto(userEntity);
    }

    @Override
    @Transactional
    public UserDto deActivateUserByUserId(UUID userId) throws UserNotFoundException {
        UserModel userEntity = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        userEntity.setEnabled(false);

        userEntity = this.userRepository.save(userEntity);
        return this.userMapper.toDto(userEntity);
    }
}
