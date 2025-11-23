package com.example.springbootcorrecthomework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringbootCorrectHomeworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootCorrectHomeworkApplication.class, args);

    }
    @GetMapping("/hello")
    public ResponseEntity<String> hello (){
        return ResponseEntity.ok("Hello World");
    }

}
