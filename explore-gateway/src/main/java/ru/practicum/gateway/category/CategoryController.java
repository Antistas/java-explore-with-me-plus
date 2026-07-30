package ru.practicum.gateway.category;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
@Slf4j
@Validated
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryClient categoryClient;

    // 1. PUBLIC API (Доступно всем)
    @GetMapping("/categories")
    public ResponseEntity<Object> getCategoriesPublic(
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        log.info("Gateway: GET /categories | from={}, size={}", from, size);
        return categoryClient.getCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<Object> getCategoryByIdPublic(@PathVariable Long catId) {
        log.info("Gateway: GET /categories/{}", catId);
        return categoryClient.getCategory(catId);
    }

    // 2. ADMIN API (Только для администраторов)
    @PostMapping("/admin/categories")
    public ResponseEntity<Object> addCategoryAdmin(@RequestBody @Valid NewCategoryDto categoryDto) {
        log.info("Gateway stub: POST /admin/categories | body: {}", categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/admin/categories/{catId}")
    public ResponseEntity<Object> deleteCategoryAdmin(@PathVariable Long catId) {
        log.info("Gateway stub: DELETE /admin/categories/{}", catId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/admin/categories/{catId}")
    public ResponseEntity<Object> updateCategoryAdmin(
            @PathVariable Long catId,
            @RequestBody @Valid CategoryDto categoryDto) {

        log.info("Gateway stub: PATCH /admin/categories/{} | body: {}", catId, categoryDto);
        return ResponseEntity.ok().build();
    }
}
