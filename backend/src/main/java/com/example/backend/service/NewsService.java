package com.example.backend.service;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class NewsService {

    @Value("${gnews.api.url:https://gnews.io/api/v4/search}")
    private String gNewsApiUrl;

    @Value("${gnews.cache.ttl-seconds:300}")
    private long cacheTtlSeconds;

    private final Environment environment;

    private final Object cacheLock = new Object();
    private volatile List<Map<String, String>> cachedArticles = List.of();
    private volatile Instant cacheFetchedAt = Instant.EPOCH;

    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getSportsNews() {
        if (isCacheFresh()) {
            return cachedArticles;
        }

        synchronized (cacheLock) {
            if (isCacheFresh()) {
                return cachedArticles;
            }

            String gNewsApiKey = resolveApiKey();
            if (gNewsApiKey.isBlank()) {
                return cachedArticles;
            }

        URI uri = UriComponentsBuilder.fromUriString(gNewsApiUrl)
                .queryParam("q", "sports AND (cricket OR football OR stadium)")
                .queryParam("country", "in")
                .queryParam("token", gNewsApiKey)
                .queryParam("lang", "en")
                .encode()
                .build()
                .toUri();

        Map<String, Object> response;
        try {
            RestTemplate restTemplate = new RestTemplate();
            response = restTemplate.getForObject(uri, Map.class);
        } catch (RestClientException ex) {
                return cachedArticles;
        }

        if (response == null || !(response.get("articles") instanceof List<?> rawArticles)) {
                return cachedArticles;
        }

            List<Map<String, String>> freshArticles = rawArticles.stream()
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(this::toArticle)
                .toList();

            cachedArticles = freshArticles;
            cacheFetchedAt = Instant.now();
            return freshArticles;
        }
    }

    private boolean isCacheFresh() {
        if (cacheTtlSeconds <= 0 || cacheFetchedAt.equals(Instant.EPOCH)) {
            return false;
        }
        return cacheFetchedAt.plusSeconds(cacheTtlSeconds).isAfter(Instant.now());
    }

    private String resolveApiKey() {
        String key = environment.getProperty("G_API_KEY");
        if (key == null || key.isBlank()) {
            key = environment.getProperty("GNEWS_API_KEY", "");
        }
        return key == null ? "" : key.trim();
    }

    private Map<String, String> toArticle(Map<?, ?> rawArticle) {
        return Map.of(
                "title", toSafeString(rawArticle.get("title")),
                "description", toSafeString(rawArticle.get("description")),
                "image", toSafeString(rawArticle.get("image")),
                "url", toSafeString(rawArticle.get("url")));
    }

    private String toSafeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
