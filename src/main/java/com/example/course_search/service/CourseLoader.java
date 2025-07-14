package com.example.course_search.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import com.example.course_search.document.CourseDocument;
import com.example.course_search.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseLoader {

    private final CourseRepository courseRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void init() throws IOException {
        // Clear existing index and recreate with proper mappings
        try {
            if (elasticsearchOperations.indexOps(IndexCoordinates.of("courses")).exists()) {
                elasticsearchOperations.indexOps(IndexCoordinates.of("courses")).delete();
                log.info("Deleted existing courses index");
            }

            // Create index with proper mappings
            elasticsearchOperations.indexOps(CourseDocument.class).createWithMapping();
            log.info("Created courses index with proper mappings");

            // Load sample data with custom ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            InputStream is = getClass().getResourceAsStream("/sample-courses.json");
            List<CourseDocument> courses = objectMapper.readValue(is, new TypeReference<>() {
            });

            // Set titleSuggest field for autocomplete
            for (CourseDocument course : courses) {
                course.setTitleSuggest(course.getTitle());
            }

            // Save courses one by one to handle any conversion errors
            for (CourseDocument course : courses) {
                try {
                    courseRepository.save(course);
                } catch (Exception e) {
                    log.error("Error saving course {}: {}", course.getId(), e.getMessage());
                }
            }

            log.info("Loaded {} courses into Elasticsearch", courses.size());

        } catch (Exception e) {
            log.error("Error initializing course data", e);
        }
    }
}
