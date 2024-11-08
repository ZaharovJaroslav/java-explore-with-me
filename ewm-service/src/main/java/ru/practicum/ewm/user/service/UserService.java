package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.subscription.dto.UserWithSubscribers;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUserAdmin(NewUserRequest newUserRequest);

    List<UserDto> getUsersAdmin(List<Long> ids, Pageable pageable);

    void deleteUserAdmin(@PathVariable Long id);

    UserWithSubscribers addSubscriber(Long userId, Long authorId);

    void deleteSubscriber(Long userId, Long authorId);

    UserWithSubscribers getUserWithSubscribers(Long userId, Pageable pageable);
}
