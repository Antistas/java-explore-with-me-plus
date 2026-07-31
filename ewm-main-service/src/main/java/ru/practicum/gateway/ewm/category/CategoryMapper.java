package ru.practicum.gateway.ewm.category;

import ru.practicum.gateway.ewm.model.Category;
import ru.practicum.gateway.ewm.category.dto.CategoryDto;
import ru.practicum.gateway.ewm.category.dto.NewCategoryDto;

public class CategoryMapper {

    public static Category toCategory(NewCategoryDto request) {
        return Category.builder()
                .name(request.getName())
                .build();
    }

    public static Category toCategory(CategoryDto request) {
        return Category.builder()
                .id(request.getId())
                .name(request.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}