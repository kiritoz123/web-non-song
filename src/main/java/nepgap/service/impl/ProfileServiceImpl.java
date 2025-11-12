package nepgap.service.impl;

import lombok.RequiredArgsConstructor;
import nepgap.dto.ProfileDTO;
import nepgap.dto.UpdateProfileRequest;
import nepgap.exception.ApiException;
import nepgap.model.User;
import nepgap.repository.UserRepository;
import nepgap.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;

    @Override
    public ProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        return ProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .build();
    }

    @Override
    @Transactional
    public ProfileDTO updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        User savedUser = userRepository.save(user);

        return ProfileDTO.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .phone(savedUser.getPhone())
                .build();
    }
}
