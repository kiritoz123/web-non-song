package nepgap.config;



import nepgap.model.Role;
import nepgap.model.RoleName;
import nepgap.model.User;
import nepgap.repository.RoleRepository;
import nepgap.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(RoleRepository roleRepo,
                           UserRepository userRepo,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.initial.admin-email}") String adminEmail,
                           @Value("${app.initial.admin-password}") String adminPassword) {
        return args -> {
            if (roleRepo.count() == 0) {
                roleRepo.save(Role.builder().name(RoleName.ROLE_USER).build());
                roleRepo.save(Role.builder().name(RoleName.ROLE_MANAGER).build());
                roleRepo.save(Role.builder().name(RoleName.ROLE_ADMIN).build());
            }

            if (!userRepo.existsByEmail(adminEmail)) {
                var adminRole = roleRepo.findByName(RoleName.ROLE_ADMIN).orElseThrow();
                var admin = User.builder()
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .fullName("Super Admin")
                        .roles(Set.of(adminRole))
                        .build();
                userRepo.save(admin);
            }
        };
    }
}
