package com.clement.magichome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

/**
 * This is the main class that launch the spring application.
 * 
 * @author Clement_Soullard
 *
 */

@SpringBootApplication
@PropertySource("classpath:ext-application.properties")
public class SchedulerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SchedulerApplication.class, args);
	}

}