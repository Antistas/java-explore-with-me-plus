package ru.practicum.gateway.ewm.user;

import ru.practicum.gateway.ewm.user.dto.NewUserRequest;
import ru.practicum.gateway.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {


    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);
}