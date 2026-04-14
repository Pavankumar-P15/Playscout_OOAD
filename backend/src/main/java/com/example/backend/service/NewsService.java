package com.example.backend.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.backend.adapter.NewsSourceAdapter;

/**
 * Proxy Pattern: Controls access to expensive external resource (news source) through caching.
 * Depends on NewsSourceAdapter abstraction, not concrete implementations.
 * 
 * Open-Closed Principle: Service is closed for modification but open for extension.
 * To add new news sources (RSS, Twitter, Reddit, etc.), simply create new adapter implementations
 * and inject them. The NewsService proxy logic remains unchanged.
 * 
 * Example - Can switch implementations via constructor injection:
 * - new NewsService(new NewsApiAdapter(...))          // GNews API
 * - new NewsService(new RssFeedAdapter(...))          // RSS feeds
 * - new NewsService(new TwitterApiAdapter(...))       // Twitter
 * - new NewsService(new NewsAggregatorAdapter(...))   // Multiple sources combined
 */
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

    /**
     * Proxy method: Returns cached articles if fresh, otherwise delegates to adapter to fetch fresh data.
     * Uses double-checked locking for thread-safe caching.
     */
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

    /**
     * Proxy decides: should we use cached version or fetch fresh data?
     */
    private boolean isCacheFresh() {
        if (cacheTtlSeconds <= 0 || cacheFetchedAt.equals(Instant.EPOCH)) {
            return false;
        }
        return cacheFetchedAt.plusSeconds(cacheTtlSeconds).isAfter(Instant.now());
    }
}
