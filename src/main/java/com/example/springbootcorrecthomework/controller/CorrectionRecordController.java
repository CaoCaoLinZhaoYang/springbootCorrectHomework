package com.example.springbootcorrecthomework.controller;

import com.example.springbootcorrecthomework.entity.CorrectionRecord;
import com.example.springbootcorrecthomework.service.CorrectionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/correction-records")
@CrossOrigin
public class CorrectionRecordController {
    
    @Autowired
    private CorrectionRecordService correctionRecordService;
    
    @GetMapping
    public List<CorrectionRecord> getCorrectionRecords(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam Integer typeId) {
        return correctionRecordService.findByDateAndType(date, typeId);
    }
    
    @PostMapping
    public CorrectionRecord saveCorrectionRecord(@RequestBody CorrectionRecord correctionRecord) {
        return correctionRecordService.save(correctionRecord);
    }
    
    @PutMapping("/reset")
    public void resetCorrectionRecords(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam Integer typeId) {
        correctionRecordService.resetByDateAndType(date, typeId);
    }
    
    @GetMapping("/statistics")
    public Map<String, Integer> getStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam Integer typeId) {
        return correctionRecordService.getStatisticsByDateAndType(date, typeId);
    }
    
    @GetMapping("/unfinished-students")
    public List<Object[]> getUnfinishedStudents(@RequestParam Integer typeId) {
        return correctionRecordService.findUnfinishedStudentsByType(typeId);
    }
    
    @GetMapping("/student-unfinished")
    public List<CorrectionRecord> getStudentUnfinished(
            @RequestParam Integer studentId,
            @RequestParam Integer typeId) {
        return correctionRecordService.findUnfinishedByStudentAndType(studentId, typeId);
    }
}