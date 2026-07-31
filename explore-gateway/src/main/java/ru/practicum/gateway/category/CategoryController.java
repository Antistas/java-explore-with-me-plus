package ru.practicum.gateway.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.category.dto.CategoryDto;
import ru.practicum.gateway.category.dto.NewCategoryDto;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class CategoryController {
    private final CategoryClient categoryClient;

    @GetMapping("/categories")
    public ResponseEntity<Object> getCategoriesPublic(
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Gateway: GET /categories | from={}, size={}", from, size);
        return categoryClient.getCategories(from, size);
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<Object> getCategoryByIdPublic(@PathVariable long categoryId) {
        log.info("Gateway: GET /categories/{}", categoryId);
        return categoryClient.getCategory(categoryId);
    }

    @PostMapping("/admin/categories")
    public ResponseEntity<Object> addCategoryAdmin(
            @RequestBody @Valid NewCategoryDto categoryDto) {
        log.info("Gateway: POST /admin/categories | body={}", categoryDto);
        return categoryClient.addCategory(categoryDto);
    }

    @PatchMapping("/admin/categories/{categoryId}")
    public ResponseEntity<Object> updateCategoryAdmin(
            @PathVariable long categoryId,
            @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Gateway: PATCH /admin/categories/{} | body={}", categoryId, categoryDto);
        return categoryClient.updateCategory(categoryId, categoryDto);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<Object> deleteCategoryAdmin(@PathVariable long categoryId) {
        log.info("Gateway: DELETE /admin/categories/{}", categoryId);
        return categoryClient.deleteCategory(categoryId);
    }
}
