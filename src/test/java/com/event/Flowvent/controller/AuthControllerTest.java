package com.event.Flowvent.controller;

import com.event.Flowvent.auth.AuthController;
import com.event.Flowvent.auth.AuthService;
import com.event.Flowvent.auth.AuthUserResponseDto;
import com.event.Flowvent.exception.GlobalExceptionHandler;
import com.event.Flowvent.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController authController = new AuthController(authService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void getAuthenticatedUser_shouldReturnAuthenticatedUser() throws Exception {
        AuthUserResponseDto response = new AuthUserResponseDto(
                1L,
                "client",
                "client@flowvent.com",
                Role.CLIENT
        );

        when(authService.getAuthenticatedUser()).thenReturn(response);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("client"))
                .andExpect(jsonPath("$.email").value("client@flowvent.com"))
                .andExpect(jsonPath("$.role").value("CLIENT"));

        verify(authService).getAuthenticatedUser();
    }
}