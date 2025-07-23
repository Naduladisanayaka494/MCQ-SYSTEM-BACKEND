package com.mcqsystem.demo.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mcqsystem.demo.dto.AnswerDTO;
import com.mcqsystem.demo.dto.SubmitAnswerRequest;
import com.mcqsystem.demo.model.Answer;
import com.mcqsystem.demo.model.Exam;
import com.mcqsystem.demo.model.Result;
import com.mcqsystem.demo.model.User;
import com.mcqsystem.demo.model.Question;
import com.mcqsystem.demo.repository.AnswerRepository;
import com.mcqsystem.demo.repository.ExamRepository;
import com.mcqsystem.demo.repository.QuestionRepository;
import com.mcqsystem.demo.repository.ResultRepository;
import com.mcqsystem.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultRepository resultRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    // Submit exam answers endpoint
    @PostMapping("/submit")
    public ResponseEntity<?> submitExam(@RequestBody SubmitAnswerRequest request, Principal principal) {
        // Fetch user by email from principal
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch the exam by ID from request
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Create new Result entity
        Result result = new Result();
        result.setUser(user);
        result.setExam(exam);
        result.setTimestamp(LocalDateTime.now());

        int score = 0;
        List<Answer> answerList = new ArrayList<>();

        // Process each answer DTO from request
        for (AnswerDTO a : request.getAnswers()) {
            Question question = questionRepository.findById(a.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            boolean isCorrect = question.getCorrectOption() == a.getSelectedOption();
            if (isCorrect)
                score++;

            // Create Answer entity
            Answer ans = new Answer();
            ans.setQuestion(question);
            ans.setSelectedOption(a.getSelectedOption());
            ans.setCorrect(isCorrect);
            ans.setResult(result);

            answerList.add(ans);
        }

        result.setScore(score);
        resultRepository.save(result);
        answerRepository.saveAll(answerList);

        return ResponseEntity.ok(Map.of("score", score, "total", request.getAnswers().size()));
    }

    // Get results of currently authenticated user
    @GetMapping
    public List<Result> getMyResults(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return resultRepository.findByUserId(user.getId());
    }

    // Get answers for a specific result, including correctness info
    @GetMapping("/{resultId}/answers")
    public ResponseEntity<?> getAnswersWithCorrectness(@PathVariable Long resultId, Principal principal) {
        Result result = resultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        // Ensure current user owns this result
        if (!result.getUser().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized");
        }

        List<Answer> answers = answerRepository.findAll().stream()
                .filter(a -> a.getResult().getId().equals(resultId))
                .toList();

        List<Map<String, Object>> response = answers.stream().map(a -> Map.of(
                "question", a.getQuestion().getQuestionText(),
                "options", a.getQuestion().getOptions(),
                "selectedOption", a.getSelectedOption(),
                "correctOption", a.getQuestion().getCorrectOption(),
                "isCorrect", a.isCorrect())).toList();

        return ResponseEntity.ok(response);
    }
}