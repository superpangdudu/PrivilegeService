package com.plus3.privilege;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@ServletComponentScan
@EnableDiscoveryClient
public class PrivilegeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrivilegeServiceApplication.class, args);
	}
}
