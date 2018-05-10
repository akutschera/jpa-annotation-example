package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.Course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Andreas Kutschera.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

}
