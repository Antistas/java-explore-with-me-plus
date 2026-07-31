package ru.practicum.gateway.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.category.dto.CategoryDto;
import ru.practicum.gateway.category.dto.NewCategoryDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class CategoryClient extends BaseClient {

    @Autowired
    public CategoryClient(@Value("${ewm-main-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }



    public ResponseEntity<Object> addCategory(NewCategoryDto request) {
        return post("/admin/categories", request);
    }

    public ResponseEntity<Object> updateCategory(Long catId, CategoryDto request) {
        return patch("/admin/categories/" + catId, request);
    }

    public ResponseEntity<Object> deleteCategory(Long catId) {
        return delete("/admin/categories/" + catId);
    }


}