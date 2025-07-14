package com.example.course_search.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import com.example.course_search.document.CourseDocument;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutocompleteService {

    private final ElasticsearchOperations operations;

    public List<String> getSuggestions(String partialTitle) {
        try {
            // Use a simple prefix search on the title field for autocomplete
            Criteria criteria = new Criteria("title").startsWith(partialTitle);
            CriteriaQuery searchQuery = new CriteriaQuery(criteria);

            SearchHits<CourseDocument> hits = operations.search(searchQuery, CourseDocument.class);

            // Extract unique titles from the results
            List<String> suggestions = hits.getSearchHits().stream()
                    .map(hit -> hit.getContent().getTitle())
                    .distinct()
                    .limit(10)
                    .collect(Collectors.toList());

            log.info("Found {} suggestions for '{}'", suggestions.size(), partialTitle);
            return suggestions;

        } catch (Exception e) {
            log.error("Error getting suggestions for '{}': {}", partialTitle, e.getMessage());
            return List.of();
        }
    }
}