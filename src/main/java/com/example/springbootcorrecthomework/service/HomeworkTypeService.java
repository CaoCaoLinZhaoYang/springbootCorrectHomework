package com.example.springbootcorrecthomework.service;

import com.example.springbootcorrecthomework.entity.HomeworkType;
import com.example.springbootcorrecthomework.repository.HomeworkTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeworkTypeService {
    
    @Autowired
    private HomeworkTypeRepository homeworkTypeRepository;
    
    public List<HomeworkType> findAll() {
        return homeworkTypeRepository.findAll();
    }
    
    public HomeworkType findById(Integer id) {
        return homeworkTypeRepository.findById(id);
    }
}