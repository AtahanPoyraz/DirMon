package io.dirmon.project.auth.service;

import io.dirmon.project.auth.dto.SignInRequest;
import io.dirmon.project.auth.dto.SignUpRequest;
import io.dirmon.project.user.model.UserModel;

public interface AuthService {
    UserModel signUp(SignUpRequest signUpRequest);
    UserModel signIn(SignInRequest signInRequest);
}
