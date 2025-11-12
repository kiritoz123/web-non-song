package nepgap.service;

import nepgap.dto.ProfileDTO;
import nepgap.dto.UpdateProfileRequest;

public interface ProfileService {
    ProfileDTO getProfile(Long userId);
    ProfileDTO updateProfile(Long userId, UpdateProfileRequest request);
}