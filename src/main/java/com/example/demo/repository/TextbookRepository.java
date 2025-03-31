package com.example.demo.repository;

import com.example.demo.model.Textbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TextbookRepository extends JpaRepository<Textbook, Long> {
    List<Textbook> findByGrade(String grade);
    List<Textbook> findBySubject(String subject);
    List<Textbook> findByGradeAndSubject(String grade, String subject);
    List<Textbook> findByNameContaining(String name);
    List<Textbook> findByNameContainingOrGradeOrSubject(String name, String grade, String subject);
} 