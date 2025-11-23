package com.example.springbootcorrecthomework.repository;

import com.example.springbootcorrecthomework.entity.HomeworkContent;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface HomeworkContentRepository {
    
    @Select("SELECT * FROM homework_contents WHERE date = #{date} AND homework_type_id = #{homeworkTypeId}")
    HomeworkContent findByDateAndType(Date date, Integer homeworkTypeId);
    
    @Insert("INSERT INTO homework_contents(date, homework_type_id, content) VALUES(#{date}, #{homeworkTypeId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HomeworkContent homeworkContent);
    
    @Update("UPDATE homework_contents SET content = #{content} WHERE id = #{id}")
    int update(HomeworkContent homeworkContent);
    
    @Delete("DELETE FROM homework_contents WHERE id = #{id}")
    int deleteById(Integer id);
}