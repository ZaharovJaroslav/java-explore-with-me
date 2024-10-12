package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.user.validator.CreateUserValidator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(NewUserRequest request) {
        log.debug("addUser({})", request);
        CreateUserValidator validator = new CreateUserValidator(request);
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Запрос составлен некорректно", validator.getMessages());
        }
        User user = new User(request.getEmail(),
                             request.getName());
        try {
            return UserMapper.touserDto(userRepository.save(user));
        } catch (Exception e) {
            throw new ConflictException(e.getMessage(), new ConflictException("Нарушение целостности данных"));
        }
    }

    @Override
    public List<UserDto> getUsers(List<Integer> ids, int from, int size) {
        log.debug("getUsers({}, {}, {})", ids, from ,size);
        Pageable pageable =  PageRequest.of(from, size, Sort.by("id").ascending());
        List<User> users;
        if (!ids.isEmpty()) {
            log.info("Получение пользователей по списку id");
            users = userRepository.findAllByIdIn(ids, pageable).getContent();
        } else {
            log.info("Получение всех пользователей по параметрам: {}, {}", from, size);
            users = userRepository.findAll(pageable).getContent();
        }
        return users.stream().map(UserMapper::touserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        log.debug("deleteUserById({})", userId);
       if (userRepository.findById(userId).isPresent()) {
           userRepository.deleteById(userId);
       } else
           throw new NotFoundException("Нужный обьект не найден:",
                                       new NotFoundException("Пользователь с id = " + userId + "не найден"));
    }
}
