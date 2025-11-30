package com.example.springbootcorrecthomework.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("homework_types")
public class HomeworkType {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private Integer subjectId;
}