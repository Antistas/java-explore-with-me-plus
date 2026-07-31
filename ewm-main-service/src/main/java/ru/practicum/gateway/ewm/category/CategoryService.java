package ru.practicum.gateway.ewm.category;

import ru.practicum.gateway.ewm.category.dto.CategoryDto;
import ru.practicum.gateway.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {


    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    void deleteCategory(Long catId);

}