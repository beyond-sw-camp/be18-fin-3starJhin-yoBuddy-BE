package com.j3s.yobuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.j3s.yobuddy")
public class YobuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(YobuddyApplication.class, args);
	}

}
