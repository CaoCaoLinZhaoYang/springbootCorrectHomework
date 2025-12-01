package com.example.springbootcorrecthomework.dto;

import lombok.Data;

@Data
public class UnfinishedCountByStudent {
    private Integer studentId;
    private Integer unfinishedCount;
}