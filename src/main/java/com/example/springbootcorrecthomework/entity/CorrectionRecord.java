package com.example.springbootcorrecthomework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("correction_records")
public class CorrectionRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date date;
    private Integer studentId;
    private Integer homeworkTypeId;
    private Boolean corrected;
    private Date createdAt;
    private Date updatedAt;
}