package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;
import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest user);

    List<UserDto> getUsers(List<Integer> ids, int from, int size);

    void deleteUserById(Long userId);

    User getUserById(Long userId);
}