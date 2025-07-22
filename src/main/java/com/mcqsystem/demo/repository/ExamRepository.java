package com.mcqsystem.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mcqsystem.demo.model.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
}
