package ru.practicum.gateway.event.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Location {
    private Double lat;
    private Double lon;
}