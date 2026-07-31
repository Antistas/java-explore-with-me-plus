package ru.practicum.gateway.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.gateway.exception.ConflictException;
import ru.practicum.gateway.exception.NotFoundException;
import ru.practicum.gateway.user.dto.NewUserRequest;
import ru.practicum.gateway.user.dto.UserDto;
import ru.practicum.gateway.user.mapper.UserMapper;
import ru.practicum.gateway.user.model.User;
import ru.practicum.gateway.user.repository.UserRepository;
import ru.practicum.gateway.util.OffsetPageRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {
    private final UserRepository userRepository;

    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        OffsetPageRequest pageable = new OffsetPageRequest(from, size, Sort.by("id"));
        List<User> users = ids == null || ids.isEmpty()
                ? userRepository.findAll(pageable).getContent()
                : userRepository.findAllByIdIn(ids, pageable);

        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Transactional
    public UserDto addUser(NewUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("User with email='" + request.getEmail() + "' already exists");
        }

        return UserMapper.toUserDto(userRepository.save(UserMapper.toEntity(request)));
    }

    @Transactional
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        userRepository.deleteById(userId);
    }
}
