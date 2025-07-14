package com.example.course_search.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.course_search.document.CourseDocument;
import com.example.course_search.service.CourseSearchService;
import com.example.course_search.service.AutocompleteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class CourseSearchController {

    private final CourseSearchService courseSearchService;
    private final AutocompleteService autocompleteService;

    @GetMapping
    public Map<String, Object> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(defaultValue = "upcoming") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<CourseDocument> courses = courseSearchService.search(q, category, type, minAge, maxAge, minPrice, maxPrice,
                startDate, sort, page, size);
        return Map.of("total", courses.size(), "courses", courses);
    }

    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam String q) {
        return autocompleteService.getSuggestions(q);
    }
}
