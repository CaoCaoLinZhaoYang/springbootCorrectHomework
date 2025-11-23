package com.example.springbootcorrecthomework.entity;

import lombok.Data;
import java.util.Date;

@Data
public class HomeworkContent {
    private Integer id;
    private Date date;
    private Integer homeworkTypeId;
    private String content;
    private Date createdAt;
}