package io.dirmon.auth.service;

import io.dirmon.auth.dto.SignInRequest;
import io.dirmon.auth.dto.SignUpRequest;
import io.dirmon.user.model.UserModel;

public interface AuthService {
    UserModel signUp(SignUpRequest signUpRequest);
    UserModel signIn(SignInRequest signInRequest);
}
