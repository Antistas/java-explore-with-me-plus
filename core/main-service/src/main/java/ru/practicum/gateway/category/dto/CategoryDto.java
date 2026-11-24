package ru.practicum.gateway.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    private Long id;

    @NotBlank(message = "Category name cannot be blank")
    @Size(max = 50, message = "Category name must not exceed 50 characters")
    private String name;
}
