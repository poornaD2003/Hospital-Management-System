package com.example.HospitalManagmentSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HospitalManagmentSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalManagmentSystemApplication.class, args);
	}

}
