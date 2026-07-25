package ru.practicum.gateway.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Некорректный формат электронной почты")
    @Size(min = 6, max = 254, message = "Адрес электронной почты должен быть длиной от 6 до 254 символов")
    private String email;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 250, message = "Имя должно быть длиной от 2 до 250 символов")
    private String name;
}