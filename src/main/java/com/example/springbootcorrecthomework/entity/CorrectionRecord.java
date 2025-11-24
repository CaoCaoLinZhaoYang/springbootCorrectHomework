package com.example.springbootcorrecthomework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
@TableName("correction_records")
public class CorrectionRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
    
    private Integer studentId;
    private Integer homeworkTypeId;
    private Boolean corrected;
    private Date createdAt;
    private Date updatedAt;
}