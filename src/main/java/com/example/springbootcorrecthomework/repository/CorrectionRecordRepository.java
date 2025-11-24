package com.example.springbootcorrecthomework.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootcorrecthomework.dto.UnfinishedStudentDTO;
import com.example.springbootcorrecthomework.entity.CorrectionRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CorrectionRecordRepository extends BaseMapper<CorrectionRecord> {
    
    @Select("SELECT * FROM correction_records WHERE date = #{date} AND homework_type_id = #{homeworkTypeId}")
    List<CorrectionRecord> findByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Select("SELECT c.* FROM correction_records c " +
           "JOIN homework_assignments ha ON c.date = ha.date AND c.homework_type_id = ha.homework_type_id " +
           "WHERE c.student_id = #{studentId} AND c.homework_type_id = #{homeworkTypeId} AND c.corrected = false " +
           "ORDER BY c.date")
    List<CorrectionRecord> findUnfinishedByStudentAndType(@Param("studentId") Integer studentId, @Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Select("SELECT DISTINCT c.date, s.student_number, s.name " +
           "FROM correction_records c " +
           "JOIN students s ON c.student_id = s.id " +
           "WHERE c.homework_type_id = #{homeworkTypeId} AND c.corrected = false AND c.date IN (" +
           "SELECT date FROM correction_records WHERE homework_type_id = #{homeworkTypeId} GROUP BY date HAVING COUNT(CASE WHEN corrected = true THEN 1 END) > 0" +
           ") ORDER BY c.date, s.student_number")
    List<UnfinishedStudentDTO> findUnfinishedStudentsByType(@Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Select("SELECT COUNT(*) FROM correction_records WHERE date = #{date} AND homework_type_id = #{homeworkTypeId} AND corrected = true")
    int countFinishedRecordsByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Select("SELECT COUNT(*) FROM correction_records WHERE date = #{date} AND homework_type_id = #{homeworkTypeId} AND corrected = false")
    int countUnfinishedByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Select("SELECT COUNT(*) FROM correction_records WHERE date = #{date} AND homework_type_id = #{homeworkTypeId} AND corrected = true")
    int countFinishedByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Update("UPDATE correction_records SET corrected = false WHERE date = #{date} AND homework_type_id = #{homeworkTypeId}")
    int resetByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Select("SELECT COUNT(*) FROM correction_records WHERE date = #{date} AND homework_type_id = #{homeworkTypeId}")
    int countRecordsByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
    
    @Select("SELECT COUNT(*) FROM correction_records WHERE date = #{date} AND homework_type_id = #{homeworkTypeId} AND corrected = true")
    int countCorrectedRecordsByDateAndType(@Param("date") Date date, @Param("homeworkTypeId") Integer homeworkTypeId);
}