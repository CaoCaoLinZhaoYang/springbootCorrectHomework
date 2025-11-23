package com.example.springbootcorrecthomework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

@Data
@TableName("homework_contents")
public class HomeworkContent {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Date date;
    private Integer homeworkTypeId;
    private String content;
    private Date createdAt;
}