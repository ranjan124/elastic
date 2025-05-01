package com.shieldteq.elastic;

import org.springframework.boot.SpringApplication;

public class TestElasticApplication {

	public static void main(String[] args) {
		SpringApplication.from(ElasticApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
