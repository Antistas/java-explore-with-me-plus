package ru.practicum.gateway.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.gateway.user.dto.NewUserRequest;

import java.util.List;

@RestController
@Slf4j
@Validated
public class UserController {


    @GetMapping("/admin/users")
    public ResponseEntity<Object> getUsersAdmin(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {

        log.info("Gateway stub: GET /admin/users | ids={}, from={}, size={}", ids, from, size);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/admin/users")
    public ResponseEntity<Object> registerUserAdmin(@RequestBody @Valid NewUserRequest userRequest) {
        log.info("Gateway stub: POST /admin/users | body: {}", userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<Object> deleteUserAdmin(@PathVariable Long userId) {
        log.info("Gateway stub: DELETE /admin/users/{}", userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}