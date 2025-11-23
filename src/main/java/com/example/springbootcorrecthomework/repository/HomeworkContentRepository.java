package com.example.springbootcorrecthomework.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootcorrecthomework.entity.HomeworkContent;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface HomeworkContentRepository extends BaseMapper<HomeworkContent> {
    
    @Select("SELECT * FROM homework_contents WHERE date = #{date} AND homework_type_id = #{homeworkTypeId}")
    HomeworkContent findByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
}