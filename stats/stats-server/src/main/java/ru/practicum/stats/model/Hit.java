package ru.practicum.stats.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "hits",
        indexes = {
                @Index(name = "idx_hit_timestamp", columnList = "hit_timestamp"),
                @Index(name = "idx_uri", columnList = "uri"),
                @Index(name = "idx_ip", columnList = "ip")
        })
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String app;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false)
    private String ip;

    @Column(name = "hit_timestamp", nullable = false)
    private LocalDateTime timestamp;
}