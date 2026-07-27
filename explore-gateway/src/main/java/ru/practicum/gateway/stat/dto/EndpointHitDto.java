package ru.practicum.gateway.stat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {

    @NotBlank(message = "Имя приложения (app) не может быть пустым")
    private String app;

    @NotBlank(message = "URI не может быть пустым")
    private String uri;

    @NotBlank(message = "IP-адрес (ip) не может быть пустым")
    private String ip;

    @NotBlank(message = "Временная метка (timestamp) не может быть пустой")
    private String timestamp; // Передается строкой в формате "yyyy-MM-dd HH:mm:ss"
}