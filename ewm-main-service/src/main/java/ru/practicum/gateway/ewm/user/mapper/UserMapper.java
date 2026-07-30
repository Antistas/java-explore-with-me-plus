package ru.practicum.gateway.ewm.user.mapper;

import ru.practicum.gateway.ewm.user.dto.UserShortDto;
import ru.practicum.gateway.ewm.user.model.User;

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
}