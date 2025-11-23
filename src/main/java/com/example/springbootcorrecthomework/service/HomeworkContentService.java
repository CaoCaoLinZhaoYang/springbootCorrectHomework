package com.example.springbootcorrecthomework.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootcorrecthomework.entity.HomeworkContent;
import com.example.springbootcorrecthomework.repository.HomeworkContentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class HomeworkContentService extends ServiceImpl<HomeworkContentRepository, HomeworkContent> {
    
    private final HomeworkContentRepository homeworkContentRepository;
    
    public HomeworkContentService(HomeworkContentRepository homeworkContentRepository) {
        this.homeworkContentRepository = homeworkContentRepository;
    }
    
    public HomeworkContent findByDateAndType(Date date, Integer homeworkTypeId) {
        return homeworkContentRepository.findByDateAndType(date, homeworkTypeId);
    }
    
    // 修复save方法与父类方法签名冲突的问题
    public HomeworkContent saveContent(HomeworkContent homeworkContent) {
        if (homeworkContent.getId() != null) {
            homeworkContentRepository.updateById(homeworkContent);
        } else {
            homeworkContentRepository.insert(homeworkContent);
        }
        return homeworkContent;
    }
}