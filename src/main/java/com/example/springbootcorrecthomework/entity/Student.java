package com.example.springbootcorrecthomework.entity;

import lombok.Data;

@Data
public class Student {
    private Integer id;
    private String studentNumber;
    private String name;
    private String pinyinInitials;
}