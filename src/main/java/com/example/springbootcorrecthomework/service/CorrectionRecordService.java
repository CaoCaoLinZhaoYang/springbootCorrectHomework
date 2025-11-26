package com.example.springbootcorrecthomework.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootcorrecthomework.dto.UnfinishedStudentDTO;
import com.example.springbootcorrecthomework.entity.CorrectionRecord;
import com.example.springbootcorrecthomework.entity.HomeworkAssignment;
import com.example.springbootcorrecthomework.repository.CorrectionRecordRepository;
import com.example.springbootcorrecthomework.repository.StudentRepository;
import com.example.springbootcorrecthomework.service.HomeworkAssignmentService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CorrectionRecordService extends ServiceImpl<CorrectionRecordRepository, CorrectionRecord> {
    
    private final CorrectionRecordRepository correctionRecordRepository;
    private final StudentRepository studentRepository;
    private final HomeworkAssignmentService homeworkAssignmentService;
    
    public CorrectionRecordService(CorrectionRecordRepository correctionRecordRepository, 
                                   StudentRepository studentRepository,
                                   HomeworkAssignmentService homeworkAssignmentService) {
        this.correctionRecordRepository = correctionRecordRepository;
        this.studentRepository = studentRepository;
        this.homeworkAssignmentService = homeworkAssignmentService;
    }
    
    public List<CorrectionRecord> findByDateAndType(Date date, Integer homeworkTypeId) {
        List<CorrectionRecord> records = correctionRecordRepository.findByDateAndType(date, homeworkTypeId);
        
        // 如果查询不到记录，则为所有学生创建默认记录
        if (records.isEmpty()) {
            List<com.example.springbootcorrecthomework.entity.Student> allStudents = studentRepository.findAll();
            for (com.example.springbootcorrecthomework.entity.Student student : allStudents) {
                CorrectionRecord record = new CorrectionRecord();
                record.setDate(date);
                record.setStudentId(student.getId());
                record.setHomeworkTypeId(homeworkTypeId);
                record.setCorrected(false);
                correctionRecordRepository.insert(record);
            }
            // 重新查询记录
            records = correctionRecordRepository.findByDateAndType(date, homeworkTypeId);
        }
        
        return records;
    }
    
    public List<CorrectionRecord> findUnfinishedByStudentAndType(Integer studentId, Integer homeworkTypeId) {
        return correctionRecordRepository.findUnfinishedByStudentAndType(studentId, homeworkTypeId);
    }
    
    public List<CorrectionRecord> findUnfinishedByStudentAllTypes(Integer studentId) {
        return correctionRecordRepository.findUnfinishedByStudentAllTypes(studentId);
    }
    
    public List<UnfinishedStudentDTO> findUnfinishedStudentsByType(Integer homeworkTypeId) {
        return correctionRecordRepository.findUnfinishedStudentsByType(homeworkTypeId);
    }
    
    public boolean hasHomeworkRecords(Date date, Integer homeworkTypeId) {
        // 判断某天某类型作业是否布置了
        return homeworkAssignmentService.isHomeworkAssigned(date, homeworkTypeId);
    }
    
    // 修复save方法与父类方法签名冲突的问题
    public CorrectionRecord saveRecord(CorrectionRecord correctionRecord) {
        boolean isNewRecord = correctionRecord.getId() == null;
        
        if (!isNewRecord) {
            correctionRecordRepository.updateById(correctionRecord);
        } else {
            correctionRecordRepository.insert(correctionRecord);
        }
        
        // 如果记录是corrected=true，则确保作业已被标记为布置
        if (correctionRecord.getCorrected() != null && correctionRecord.getCorrected()) {
            homeworkAssignmentService.createAssignmentIfNotExists(correctionRecord.getDate(), correctionRecord.getHomeworkTypeId());
        } else if (correctionRecord.getCorrected() != null && !correctionRecord.getCorrected()) {
            // 如果记录是corrected=false，检查是否需要移除作业布置记录
            checkAndRemoveAssignmentIfNecessary(correctionRecord.getDate(), correctionRecord.getHomeworkTypeId());
        }
        
        return correctionRecord;
    }
    
    /**
     * 检查并移除作业布置记录（如果必要）
     * 当某天某类型作业的所有订正记录都为false时，移除该作业布置记录
     */
    private void checkAndRemoveAssignmentIfNecessary(Date date, Integer homeworkTypeId) {
        // 检查该天该类型作业是否还有订正完成的记录
        int correctedCount = correctionRecordRepository.countCorrectedRecordsByDateAndType(date, homeworkTypeId);
        
        // 如果没有订正完成的记录，移除作业布置记录
        if (correctedCount == 0) {
            homeworkAssignmentService.deleteByDateAndType(date, homeworkTypeId);
        }
    }
    
    public void resetByDateAndType(Date date, Integer homeworkTypeId) {
        correctionRecordRepository.resetByDateAndType(date, homeworkTypeId);
        // 重置后检查是否需要清除作业布置记录
        checkAndRemoveAssignmentIfNecessary(date, homeworkTypeId);
    }
    
    public Map<String, Integer> getStatisticsByDateAndType(Date date, Integer homeworkTypeId) {
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("finished", correctionRecordRepository.countFinishedByDateAndType(date, homeworkTypeId));
        statistics.put("unfinished", correctionRecordRepository.countUnfinishedByDateAndType(date, homeworkTypeId));
        return statistics;
    }
    
    public Date findPreviousAssignedDate(Integer homeworkTypeId, Date currentDate) {
        return correctionRecordRepository.findPreviousAssignedDate(homeworkTypeId, currentDate);
    }
    
    public Date findNextAssignedDate(Integer homeworkTypeId, Date currentDate) {
        return correctionRecordRepository.findNextAssignedDate(homeworkTypeId, currentDate);
    }
}