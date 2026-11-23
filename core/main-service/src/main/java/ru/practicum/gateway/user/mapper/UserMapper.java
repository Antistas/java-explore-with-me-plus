package ru.practicum.gateway.user.mapper;

import ru.practicum.gateway.user.dto.NewUserRequest;
import ru.practicum.gateway.user.dto.UserDto;
import ru.practicum.gateway.user.dto.UserShortDto;
import ru.practicum.gateway.user.model.User;

public class UserMapper {

    public static UserShortDto toUserShortDto(User user) {
        if (user == null) {
            return null;
        }

        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toEntity(NewUserRequest request) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }
}
