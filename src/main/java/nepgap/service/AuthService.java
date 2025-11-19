package nepgap.service;

import nepgap.dto.ApiResponse;
import nepgap.dto.AuthResponse;
import nepgap.dto.LoginRequest;
import nepgap.dto.SignUpRequest;

import java.util.Map;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    ApiResponse<?> register(SignUpRequest request, String path);
    boolean requestSendEmailForgetPassword(Map<String, Object> request, StringBuilder message);
}