package com.example.springbootcorrecthomework.repository;

import com.example.springbootcorrecthomework.entity.Student;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StudentRepository {
    
    @Select("SELECT * FROM students ORDER BY student_number")
    List<Student> findAll();
    
    @Select("SELECT * FROM students WHERE id = #{id}")
    Student findById(Integer id);
    
    @Select("SELECT * FROM students WHERE student_number LIKE CONCAT('%', #{keyword}, '%') " +
            "OR name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR pinyin_initials LIKE CONCAT('%', #{keyword}, '%')")
    List<Student> findByKeyword(String keyword);
}