package nepgap.config;

import lombok.RequiredArgsConstructor;
import nepgap.model.Role;
import nepgap.model.RoleName;
import nepgap.model.User;
import nepgap.repository.RoleRepository;
import nepgap.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        // We return oauth2User as is; we handle DB mapping in success handler
        return oauth2User;
    }

    /**
     * Helper to create a local User if not exist.
     * Call this from success handler when you have provider attributes.
     */
    public User createOrUpdateGoogleUser(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.getOrDefault("name", "");
        if (email == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info"), "Email not found from OAuth2 provider");
        }

        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isPresent()) {
            User existing = opt.get();
            existing.setFullName(name);
            // do not change password
            return userRepository.save(existing);
        } else {
            Role userRole = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow();
            User u = User.builder()
                    .email(email)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .fullName(name)
                    .roles(new HashSet<>(Collections.singletonList(userRole)))
                    .build();
            return userRepository.save(u);
        }
    }
}
