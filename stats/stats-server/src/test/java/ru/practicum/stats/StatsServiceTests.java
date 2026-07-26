package ru.practicum.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.stats.StatsServerApplication;
import ru.practicum.stats.dto.EndpointHit;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.repository.HitRepository;
import ru.practicum.stats.service.StatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {StatsServerApplication.class, HitRepository.class})
class StatsServiceTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HitRepository hitRepository;

    private StatsService statsService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        statsService = new StatsService(hitRepository);
    }

    @Test
    void saveHit_ShouldSaveHitToDatabase() {
        EndpointHit endpointHit = EndpointHit.builder()
                .app("")
                .uri("")
                .ip("")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();

        statsService.saveHit(endpointHit);

        List<Hit> hits = entityManager.getEntityManager()
                .createQuery("SELECT h FROM Hit h", Hit.class)
                .getResultList();

        assertThat(hits).hasSize(1);
        assertThat(hits.get(0).getApp()).isEqualTo("");
        assertThat(hits.get(0).getUri()).isEqualTo("");
        assertThat(hits.get(0).getIp()).isEqualTo("");
    }

    @Test
    void saveHit_WithMultipleHits_ShouldSaveAll() {
        EndpointHit hit1 = EndpointHit.builder()
                .app("app1")
                .uri("/uri1")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();

        EndpointHit hit2 = EndpointHit.builder()
                .app("app2")
                .uri("/uri2")
                .ip("2.2.2.2")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();

        statsService.saveHit(hit1);
        statsService.saveHit(hit2);

        List<Hit> hits = entityManager.getEntityManager()
                .createQuery("SELECT h FROM Hit h", Hit.class)
                .getResultList();

        assertThat(hits).hasSize(2);
    }

    @Test
    void getStats_WithoutUris_ShouldReturnAllStats() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Hit hit1 = Hit.builder()
                .app("app1")
                .uri("/uri1")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit1);

        Hit hit2 = Hit.builder()
                .app("app2")
                .uri("/uri2")
                .ip("2.2.2.2")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit2);

        entityManager.flush();

        List<ViewStats> stats = statsService.getStats(start, end, null, false);

        assertThat(stats).hasSize(2);
        assertThat(stats).extracting(ViewStats::getApp)
                .contains("app1", "app2");
    }

    @Test
    void getStats_WithUris_ShouldReturnFilteredStats() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Hit hit1 = Hit.builder()
                .app("app1")
                .uri("/uri1")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit1);

        Hit hit2 = Hit.builder()
                .app("app1")
                .uri("/uri2")
                .ip("2.2.2.2")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit2);
        entityManager.flush();
        List<ViewStats> stats = statsService.getStats(start, end, List.of("/uri1"), false);

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getUri()).isEqualTo("/uri1");
        assertThat(stats.get(0).getHits()).isEqualTo(1);
    }

    @Test
    void getStats_WithUniqueIp_ShouldCountDistinctIps() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Hit hit1 = Hit.builder()
                .app("app1")
                .uri("/uri1")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit1);

        Hit hit2 = Hit.builder()
                .app("app1")
                .uri("/uri1")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit2);
        entityManager.flush();
        List<ViewStats> stats = statsService.getStats(start, end, List.of("/uri1"), true);

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getHits()).isEqualTo(1);
    }

    @Test
    void getStats_WithMultipleUris_ShouldReturnMultipleStats() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Hit hit1 = Hit.builder()
                .app("app1")
                .uri("/uri1")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit1);

        Hit hit2 = Hit.builder()
                .app("app1")
                .uri("/uri2")
                .ip("2.2.2.2")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit2);

        entityManager.flush();

        List<ViewStats> stats = statsService.getStats(start, end, List.of("/uri1", "/uri2"), false);

        assertThat(stats).hasSize(2);
        assertThat(stats).extracting(ViewStats::getUri)
                .contains("/uri1", "/uri2");
    }

    @Test
    void getStats_WithNoHits_ShouldReturnEmptyList() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        List<ViewStats> stats = statsService.getStats(start, end, null, false);

        assertThat(stats).isEmpty();
    }

    @Test
    void getStats_WithUrisOutsideDateRange_ShouldReturnEmptyList() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().minusDays(1);

        Hit hit = Hit.builder()
                .app("app1")
                .uri("/uri1")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit);
        entityManager.flush();

        List<ViewStats> stats = statsService.getStats(start, end, List.of("/uri1"), false);

        assertThat(stats).isEmpty();
    }

    @Test
    void getStats_WithUniqueIpAndMultipleIps_ShouldCountCorrectly() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Hit hit1 = Hit.builder()
                .app("app1")
                .uri("/uri1")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit1);

        Hit hit2 = Hit.builder()
                .app("app1")
                .uri("/uri1")
                .ip("2.2.2.2")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit2);

        entityManager.flush();

        List<ViewStats> stats = statsService.getStats(start, end, List.of("/uri1"), true);

        assertThat(stats).hasSize(1);
        assertThat(stats.get(0).getHits()).isEqualTo(2);  // Два уникальных IP
    }

    @Test
    void getStats_WithDifferentApps_ShouldGroupByApp() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        Hit hit1 = Hit.builder()
                .app("app1")
                .uri("/uri1")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit1);

        Hit hit2 = Hit.builder()
                .app("app2")
                .uri("/uri1")
                .ip("2.2.2.2")
                .timestamp(LocalDateTime.now())
                .build();
        entityManager.persist(hit2);

        entityManager.flush();

        List<ViewStats> stats = statsService.getStats(start, end, List.of("/uri1"), false);

        assertThat(stats).hasSize(2);
        assertThat(stats).extracting(ViewStats::getApp)
                .contains("app1", "app2");
    }
}
