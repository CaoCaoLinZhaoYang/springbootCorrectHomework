package com.example.springbootcorrecthomework.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootcorrecthomework.entity.HomeworkAssignment;
import com.example.springbootcorrecthomework.repository.HomeworkAssignmentRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class HomeworkAssignmentService extends ServiceImpl<HomeworkAssignmentRepository, HomeworkAssignment> {
    
    private final HomeworkAssignmentRepository homeworkAssignmentRepository;
    
    public HomeworkAssignmentService(HomeworkAssignmentRepository homeworkAssignmentRepository) {
        this.homeworkAssignmentRepository = homeworkAssignmentRepository;
    }
    
    public boolean isHomeworkAssigned(Date date, Integer homeworkTypeId) {
        return homeworkAssignmentRepository.countByDateAndType(date, homeworkTypeId) > 0;
    }
    
    public HomeworkAssignment createAssignmentIfNotExists(Date date, Integer homeworkTypeId) {
        // 检查作业是否已布置
        if (!isHomeworkAssigned(date, homeworkTypeId)) {
            // 创建新的作业布置记录
            HomeworkAssignment assignment = new HomeworkAssignment();
            assignment.setDate(date);
            assignment.setHomeworkTypeId(homeworkTypeId);
            homeworkAssignmentRepository.insert(assignment);
            return assignment;
        }
        return homeworkAssignmentRepository.findByDateAndType(date, homeworkTypeId);
    }
    
    public List<Date> getAssignedDatesByType(Integer homeworkTypeId) {
        return homeworkAssignmentRepository.findDatesByType(homeworkTypeId);
    }
    
    public HomeworkAssignment findByDateAndType(Date date, Integer homeworkTypeId) {
        return homeworkAssignmentRepository.findByDateAndType(date, homeworkTypeId);
    }
    
    public int deleteByDateAndType(Date date, Integer homeworkTypeId) {
        return homeworkAssignmentRepository.deleteByDateAndType(date, homeworkTypeId);
    }
}