package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.LoginResponseDto;
import com.project.sentimentapi.dto.UserDtoRegistro;

import java.util.Optional;

public interface UserService {
    void registrarUsuario(UserDtoRegistro userDtoRegistro);
    Optional<LoginResponseDto> login(UserDtoRegistro userDtoRegistro);
    void forgotPassword(String email);
    void resetPassword(String token, String nuevaContrasena);
}