package com.example.kafkaconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动时手动指定profile即可
 *
 * @author chenchuancheng
 * @since 2023/09/19 16:28
 */
@SpringBootApplication
public class Consumer {

	public static void main(String[] args) {
		SpringApplication.run(Consumer.class, args);
	}

}
