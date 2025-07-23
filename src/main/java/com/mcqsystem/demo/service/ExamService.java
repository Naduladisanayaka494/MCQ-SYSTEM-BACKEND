package com.mcqsystem.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mcqsystem.demo.model.Exam;
import com.mcqsystem.demo.repository.ExamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository examRepository;

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Exam createExam(Exam exam) {
        return examRepository.save(exam);
    }
}
