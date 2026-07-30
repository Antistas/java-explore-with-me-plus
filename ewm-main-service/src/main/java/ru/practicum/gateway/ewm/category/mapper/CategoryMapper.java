package ru.practicum.gateway.ewm.category.mapper;

import ru.practicum.gateway.ewm.category.dto.CategoryDto;
import ru.practicum.gateway.ewm.category.model.Category;

public class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category toEntity(CategoryDto dto) {
        if (dto == null) {
            return null;
        }

        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}