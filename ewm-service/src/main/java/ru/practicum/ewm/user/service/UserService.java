package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {

    public UserDto addUserAdmin(NewUserRequest newUserRequest);

    public List<UserDto> getUsersAdmin(List<Long> ids, Pageable pageable);

    public void deleteUserAdmin(@PathVariable Long id);
}
