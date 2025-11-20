package nepgap.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import nepgap.dto.AuthResponse;
import nepgap.model.User;
import nepgap.repository.UserRepository;
import nepgap.security.JwtProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * On successful OAuth2 login, create or update local user, create JWT tokens,
 * then redirect to configured frontend URL with tokens as query params.
 */
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    // frontend URL where tokens will be sent (set via env var APP_OAUTH2_REDIRECT_URL)



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attrs = oauthUser.getAttributes();

        // Create or update local user
        User user = customOAuth2UserService.createOrUpdateGoogleUser(attrs);

        // Generate tokens
        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRoles());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        // Redirect to frontend with token params (you may want to use cookies instead)
        String redirectUrl = "https://nepgapnonsong.com" +
                "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8) +
                "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);


        response.sendRedirect(redirectUrl);
    }
}
