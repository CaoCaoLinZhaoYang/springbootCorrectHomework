package com.example.springbootcorrecthomework.controller;

import com.example.springbootcorrecthomework.dto.UnfinishedCountByStudent;
import com.example.springbootcorrecthomework.dto.UnfinishedStudentDTO;
import com.example.springbootcorrecthomework.entity.CorrectionRecord;
import com.example.springbootcorrecthomework.service.CorrectionRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/correctionRecords")
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
    
    @GetMapping("/has-homework")
    public boolean hasHomeworkRecords(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam Integer typeId) {
        return correctionRecordService.hasHomeworkRecords(date, typeId);
    }
    
    @PostMapping
    public CorrectionRecord saveCorrectionRecord(@RequestBody CorrectionRecord correctionRecord) {
        return correctionRecordService.saveRecord(correctionRecord);
    }
    
    @GetMapping("/previousAssignedDate")
    public Date getPreviousAssignedDate(
            @RequestParam Integer typeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date currentDate) {
        return correctionRecordService.findPreviousAssignedDate(typeId, currentDate);
    }
    
    @GetMapping("/nextAssignedDate")
    public Date getNextAssignedDate(
            @RequestParam Integer typeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date currentDate) {
        return correctionRecordService.findNextAssignedDate(typeId, currentDate);
    }
    
    @GetMapping("/statistics")
    public Map<String, Integer> getStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam Integer typeId) {
        return correctionRecordService.getStatisticsByDateAndType(date, typeId);
    }
    
    @GetMapping("/unfinishedStudents")
    public List<UnfinishedStudentDTO> getUnfinishedStudents(@RequestParam Integer typeId) {
        return correctionRecordService.findUnfinishedStudentsByType(typeId);
    }
    
    @GetMapping("/studentUnfinished")
    public List<CorrectionRecord> getStudentUnfinished(
            @RequestParam Integer studentId,
            @RequestParam Integer typeId) {
        return correctionRecordService.findUnfinishedByStudentAndType(studentId, typeId);
    }
    
    @GetMapping("/studentUnfinishedAllTypes")
    public List<CorrectionRecord> getStudentUnfinishedAllTypes(
            @RequestParam Integer studentId,
            @RequestParam(required = false, defaultValue = "1") Integer subjectId) {
        if (subjectId != null) {
            return correctionRecordService.findUnfinishedByStudentAndSubject(studentId, subjectId);
        }
        return correctionRecordService.findUnfinishedByStudentAllTypes(studentId);
    }
    
    @GetMapping("/unfinishedCountBySubject")
    public List<UnfinishedCountByStudent> getUnfinishedCountBySubject(@RequestParam Integer subjectId) {
        return correctionRecordService.findUnfinishedCountBySubject(subjectId);
    }
    
    @PutMapping("/reset")
    public void resetCorrectionRecords(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam Integer typeId) {
        correctionRecordService.resetByDateAndType(date, typeId);
    }
    
    @PutMapping("/setAllFinished")
    public void setAllFinished(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam Integer typeId) {
        correctionRecordService.setAllFinishedByDateAndType(date, typeId);
    }
}