package com.devops.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Main class for the Task Mmanager application.
 * The focus of this application is to show an example of a Task Manager using Java
 * that can create a task, assign it to a user and update or delete the task. */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TaskManagerApiApplication {

    /**
     * The main entry point for the Spring Boot application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Avvia l'applicazione Spring Boot
        SpringApplication.run(TaskManagerApiApplication.class, args);
        System.out.println("Task Manager API started successfully! Ready for task workflow operations.");
    }
}


