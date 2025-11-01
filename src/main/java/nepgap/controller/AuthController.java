package nepgap.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nepgap.dto.ApiResponse;
import nepgap.dto.AuthResponse;
import nepgap.dto.LoginRequest;
import nepgap.dto.SignUpRequest;
import nepgap.model.User;
import nepgap.repository.UserRepository;
import nepgap.security.JwtProvider;
import nepgap.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpReq) {
        AuthResponse auth = authService.login(request);
        ApiResponse<AuthResponse> res = ApiResponse.<AuthResponse>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Login successful")
                .data(auth)
                .path(httpReq.getRequestURI())
                .build();
        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody SignUpRequest request, HttpServletRequest httpReq) {
        ApiResponse<?> res = authService.register(request, httpReq.getRequestURI());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody Map<String, String> body, HttpServletRequest httpReq) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            ApiResponse<AuthResponse> bad = ApiResponse.<AuthResponse>builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("refreshToken is required")
                    .path(httpReq.getRequestURI())
                    .build();
            return ResponseEntity.badRequest().body(bad);
        }

        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            ApiResponse<AuthResponse> unauthorized = ApiResponse.<AuthResponse>builder()
                    .timestamp(Instant.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid or expired refresh token")
                    .path(httpReq.getRequestURI())
                    .build();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(unauthorized);
        }

        String username = jwtProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // rotate tokens: issue new access + refresh
        String newAccessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRoles());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        AuthResponse resp = AuthResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .refreshToken(newRefreshToken)
                .build();

        ApiResponse<AuthResponse> ok = ApiResponse.<AuthResponse>builder()
                .timestamp(Instant.now())
                .status(HttpStatus.OK.value())
                .message("Token refreshed")
                .data(resp)
                .path(httpReq.getRequestURI())
                .build();
        return ResponseEntity.ok(ok);
    }

    @GetMapping("/login/google")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }
}