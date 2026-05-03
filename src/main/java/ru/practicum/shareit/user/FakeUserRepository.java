package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Exeption.DublicateException;
import ru.practicum.shareit.Exeption.NotFoundException;

import java.util.HashMap;
import java.util.List;

@Repository
public class FakeUserRepository implements UserRepository {
    private static final HashMap<Long, UserDto> users = new HashMap<>();
    private long nextId = 1L;

    @Override
    public List<UserDto> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public UserDto save(UserDto user) {
        validateUser(user);

        if (user.getId() == null) {
            user.setId(nextId++);
        }

        users.put(user.getId(), user);
        return user;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto user) {
        UserDto oldUser;
        if (users.containsKey(userId)) {
             oldUser = users.get(userId);

             if (user.getName() != null && !user.getName().isBlank()) {
                 oldUser.setName(user.getName());
             }

             if (user.getEmail() != null && !user.getEmail().isBlank()) {
                 validateUser(user);
                 oldUser.setEmail(user.getEmail());
             }
        } else {
            throw new NotFoundException("Данный пользователь не найден");
        }
        return oldUser;
    }


    private void validateUser(UserDto user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Некорректный email");
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty() && !user.getEmail().isBlank()) {
            for (UserDto u : users.values()) {
                if (u.getEmail().contains(user.getEmail())) {
                    throw new DublicateException("Данный email уже используется");
                }
            }
        }
    }

    @Override
    public UserDto findUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        } else {
            throw new  NotFoundException("Данный пользователь не найден");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

}