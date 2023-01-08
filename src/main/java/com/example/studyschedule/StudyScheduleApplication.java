package com.example.studyschedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudyScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyScheduleApplication.class, args);
    }

}
