package com.example.springbootcorrecthomework.controller;

import com.example.springbootcorrecthomework.entity.HomeworkContent;
import com.example.springbootcorrecthomework.service.HomeworkContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/homework-contents")
@CrossOrigin
public class HomeworkContentController {
    
    @Autowired
    private HomeworkContentService homeworkContentService;
    
    @GetMapping
    public HomeworkContent getHomeworkContent(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam Integer typeId) {
        return homeworkContentService.findByDateAndType(date, typeId);
    }
    
    @PostMapping
    public HomeworkContent saveHomeworkContent(@RequestBody HomeworkContent homeworkContent) {
        return homeworkContentService.saveContent(homeworkContent);
    }
}