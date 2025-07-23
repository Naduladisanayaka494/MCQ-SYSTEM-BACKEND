package com.mcqsystem.demo.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.mcqsystem.demo.model.Question;
import com.mcqsystem.demo.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getQuestionsByExam(Long examId) {
        return questionRepository.findByExamId(examId);
    }

    public Question addQuestion(Question question) {
        return questionRepository.save(question);
    }
}
