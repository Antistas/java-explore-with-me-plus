package ru.practicum.gateway.ewm.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.gateway.ewm.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}