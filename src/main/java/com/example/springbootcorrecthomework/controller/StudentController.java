package com.example.springbootcorrecthomework.controller;

import com.example.springbootcorrecthomework.entity.Student;
import com.example.springbootcorrecthomework.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.findAll();
    }
    
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Integer id) {
        return studentService.findById(id);
    }
    
    @GetMapping("/search")
    public List<Student> searchStudents(@RequestParam String keyword) {
        return studentService.findByKeyword(keyword);
    }
}