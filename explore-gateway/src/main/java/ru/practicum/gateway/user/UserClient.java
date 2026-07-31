package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.NewUserRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/admin/users";

    @Autowired
    public UserClient(@Value("${ewm-main-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build());
    }

    public ResponseEntity<Object> getUsers(List<Long> ids, int from, int size) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", from);
        parameters.put("size", size);

        StringBuilder pathBuilder = new StringBuilder("?from={from}&size={size}");

        if (ids != null && !ids.isEmpty()) {
            // Преобразуем список ID в строку через запятую
            String idsParam = ids.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            parameters.put("ids", idsParam);
            pathBuilder.append("&ids={ids}");
        }

        return get(pathBuilder.toString(), null, parameters);
    }

    public ResponseEntity<Object> addUser(NewUserRequest request) {
        return post("", request);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId);
    }
}