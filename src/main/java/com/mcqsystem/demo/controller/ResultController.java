package com.mcqsystem.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mcqsystem.demo.model.Result;
import com.mcqsystem.demo.dto.SubmitAnswerRequest;
import com.mcqsystem.demo.service.ResultService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitExam(@RequestBody SubmitAnswerRequest request, Principal principal) {
        return resultService.submitExam(request, principal);
    }

    @GetMapping
    public List<Result> getMyResults(Principal principal) {
        return resultService.getMyResults(principal);
    }

    @GetMapping("/{resultId}/answers")
    public ResponseEntity<?> getAnswersWithCorrectness(@PathVariable Long resultId, Principal principal) {
        return resultService.getAnswersWithCorrectness(resultId, principal);
    }
}
