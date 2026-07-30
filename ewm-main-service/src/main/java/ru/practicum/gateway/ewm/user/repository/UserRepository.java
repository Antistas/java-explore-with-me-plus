package ru.practicum.gateway.ewm.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.gateway.ewm.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}