package ru.practicum.gateway.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.gateway.category.dto.CategoryDto;
import ru.practicum.gateway.exception.NotFoundException;
import ru.practicum.gateway.category.model.Category;
import ru.practicum.gateway.category.repository.CategoryRepository;
import ru.practicum.gateway.util.OffsetPageRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublicCategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getCategories(int from, int size) {
        return categoryRepository.findAll(new OffsetPageRequest(from, size, Sort.by("id")))
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CategoryDto getCategory(long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
    }

    private CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
