package ru.practicum.gateway.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.gateway.category.dto.CategoryDto;
import ru.practicum.gateway.category.dto.NewCategoryDto;
import ru.practicum.gateway.category.mapper.CategoryMapper;
import ru.practicum.gateway.category.model.Category;
import ru.practicum.gateway.category.repository.CategoryRepository;
import ru.practicum.gateway.event.repository.EventRepository;
import ru.practicum.gateway.exception.ConflictException;
import ru.practicum.gateway.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CategoryDto addCategory(NewCategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Category with name='" + categoryDto.getName() + "' already exists");
        }

        Category category = categoryRepository.save(CategoryMapper.toEntity(categoryDto));
        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    public CategoryDto updateCategory(long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(
                        "Category with id=" + categoryId + " was not found"));

        if (categoryRepository.existsByNameAndIdNot(categoryDto.getName(), categoryId)) {
            throw new ConflictException("Category with name='" + categoryDto.getName() + "' already exists");
        }

        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Category with id=" + categoryId + " was not found");
        }
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("The category is not empty");
        }

        categoryRepository.deleteById(categoryId);
    }
}
