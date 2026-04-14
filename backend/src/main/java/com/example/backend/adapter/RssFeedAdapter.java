package com.example.backend.adapter;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RssFeedAdapter implements NewsSourceAdapter {

    @Override
    public List<Map<String, String>> fetchSportsArticles() {
        return List.of();
    }
}
