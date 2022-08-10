package com.somoim.service;

import com.somoim.exception.NotFoundException;
import com.somoim.model.dto.LoginUser;
import com.somoim.model.dto.SignUpUser;
import com.somoim.model.entity.UserEntity;
import com.somoim.repository.user.UserRepository;
import java.time.LocalDateTime;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final HttpSession httpSession;

    private final String USER_ID = "USER_ID";

    @Transactional
    public void createUser(SignUpUser user) {
        LocalDateTime time = LocalDateTime.now();

        UserEntity newUser = UserEntity.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .createAt(time)
                .modifyAt(time)
                .disband(false)
                .build();

        userRepository.save(newUser);
    }

    @Transactional
    public void deleteUser() {
        UserEntity userEntity = getCurrentUser();

        userEntity.setDisband(true);
        userEntity.setModifyAt(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public void loginUser(LoginUser loginUser) {
        UserEntity user = findUserByEmail(loginUser.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("user does not exist.");
        }

        if (!passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("password is wrong.");
        }

        if (user.getDisband()) {
            throw new IllegalArgumentException("resigned user");
        }

        httpSession.setAttribute(USER_ID, user.getId());
    }

    public void logoutUser() {
        httpSession.removeAttribute(USER_ID);
    }

    @Transactional(readOnly = true)
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public UserEntity getCurrentUser() {
        Long userId = (Long) httpSession.getAttribute(USER_ID);

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user(" + userId + ") not found."));
    }
}
