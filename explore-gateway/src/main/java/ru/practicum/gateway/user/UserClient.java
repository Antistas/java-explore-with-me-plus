package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.NewUserRequest;

import java.util.List;

@Service
public class UserClient extends BaseClient {

    public UserClient(@Value("${ewm-main-service.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public ResponseEntity<Object> getUsers(List<Long> ids, int from, int size) {
        UriComponentsBuilder uri = UriComponentsBuilder
                .fromPath("/admin/users")
                .queryParam("from", from)
                .queryParam("size", size);
        if (ids != null && !ids.isEmpty()) {
            uri.queryParam("ids", ids.toArray());
        }
        return get(uri.build().toUriString());
    }

    public ResponseEntity<Object> addUser(NewUserRequest request) {
        return post("/admin/users", request);
    }

    public ResponseEntity<Object> deleteUser(long userId) {
        return delete("/admin/users/{userId}", null, java.util.Map.of("userId", userId));
    }
}
