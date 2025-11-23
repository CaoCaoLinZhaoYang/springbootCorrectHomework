package com.example.springbootcorrecthomework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.springbootcorrecthomework.repository")
public class SpringbootCorrectHomeworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootCorrectHomeworkApplication.class, args);
    }

}