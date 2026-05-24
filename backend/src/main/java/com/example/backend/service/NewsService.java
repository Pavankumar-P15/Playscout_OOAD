package com.example.backend.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.backend.adapter.NewsSourceAdapter;


@Service
public class NewsService {

    @Value("${gnews.cache.ttl-seconds:300}")
    private long cacheTtlSeconds;

    // Depends on abstraction, not concrete implementation
    private final NewsSourceAdapter newsSourceAdapter;

    // Cache state managed by proxy
    private final Object cacheLock = new Object();
    private volatile List<Map<String, String>> cachedArticles = List.of();
    private volatile Instant cacheFetchedAt = Instant.EPOCH;

    public NewsService(@Qualifier("newsApiAdapter") NewsSourceAdapter newsSourceAdapter) {
        this.newsSourceAdapter = newsSourceAdapter;
    }

 
    public List<Map<String, String>> getSportsNews() {
        // First check without lock (fast path)
        if (isCacheFresh()) {
            return cachedArticles;
        }

        // Synchronized block for cache update (slow path)
        synchronized (cacheLock) {
            // Double-checked locking: verify cache is still stale
            if (isCacheFresh()) {
                return cachedArticles;
            }

            // Cache is stale - delegate to real subject (any NewsSourceAdapter implementation)
            List<Map<String, String>> freshArticles = newsSourceAdapter.fetchSportsArticles();

            // Update cache
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
}
