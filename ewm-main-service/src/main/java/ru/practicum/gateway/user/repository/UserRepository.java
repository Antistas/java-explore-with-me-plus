package ru.practicum.gateway.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.gateway.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}