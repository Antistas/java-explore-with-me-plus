package ru.practicum.gateway.event;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventClient extends BaseClient {

    public EventClient(@Value("${ewm-main-service.url}") String serverUrl,
                       RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .build());
    }

    public ResponseEntity<Object> getEvents(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            String rangeStart,
                                            String rangeEnd,
                                            boolean onlyAvailable,
                                            String sort,
                                            int from,
                                            int size) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("onlyAvailable", onlyAvailable);
        parameters.put("from", from);
        parameters.put("size", size);

        StringBuilder path = new StringBuilder("/events?onlyAvailable={onlyAvailable}&from={from}&size={size}");

        addParameter(path, parameters, "text", text);
        addParameter(path, parameters, "categories", categories);
        addParameter(path, parameters, "paid", paid);
        addParameter(path, parameters, "rangeStart", rangeStart);
        addParameter(path, parameters, "rangeEnd", rangeEnd);
        addParameter(path, parameters, "sort", sort);

        return get(path.toString(), null, parameters);
    }

    public ResponseEntity<Object> getEvent(long eventId) {
        return get("/events/{eventId}", null, Map.of("eventId", eventId));
    }

    private void addParameter(StringBuilder path,
                              Map<String, Object> parameters,
                              String name,
                              Object value) {
        if (value != null) {
            path.append('&').append(name).append("={").append(name).append('}');
            parameters.put(name, value);
        }
    }
}
