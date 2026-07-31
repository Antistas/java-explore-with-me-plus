package ru.practicum.gateway.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.category.dto.CategoryDto;
import ru.practicum.gateway.category.dto.NewCategoryDto;
import ru.practicum.gateway.category.service.AdminCategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class AdminCategoryController {
    private final AdminCategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid NewCategoryDto categoryDto) {
        log.info("EWM: POST /admin/categories | body={}", categoryDto);
        return categoryService.addCategory(categoryDto);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto updateCategory(@PathVariable long categoryId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        log.info("EWM: PATCH /admin/categories/{} | body={}", categoryId, categoryDto);
        return categoryService.updateCategory(categoryId, categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long categoryId) {
        log.info("EWM: DELETE /admin/categories/{}", categoryId);
        categoryService.deleteCategory(categoryId);
    }
}
