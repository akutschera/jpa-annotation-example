package com.example.demo.jpa.repository;

import com.example.demo.jpa.entity.GraduationClass;
import com.example.demo.jpa.entity.MasterThesis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Andreas Kutschera.
 */
@Repository
public interface GraduationClassRepository extends JpaRepository<GraduationClass, Long> {

}
