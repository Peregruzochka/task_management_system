package ru.peregruzochka.task_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class TaskManagementSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(TaskManagementSystemApplication.class, args);
	}
}
