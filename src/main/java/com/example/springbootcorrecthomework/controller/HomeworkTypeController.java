package com.example.springbootcorrecthomework.controller;

import com.example.springbootcorrecthomework.entity.HomeworkType;
import com.example.springbootcorrecthomework.service.HomeworkTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/homeworkTypes")
@CrossOrigin
public class HomeworkTypeController {
    
    @Autowired
    private HomeworkTypeService homeworkTypeService;
    
    @GetMapping
    public List<HomeworkType> getAllHomeworkTypes(@RequestParam(required = false, defaultValue = "1") Integer subjectId) {
        if (subjectId != null) {
            return homeworkTypeService.findBySubjectId(subjectId);
        }
        return homeworkTypeService.findAll();
    }
}