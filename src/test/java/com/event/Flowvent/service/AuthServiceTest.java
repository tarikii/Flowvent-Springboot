package com.event.Flowvent.auth;

import com.event.Flowvent.entity.Client;
import com.event.Flowvent.exception.EmailAlreadyExistsException;
import com.event.Flowvent.exception.InvalidCredentialsException;
import com.event.Flowvent.repository.ClientRepository;
import com.event.Flowvent.security.JwtService;
import com.event.Flowvent.user.Role;
import com.event.Flowvent.user.User;
import com.event.Flowvent.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldCreateUserAndClientProfile_whenRoleIsClient() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("client");
        request.setEmail("client@flowvent.com");
        request.setPassword("password123");
        request.setRole(Role.CLIENT);

        when(userRepository.findByEmail("client@flowvent.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        when(jwtService.generateToken("client@flowvent.com"))
                .thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo("client");
        assertThat(savedUser.getEmail()).isEqualTo("client@flowvent.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(savedUser.getRole()).isEqualTo(Role.CLIENT);

        ArgumentCaptor<Client> clientCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(clientCaptor.capture());

        Client savedClient = clientCaptor.getValue();

        assertThat(savedClient.getName()).isEqualTo("client");
        assertThat(savedClient.getEmail()).isEqualTo("client@flowvent.com");
        assertThat(savedClient.getUser()).isEqualTo(savedUser);
    }

    @Test
    void register_shouldNotCreateClientProfile_whenRoleIsAdmin() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("admin");
        request.setEmail("admin@flowvent.com");
        request.setPassword("password123");
        request.setRole(Role.ADMIN);

        when(userRepository.findByEmail("admin@flowvent.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        when(jwtService.generateToken("admin@flowvent.com"))
                .thenReturn("admin-token");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("admin-token");

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void register_shouldThrowEmailAlreadyExistsException_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("client");
        request.setEmail("client@flowvent.com");
        request.setPassword("password123");
        request.setRole(Role.CLIENT);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("client@flowvent.com");

        when(userRepository.findByEmail("client@flowvent.com"))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any(User.class));
        verify(clientRepository, never()).save(any(Client.class));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("client@flowvent.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("client@flowvent.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CLIENT);

        when(userRepository.findByEmail("client@flowvent.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);

        when(jwtService.generateToken("client@flowvent.com"))
                .thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenEmailDoesNotExist() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@flowvent.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("unknown@flowvent.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("client@flowvent.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setId(1L);
        user.setEmail("client@flowvent.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.CLIENT);

        when(userRepository.findByEmail("client@flowvent.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtService, never()).generateToken(anyString());
    }
}