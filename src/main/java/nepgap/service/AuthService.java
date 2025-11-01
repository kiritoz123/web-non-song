package nepgap.service;

import nepgap.dto.ApiResponse;
import nepgap.dto.AuthResponse;
import nepgap.dto.LoginRequest;
import nepgap.dto.SignUpRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    ApiResponse<?> register(SignUpRequest request, String path);
}