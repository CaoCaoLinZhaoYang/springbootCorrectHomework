package com.example.springbootcorrecthomework.repository;

import com.example.springbootcorrecthomework.entity.HomeworkType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface HomeworkTypeRepository {
    
    @Select("SELECT * FROM homework_types")
    List<HomeworkType> findAll();
    
    @Select("SELECT * FROM homework_types WHERE id = #{id}")
    HomeworkType findById(Integer id);
}