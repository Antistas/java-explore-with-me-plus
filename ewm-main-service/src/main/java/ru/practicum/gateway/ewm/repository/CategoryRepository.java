package ru.practicum.gateway.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.gateway.ewm.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
