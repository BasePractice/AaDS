package ru.mifi.practice.voln.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.mifi.practice.voln.domain.entity.UserEntity;
import ru.mifi.practice.voln.domain.model.JwtAuthenticationResponse;
import ru.mifi.practice.voln.domain.model.SignInRequest;
import ru.mifi.practice.voln.domain.model.SignUpRequest;
import ru.mifi.practice.voln.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse signUp(SignUpRequest request) {
        var user = UserEntity.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .authorities(List.of())
            .enabled(true)
            .build();
        userRepository.saveAndFlush(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        ));
        var user = userRepository.findByUsername(request.getUsername());
        var jwt = jwtService.generateToken(user.orElseThrow());
        return new JwtAuthenticationResponse(jwt);
    }
}
