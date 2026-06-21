package com.event.Flowvent.service;

import com.event.Flowvent.entity.Client;
import com.event.Flowvent.entity.Event;
import com.event.Flowvent.repository.ClientRepository;
import com.event.Flowvent.repository.EventRepository;
import com.event.Flowvent.user.Role;
import com.event.Flowvent.user.User;
import com.event.Flowvent.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@flowvent.com").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@flowvent.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(admin);
        }

        if (userRepository.findByEmail("client@flowvent.com").isEmpty()) {
            User client = User.builder()
                    .username("client")
                    .email("client@flowvent.com")
                    .password(passwordEncoder.encode("client123"))
                    .role(Role.CLIENT)
                    .build();

            userRepository.save(client);
        }
    }
}
