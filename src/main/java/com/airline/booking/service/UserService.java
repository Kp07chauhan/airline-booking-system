package com.airline.booking.service;

import com.airline.booking.dto.user.LoginResponseDto;
import com.airline.booking.dto.user.LoginUserDto;
import com.airline.booking.dto.user.RegisterUserDto;
import com.airline.booking.dto.user.ResponseUserDto;
import com.airline.booking.entity.User;
import com.airline.booking.entity.enumEntity.Role;
import com.airline.booking.repository.UserRepository;
import com.airline.booking.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ✅ Register
    public ResponseUserDto registerUser(RegisterUserDto dto){

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);

        User saved = userRepository.save(user);

        return new ResponseUserDto(
                saved.getId(),
                saved.getName(),
                saved.getEmail()
        );
    }

    // ✅ Login (JWT)
    public LoginResponseDto loginUser(LoginUserDto dto){

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()   // ✅ PASS ROLE
        );

        return new LoginResponseDto(token);
    }
}