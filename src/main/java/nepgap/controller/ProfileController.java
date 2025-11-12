package nepgap.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nepgap.dto.ApiResponse;
import nepgap.dto.ProfileDTO;
import nepgap.dto.UpdateProfileRequest;
import nepgap.security.UserPrincipal;
import nepgap.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileDTO>> getProfile(
            @AuthenticationPrincipal UserPrincipal user,
            HttpServletRequest req) {
        ProfileDTO profile = profileService.getProfile(user.getId());
        ApiResponse<ProfileDTO> response = ApiResponse.<ProfileDTO>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Profile retrieved successfully")
                .data(profile)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<ProfileDTO>> updateProfile(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest req) {
        ProfileDTO profile = profileService.updateProfile(user.getId(), request);
        ApiResponse<ProfileDTO> response = ApiResponse.<ProfileDTO>builder()
                .timestamp(Instant.now())
                .status(200)
                .message("Profile updated successfully")
                .data(profile)
                .path(req.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }
}
