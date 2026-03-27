package com.dirmon.project.auth.service;

import com.dirmon.project.auth.dto.SignInRequest;
import com.dirmon.project.auth.dto.SignUpRequest;
import com.dirmon.project.user.exception.EmailAlreadyExistException;
import com.dirmon.project.user.exception.UserNotFoundException;
import com.dirmon.project.user.model.UserModel;

public interface AuthService {
    UserModel signUp(SignUpRequest signUpRequest) throws UserNotFoundException;
    UserModel signIn(SignInRequest signInRequest) throws UserNotFoundException, EmailAlreadyExistException;
}
