package com.example.backend.adapter;

import java.util.List;
import java.util.Map;

public interface NewsSourceAdapter {

    List<Map<String, String>> fetchSportsArticles();
}
