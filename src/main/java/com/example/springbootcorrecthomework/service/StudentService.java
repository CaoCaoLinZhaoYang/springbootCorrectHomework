package com.example.springbootcorrecthomework.service;

import com.example.springbootcorrecthomework.entity.Student;
import com.example.springbootcorrecthomework.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    public List<Student> findAll() {
        return studentRepository.findAll();
    }
    
    public Student findById(Integer id) {
        return studentRepository.findById(id);
    }
    
    public List<Student> findByKeyword(String keyword) {
        return studentRepository.findByKeyword(keyword);
    }
}