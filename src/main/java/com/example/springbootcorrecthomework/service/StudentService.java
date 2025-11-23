package com.example.springbootcorrecthomework.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootcorrecthomework.entity.Student;
import com.example.springbootcorrecthomework.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService extends ServiceImpl<StudentRepository, Student> {
    
    private final StudentRepository studentRepository;
    
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    
    public List<Student> findAll() {
        return studentRepository.findAll();
    }
    
    public Student findById(Integer id) {
        return studentRepository.selectById(id);
    }
    
    public List<Student> findByKeyword(String keyword) {
        return studentRepository.findByKeyword(keyword);
    }
}