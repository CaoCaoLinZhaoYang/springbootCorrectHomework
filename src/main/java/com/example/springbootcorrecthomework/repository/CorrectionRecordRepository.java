package com.example.springbootcorrecthomework.repository;

import com.example.springbootcorrecthomework.entity.CorrectionRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface CorrectionRecordRepository {
    
    @Select("SELECT * FROM correction_records WHERE date = #{date} AND homework_type_id = #{homeworkTypeId}")
    List<CorrectionRecord> findByDateAndType(Date date, Integer homeworkTypeId);
    
    @Select("SELECT * FROM correction_records WHERE student_id = #{studentId} AND homework_type_id = #{homeworkTypeId} AND corrected = false ORDER BY date")
    List<CorrectionRecord> findUnfinishedByStudentAndType(Integer studentId, Integer homeworkTypeId);
    
    @Select("SELECT DISTINCT c.date, s.student_number, s.name " +
           "FROM correction_records c " +
           "JOIN students s ON c.student_id = s.id " +
           "WHERE c.homework_type_id = #{homeworkTypeId} AND c.corrected = false AND c.date IN (" +
           "SELECT date FROM correction_records WHERE homework_type_id = #{homeworkTypeId} GROUP BY date HAVING COUNT(CASE WHEN corrected = true THEN 1 END) > 0" +
           ") ORDER BY c.date, s.student_number")
    List<Object[]> findUnfinishedStudentsByType(Integer homeworkTypeId);
    
    @Insert("INSERT INTO correction_records(date, student_id, homework_type_id, corrected) VALUES(#{date}, #{studentId}, #{homeworkTypeId}, #{corrected})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CorrectionRecord correctionRecord);
    
    @Update("UPDATE correction_records SET corrected = #{corrected}, updated_at = NOW() WHERE id = #{id}")
    int update(CorrectionRecord correctionRecord);
    
    @Update("UPDATE correction_records SET corrected = false WHERE date = #{date} AND homework_type_id = #{homeworkTypeId}")
    int resetByDateAndType(Date date, Integer homeworkTypeId);
    
    @Select("SELECT COUNT(*) FROM correction_records WHERE date = #{date} AND homework_type_id = #{homeworkTypeId} AND corrected = true")
    int countFinishedByDateAndType(Date date, Integer homeworkTypeId);
    
    @Select("SELECT COUNT(*) FROM correction_records WHERE date = #{date} AND homework_type_id = #{homeworkTypeId} AND corrected = false")
    int countUnfinishedByDateAndType(Date date, Integer homeworkTypeId);
}