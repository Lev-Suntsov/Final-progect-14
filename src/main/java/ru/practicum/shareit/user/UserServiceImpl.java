package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public UserDto saveUser(UserDto user) {
        return repository.save(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto user) {
        return repository.updateUser(userId, user);
    }

    @Override
    public UserDto findUserById(Long userId) {
        return repository.findUserById(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        repository.deleteUser(userId);
    }

}
