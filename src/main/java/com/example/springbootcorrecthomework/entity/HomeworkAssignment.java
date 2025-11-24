package com.example.springbootcorrecthomework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
@TableName("homework_assignments")
public class HomeworkAssignment {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;
    
    private Integer homeworkTypeId;
    private Date createdAt;
}