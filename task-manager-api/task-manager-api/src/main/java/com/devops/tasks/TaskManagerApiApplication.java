package com.devops.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Main class for the Task Manager API.
 * Excludes DataSourceAutoConfiguration to avoid connection issues during pipeline stages 
 * that don't need the database running (e.g., 'build', 'verify').
 */
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


