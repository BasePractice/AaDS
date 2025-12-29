package ru.mifi.practice.voln.service;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mifi.practice.voln.domain.entity.UserEntity;
import ru.mifi.practice.voln.domain.model.UserModel;
import ru.mifi.practice.voln.domain.model.UserRepresentation;
import ru.mifi.practice.voln.mapper.UserMapper;
import ru.mifi.practice.voln.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public Optional<UserModel> findForLogin(String username) {
        return userRepository.findByUsername(username).map(UserMapper.MAPPER::userEntityToUserModel);
    }

    public Optional<UserRepresentation> addUser(UserModel userModel) {
        UserEntity entity = UserMapper.MAPPER.userModelToUserEntity(userModel);
        return Optional.of(userRepository.save(entity))
            .map(UserMapper.MAPPER::userEntityToUserRepresentation);
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow();
    }
}
