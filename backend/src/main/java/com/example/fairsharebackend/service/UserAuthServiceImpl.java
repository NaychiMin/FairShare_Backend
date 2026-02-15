package com.example.fairsharebackend.service;

import com.example.fairsharebackend.entity.User;
import com.example.fairsharebackend.entity.UserCredential;
import com.example.fairsharebackend.entity.dto.request.UserRegisterRequestDto;
import com.example.fairsharebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserAuthServiceImpl implements UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegisterRequestDto dto) {
        User user = new User();

//        TODO additional validation logic here!

        user.setEmail(dto.getEmail());

        UserCredential cred = new UserCredential();
        cred.setPasswordHash(passwordEncoder.encode(dto.getPassword())); // hash password
        user.setUserCredential(cred);

        return userRepository.save(user);
    }

    public boolean checkPassword(String rawPassword, User user) {
        return passwordEncoder.matches(rawPassword, user.getUserCredential().getPasswordHash());
    }
}
