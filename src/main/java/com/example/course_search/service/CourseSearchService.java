package com.example.course_search.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.course_search.document.CourseDocument;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final ElasticsearchOperations operations;

    public List<CourseDocument> search(String query, String category, String type,
            Integer minAge, Integer maxAge,
            Double minPrice, Double maxPrice,
            LocalDate startDate, String sort,
            int page, int size) {

        Criteria criteria = new Criteria();

        if (query != null) {
            // Enhanced search with fuzzy matching for title and regular search for
            // description
            criteria = criteria.or("title").fuzzy(query) // Use fuzzy matching for title
                    .or("description").contains(query);
        }

        if (category != null) {
            criteria = criteria.and("category").is(category);
        }

        if (type != null) {
            criteria = criteria.and("type").is(type);
        }

        if (minAge != null || maxAge != null) {
            Criteria ageCriteria = new Criteria("minAge");
            if (minAge != null)
                ageCriteria.greaterThanEqual(minAge);
            if (maxAge != null)
                ageCriteria.lessThanEqual(maxAge);
            criteria = criteria.and(ageCriteria);
        }

        if (minPrice != null || maxPrice != null) {
            Criteria priceCriteria = new Criteria("price");
            if (minPrice != null)
                priceCriteria.greaterThanEqual(minPrice);
            if (maxPrice != null)
                priceCriteria.lessThanEqual(maxPrice);
            criteria = criteria.and(priceCriteria);
        }

        if (startDate != null) {
            criteria = criteria.and("nextSessionDate").greaterThanEqual(startDate);
        }

        Sort sortBuilder = Sort.by("nextSessionDate").ascending();
        if ("priceAsc".equals(sort)) {
            sortBuilder = Sort.by("price").ascending();
        } else if ("priceDesc".equals(sort)) {
            sortBuilder = Sort.by("price").descending();
        }

        Query searchQuery;
        if (query != null || category != null || type != null ||
                minAge != null || maxAge != null || minPrice != null ||
                maxPrice != null || startDate != null) {
            searchQuery = new CriteriaQuery(criteria);
        } else {
            // If no criteria, create a match all query
            searchQuery = new CriteriaQuery(new Criteria());
        }

        Pageable pageable = PageRequest.of(page, size, sortBuilder);

        SearchHits<CourseDocument> hits = operations.search(searchQuery, CourseDocument.class);
        return hits.getSearchHits().stream().map(SearchHit::getContent).toList();
    }
}
