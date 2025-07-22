package com.mcqsystem.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class SubmitAnswerRequest {
    private Long examId;
    private List<AnswerDTO> answers;
}

