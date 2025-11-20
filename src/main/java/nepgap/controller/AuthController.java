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
import nepgap.security.UserPrincipal;
import nepgap.service.AuthService;
import nepgap.service.CloudinaryService;
import nepgap.service.EmailSenderService;
import nepgap.service.OtpCacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final OtpCacheService cacheService;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

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

    @PostMapping("/account/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody Map<String, Object> request)
    {
        StringBuilder message = new StringBuilder();

        if(authService.requestSendEmailForgetPassword(request, message)) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.badRequest().body(message.toString());
        }
    }
    @PostMapping("/account/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("otp") String otp, @RequestParam("email") String email,
    @RequestParam("password") String password)
    {
        Optional<User> optional = userRepository.findByEmail(email);
        if(optional.isPresent()) {
            User user = optional.get();
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return ResponseEntity.ok("Reset password success");
        } else {
            return ResponseEntity.badRequest().body("Reset fail");
        }
    }
    @PostMapping("/user/avatar/upload")
    public ResponseEntity<?> uploadAvatar(@RequestParam(value = "avatar") MultipartFile avatar,@AuthenticationPrincipal UserPrincipal user) throws IOException {
        if((!avatar.getContentType().equals("image/png") &&
                !avatar.getContentType().equals("image/jpeg")) || avatar.equals(null)) {
            return ResponseEntity.badRequest().body("file extension must be .jpeg or .png");
        }
        String username = user.getEmail();
        if(uploadAvatar(avatar, username)) {
            return ResponseEntity.ok("Update avatar user: " + username + " success");
        } else {
            return ResponseEntity.badRequest().body("Update avatar user: " + username + " fail");
        }
    }
    private Boolean uploadAvatar(MultipartFile avatar, String username) throws IOException {
        Optional<User> usersOptional = userRepository.findByEmail(username);
        System.out.println(username);
        if (!usersOptional.isPresent()) {
            return false;
        }
        User userLogin = usersOptional.get();
        String url = cloudinaryService.uploadFile(
                avatar.getBytes(),
                String.valueOf(userLogin.getId()),
                "MyAnimeProject_TLCN/user/avatar");
        if (url.equals("-1")) {
            return false;
        }
        userLogin.setAvatar(url);
        try {
            userRepository.save(userLogin);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}