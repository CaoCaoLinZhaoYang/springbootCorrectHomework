package com.example.springbootcorrecthomework.service;

import com.example.springbootcorrecthomework.entity.CorrectionRecord;
import com.example.springbootcorrecthomework.repository.CorrectionRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CorrectionRecordService {
    
    @Autowired
    private CorrectionRecordRepository correctionRecordRepository;
    
    public List<CorrectionRecord> findByDateAndType(Date date, Integer homeworkTypeId) {
        return correctionRecordRepository.findByDateAndType(date, homeworkTypeId);
    }
    
    public List<CorrectionRecord> findUnfinishedByStudentAndType(Integer studentId, Integer homeworkTypeId) {
        return correctionRecordRepository.findUnfinishedByStudentAndType(studentId, homeworkTypeId);
    }
    
    public List<Object[]> findUnfinishedStudentsByType(Integer homeworkTypeId) {
        return correctionRecordRepository.findUnfinishedStudentsByType(homeworkTypeId);
    }
    
    public CorrectionRecord save(CorrectionRecord correctionRecord) {
        if (correctionRecord.getId() != null) {
            correctionRecordRepository.update(correctionRecord);
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