package ru.practicum.shareit.user;

import java.util.List;

interface UserRepository {
    List<UserDto> findAll();

    UserDto save(UserDto user);

    UserDto updateUser(Long userId, UserDto userDto);

    UserDto findUserById(Long userId);

    void deleteUser(Long userId);
}