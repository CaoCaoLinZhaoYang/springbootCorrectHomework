package com.example.springbootcorrecthomework.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootcorrecthomework.entity.Student;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends BaseMapper<Student> {
    
    @Select("SELECT * FROM students ORDER BY student_number")
    List<Student> findAll();
    
    @Select("SELECT * FROM students WHERE student_number LIKE CONCAT('%', #{keyword}, '%') " +
            "OR name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR pinyin_initials LIKE CONCAT('%', #{keyword}, '%')")
    List<Student> findByKeyword(String keyword);
}