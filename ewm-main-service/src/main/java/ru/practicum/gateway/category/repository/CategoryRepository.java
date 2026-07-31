package ru.practicum.gateway.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.gateway.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}