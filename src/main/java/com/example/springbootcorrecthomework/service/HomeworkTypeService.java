package com.example.springbootcorrecthomework.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    
    public List<HomeworkType> findBySubjectId(Integer subjectId) {
        QueryWrapper<HomeworkType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subject_id", subjectId);
        return homeworkTypeRepository.selectList(queryWrapper);
    }
    
    public HomeworkType findById(Integer id) {
        return homeworkTypeRepository.selectById(id);
    }
    
    /**
     * 根据作业类型ID获取科目ID
     * @param homeworkTypeId 作业类型ID
     * @return 科目ID
     */
    public Integer getSubjectIdByHomeworkType(Integer homeworkTypeId) {
        HomeworkType homeworkType = homeworkTypeRepository.selectById(homeworkTypeId);
        return homeworkType != null ? homeworkType.getSubjectId() : 1; // 默认为语文
    }
}