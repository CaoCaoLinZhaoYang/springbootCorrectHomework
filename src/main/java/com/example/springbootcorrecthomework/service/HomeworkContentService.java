package com.example.springbootcorrecthomework.service;

import com.example.springbootcorrecthomework.entity.HomeworkContent;
import com.example.springbootcorrecthomework.repository.HomeworkContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class HomeworkContentService {
    
    @Autowired
    private HomeworkContentRepository homeworkContentRepository;
    
    public HomeworkContent findByDateAndType(Date date, Integer homeworkTypeId) {
        return homeworkContentRepository.findByDateAndType(date, homeworkTypeId);
    }
    
    public HomeworkContent save(HomeworkContent homeworkContent) {
        if (homeworkContent.getId() != null) {
            homeworkContentRepository.update(homeworkContent);
        } else {
            homeworkContentRepository.insert(homeworkContent);
        }
        return homeworkContent;
    }
    
    public void deleteById(Integer id) {
        homeworkContentRepository.deleteById(id);
    }
}