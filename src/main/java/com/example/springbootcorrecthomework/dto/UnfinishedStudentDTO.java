package com.example.springbootcorrecthomework.dto;

import lombok.Data;
import java.util.Date;

@Data
public class UnfinishedStudentDTO {
    private Date date;
    private String studentNumber;
    private String name;
}