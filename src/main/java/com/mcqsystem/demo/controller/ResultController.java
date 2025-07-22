package com.mcqsystem.demo.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.catalina.User;
import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mcqsystem.demo.dto.AnswerDTO;
import com.mcqsystem.demo.dto.SubmitAnswerRequest;
import com.mcqsystem.demo.model.Answer;
import com.mcqsystem.demo.model.Exam;
import com.mcqsystem.demo.model.Result;
import com.mcqsystem.demo.repository.AnswerRepository;
import com.mcqsystem.demo.repository.QuestionRepository;
import com.mcqsystem.demo.repository.ResultRepository;
import com.mcqsystem.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {
    private final ResultRepository resultRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @PostMapping("/submit")
    public ResponseEntity<?> submitExam(@RequestBody SubmitAnswerRequest request, Principal principal) {
        User user = (User) userRepository.findByEmail(principal.getName()).orElseThrow();

        Result result = new Result();
        result.setUser((com.mcqsystem.demo.model.User) user);
        result.setExam(new Exam());
        result.setTimestamp(LocalDateTime.now());

        int score = 0;
        List<Answer> answerList = new ArrayList<>();

        for (AnswerDTO a : request.getAnswers()) {
            com.mcqsystem.demo.model.Question q = questionRepository.findById(a.getQuestionId()).orElseThrow();
            boolean isCorrect = q.getCorrectOption() == a.getSelectedOption();
            if (isCorrect)
                score++;

            Answer ans = new Answer();
            ans.setQuestion(q);
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

    @GetMapping
    public List<Result> getMyResults(Principal principal) {
        User user = (User) userRepository.findByEmail(principal.getName()).orElseThrow();
        return resultRepository.findByUserId(((Answer) user).getId());
    }


@GetMapping("/{resultId}/answers")
public ResponseEntity<?> getAnswersWithCorrectness(@PathVariable Long resultId, Principal principal) {
    Result result = resultRepository.findById(resultId)
            .orElseThrow(() -> new RuntimeException("Result not found"));

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
        "isCorrect", a.isCorrect()
    )).toList();

    return ResponseEntity.ok(response);
}

    
}
