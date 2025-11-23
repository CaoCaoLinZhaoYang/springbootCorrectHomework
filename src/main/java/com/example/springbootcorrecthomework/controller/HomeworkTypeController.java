package com.example.springbootcorrecthomework.controller;

import com.example.springbootcorrecthomework.entity.HomeworkType;
import com.example.springbootcorrecthomework.service.HomeworkTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/homework-types")
@CrossOrigin
public class HomeworkTypeController {
    
    @Autowired
    private HomeworkTypeService homeworkTypeService;
    
    @GetMapping
    public List<HomeworkType> getAllHomeworkTypes() {
        return homeworkTypeService.findAll();
    }
}