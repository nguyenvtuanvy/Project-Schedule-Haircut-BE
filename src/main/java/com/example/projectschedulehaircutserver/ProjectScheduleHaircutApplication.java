package com.example.projectschedulehaircutserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class ProjectScheduleHaircutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectScheduleHaircutApplication.class, args);
        System.out.println("hello");
    }

}
