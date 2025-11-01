package nepgap.service.impl;

import lombok.RequiredArgsConstructor;
import nepgap.dto.ApiResponse;
import nepgap.dto.AuthResponse;
import nepgap.dto.LoginRequest;
import nepgap.dto.SignUpRequest;
import nepgap.exception.ApiException;
import nepgap.model.Role;
import nepgap.model.RoleName;
import nepgap.model.User;
import nepgap.repository.RoleRepository;
import nepgap.repository.UserRepository;
import nepgap.security.JwtProvider;
import nepgap.security.UserPrincipal;
import nepgap.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        var user = userRepository.findByEmail(principal.getUsername()).orElseThrow();
        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRoles());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public ApiResponse<?> register(SignUpRequest request, String path) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email is already in use", HttpStatus.BAD_REQUEST);
        }
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ApiException("Default role not configured", HttpStatus.INTERNAL_SERVER_ERROR));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .roles(new HashSet<>(Collections.singletonList(userRole)))
                .build();
        userRepository.save(user);

        ApiResponse<Object> res = ApiResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.CREATED.value())
                .message("User registered successfully")
                .data(Map.of("email", user.getEmail(), "id", user.getId()))
                .path(path)
                .build();
        return res;
    }
}