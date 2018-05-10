package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.Student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Andreas Kutschera.
 */
@Repository
public interface StudentRepository  extends JpaRepository<Student, Long> {

}
