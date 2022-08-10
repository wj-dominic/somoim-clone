package com.somoim.service;

import com.somoim.model.dto.LoginUser;
import com.somoim.model.dto.SignUpUser;
import com.somoim.model.entity.UserEntity;
import com.somoim.repository.user.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    HttpSession httpSession;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    SignUpUser signUpUser;

    LoginUser loginUser;

    @BeforeEach
    void setUp() {
        signUpUser = new SignUpUser();
        signUpUser.setEmail("test@somoim.com");
        signUpUser.setPassword("1234");

        loginUser = new LoginUser();
        loginUser.setEmail("test@somoim.com");
        loginUser.setPassword("1234");
    }

    @Test
    void createUserTest() {
        //when
        userService.createUser(signUpUser);
        //then
        ArgumentCaptor<UserEntity> argument = ArgumentCaptor.forClass(UserEntity.class);
        Mockito.verify(userRepository).save(argument.capture());
        assertEquals(signUpUser.getEmail(), argument.getValue().getEmail());
        assertNotNull(argument.getValue().getCreateAt());
        assertNotNull(argument.getValue().getModifyAt());
    }

    @Test
    void deleteUserTest() {
        UserEntity userEntity = UserEntity.builder()
                .email("test@somoim.com")
                .password("1234")
                .disband(false)
                .modifyAt(LocalDateTime.now())
                .build();
        userEntity.setId(1L);

        when(httpSession.getAttribute("USER_ID")).thenReturn(1L);

        when(userRepository.findById(userEntity.getId()))
                .thenReturn(Optional.of(userEntity));

        //when
        userService.deleteUser();
        //then
        assertTrue(userEntity.getDisband());
    }

    @Test
    void loginUserTestWithSuccess() {
        //given
        UserEntity userEntity = UserEntity.builder()
                .email("test@somoim.com")
                .password("1234")
                .disband(false)
                .build();
        userEntity.setId(1L);

        //when
        when(userRepository.findByEmail(userEntity.getEmail()))
                .thenReturn(userEntity);

        //when
        when(passwordEncoder.matches(loginUser.getPassword(), userEntity.getPassword()))
                .thenReturn(true);

        userService.loginUser(loginUser);

        //then
        verify(httpSession).setAttribute("USER_ID", userEntity.getId());
    }

    @Test
    void loginUserTestWithFail() {
        //given
        UserEntity userEntity = UserEntity.builder()
                .email("test@somoim.com")
                .password("1234")
                .disband(false)
                .build();
        userEntity.setId(1L);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                                                       () -> userService.loginUser(loginUser));
        assertEquals("user does not exist.", thrown.getMessage());

        //when
        when(userRepository.findByEmail(userEntity.getEmail()))
                .thenReturn(userEntity);

        //when
        when(passwordEncoder.matches(loginUser.getPassword(), userEntity.getPassword()))
                .thenReturn(false);

        thrown = assertThrows(IllegalArgumentException.class,
                              () -> userService.loginUser(loginUser));
        assertEquals("password is wrong.", thrown.getMessage());

        userEntity.setDisband(true);
        //when
        when(userRepository.findByEmail(userEntity.getEmail()))
                .thenReturn(userEntity);

        //when
        when(passwordEncoder.matches(loginUser.getPassword(), userEntity.getPassword()))
                .thenReturn(true);

        thrown = assertThrows(IllegalArgumentException.class,
                              () -> userService.loginUser(loginUser));
        assertEquals("resigned user", thrown.getMessage());
    }

    @Test
    void logoutTest() {
        userService.logoutUser();
        verify(httpSession).removeAttribute("USER_ID");
    }
}