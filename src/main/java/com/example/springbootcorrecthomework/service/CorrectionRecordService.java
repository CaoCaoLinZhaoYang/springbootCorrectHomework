package com.example.springbootcorrecthomework.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootcorrecthomework.dto.UnfinishedStudentDTO;
import com.example.springbootcorrecthomework.entity.CorrectionRecord;
import com.example.springbootcorrecthomework.repository.CorrectionRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CorrectionRecordService extends ServiceImpl<CorrectionRecordRepository, CorrectionRecord> {
    
    private final CorrectionRecordRepository correctionRecordRepository;
    
    public CorrectionRecordService(CorrectionRecordRepository correctionRecordRepository) {
        this.correctionRecordRepository = correctionRecordRepository;
    }
    
    public List<CorrectionRecord> findByDateAndType(Date date, Integer homeworkTypeId) {
        return correctionRecordRepository.findByDateAndType(date, homeworkTypeId);
    }
    
    public List<CorrectionRecord> findUnfinishedByStudentAndType(Integer studentId, Integer homeworkTypeId) {
        return correctionRecordRepository.findUnfinishedByStudentAndType(studentId, homeworkTypeId);
    }
    
    public List<UnfinishedStudentDTO> findUnfinishedStudentsByType(Integer homeworkTypeId) {
        return correctionRecordRepository.findUnfinishedStudentsByType(homeworkTypeId);
    }
    
    // 修复save方法与父类方法签名冲突的问题
    public CorrectionRecord saveRecord(CorrectionRecord correctionRecord) {
        if (correctionRecord.getId() != null) {
            correctionRecordRepository.updateById(correctionRecord);
        } else {
            correctionRecordRepository.insert(correctionRecord);
        }
        return correctionRecord;
    }
    
    public void resetByDateAndType(Date date, Integer homeworkTypeId) {
        correctionRecordRepository.resetByDateAndType(date, homeworkTypeId);
    }
    
    public Map<String, Integer> getStatisticsByDateAndType(Date date, Integer homeworkTypeId) {
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("finished", correctionRecordRepository.countFinishedByDateAndType(date, homeworkTypeId));
        statistics.put("unfinished", correctionRecordRepository.countUnfinishedByDateAndType(date, homeworkTypeId));
        return statistics;
    }
}