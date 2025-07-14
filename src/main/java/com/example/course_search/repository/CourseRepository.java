package com.example.course_search.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.course_search.document.CourseDocument;

public interface CourseRepository extends ElasticsearchRepository<CourseDocument, String> {

}
