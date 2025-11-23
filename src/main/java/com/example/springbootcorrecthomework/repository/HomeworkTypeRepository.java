package com.example.springbootcorrecthomework.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootcorrecthomework.entity.HomeworkType;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkTypeRepository extends BaseMapper<HomeworkType> {
    
}