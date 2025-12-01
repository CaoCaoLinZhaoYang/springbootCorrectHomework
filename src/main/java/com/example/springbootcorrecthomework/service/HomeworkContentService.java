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
        // 先检查是否存在相同日期和类型的记录
        HomeworkContent existingContent = homeworkContentRepository.findByDateAndType(
            homeworkContent.getDate(), homeworkContent.getHomeworkTypeId());
        
        if (existingContent != null) {
            // 如果存在，则更新现有记录
            homeworkContent.setId(existingContent.getId());
            homeworkContentRepository.updateById(homeworkContent);
        } else {
            // 如果不存在，则插入新记录
            homeworkContentRepository.insert(homeworkContent);
        }
        return homeworkContent;
    }
}