package com.event.Flowvent.auth;

import com.event.Flowvent.entity.Client;
import com.event.Flowvent.repository.ClientRepository;
import com.event.Flowvent.security.JwtService;
import com.event.Flowvent.user.Role;
import com.event.Flowvent.user.User;
import com.event.Flowvent.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.event.Flowvent.exception.EmailAlreadyExistsException;
import com.event.Flowvent.exception.InvalidCredentialsException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, ClientRepository clientRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CLIENT)
                .build();

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User savedUser = userRepository.save(user);

        Client client = new Client();
        client.setName(savedUser.getUsername());
        client.setEmail(savedUser.getEmail());
        client.setUser(savedUser);

        clientRepository.save(client);

        String token = jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}