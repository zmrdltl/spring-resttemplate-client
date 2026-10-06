package com.sparta.springresttemplateclient.service;

import com.sparta.springresttemplateclient.naver.dto.ItemDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j(topic = "NAVER API")
@Service
public class NaverApiService {

    private final RestTemplate restTemplate;
    private final String shoppingApiBaseUrl;

    public NaverApiService(RestTemplateBuilder builder,
                           @Value("${shopping.api.base-url}") String shoppingApiBaseUrl) {
        this.restTemplate = builder.build();
        this.shoppingApiBaseUrl = shoppingApiBaseUrl;
    }

    public List<ItemDto> searchItems(String query) {
        // 종료된 네이버 쇼핑 API의 응답 형식을 로컬 서버로 실습한다.
        URI uri = UriComponentsBuilder
                .fromUriString(shoppingApiBaseUrl)
                .path("/api/server/search/shop")
                .queryParam("query", "{query}")
                .encode()
                .buildAndExpand(query)
                .toUri();
        log.info("shopping uri = {}", uri);

        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)
                .build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        log.info("Shopping API Status Code : {}", responseEntity.getStatusCode());

        return fromJSONtoItems(responseEntity.getBody());
    }

    public List<ItemDto> fromJSONtoItems(String responseEntity) {
        JSONObject jsonObject = new JSONObject(responseEntity);
        JSONArray items = jsonObject.getJSONArray("items");
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Object item : items) {
            ItemDto itemDto = new ItemDto((JSONObject) item);
            itemDtoList.add(itemDto);
        }

        return itemDtoList;
    }
}
