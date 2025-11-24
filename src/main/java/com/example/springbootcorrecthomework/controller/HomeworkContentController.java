package com.example.springbootcorrecthomework.controller;

import com.example.springbootcorrecthomework.entity.HomeworkContent;
import com.example.springbootcorrecthomework.service.HomeworkContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/homework-contents")
@CrossOrigin
public class HomeworkContentController {
    
    @Autowired
    private HomeworkContentService homeworkContentService;
    
    @GetMapping
    public ResponseEntity<HomeworkContent> getHomeworkContent(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam Integer typeId) {
        HomeworkContent content = homeworkContentService.findByDateAndType(date, typeId);
        if (content != null) {
            return ResponseEntity.ok(content);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
    
    @PostMapping
    public HomeworkContent saveHomeworkContent(@RequestBody HomeworkContent homeworkContent) {
        return homeworkContentService.saveContent(homeworkContent);
    }
}