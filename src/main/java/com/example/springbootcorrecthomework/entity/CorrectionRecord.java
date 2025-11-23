package com.example.springbootcorrecthomework.entity;

import lombok.Data;
import java.util.Date;

@Data
public class CorrectionRecord {
    private Integer id;
    private Date date;
    private Integer studentId;
    private Integer homeworkTypeId;
    private Boolean corrected;
    private Date createdAt;
    private Date updatedAt;
}