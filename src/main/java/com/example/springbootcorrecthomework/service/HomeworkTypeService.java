package com.example.springbootcorrecthomework.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootcorrecthomework.entity.HomeworkType;
import com.example.springbootcorrecthomework.repository.HomeworkTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeworkTypeService extends ServiceImpl<HomeworkTypeRepository, HomeworkType> {
    
    private final HomeworkTypeRepository homeworkTypeRepository;
    
    public HomeworkTypeService(HomeworkTypeRepository homeworkTypeRepository) {
        this.homeworkTypeRepository = homeworkTypeRepository;
    }
    
    public List<HomeworkType> findAll() {
        return homeworkTypeRepository.selectList(null);
    }
    
    public HomeworkType findById(Integer id) {
        return homeworkTypeRepository.selectById(id);
    }
}