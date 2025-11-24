package com.example.springbootcorrecthomework.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootcorrecthomework.entity.HomeworkAssignment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HomeworkAssignmentRepository extends BaseMapper<HomeworkAssignment> {
    
    @Select("SELECT COUNT(*) FROM homework_assignments WHERE date = #{date} AND homework_type_id = #{homeworkTypeId}")
    int countByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Select("SELECT * FROM homework_assignments WHERE date = #{date} AND homework_type_id = #{homeworkTypeId}")
    HomeworkAssignment findByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Select("SELECT DISTINCT date FROM homework_assignments WHERE homework_type_id = #{homeworkTypeId} ORDER BY date")
    List<Date> findDatesByType(@Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Delete("DELETE FROM homework_assignments WHERE date = #{date} AND homework_type_id = #{homeworkTypeId}")
    int deleteByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
}