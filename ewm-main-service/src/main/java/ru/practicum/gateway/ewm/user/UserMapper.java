package ru.practicum.gateway.ewm.user;

import ru.practicum.gateway.ewm.model.User;
import ru.practicum.gateway.ewm.user.dto.NewUserRequest;
import ru.practicum.gateway.ewm.user.dto.UserDto;

public class UserMapper {

    public static User toUser(NewUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}