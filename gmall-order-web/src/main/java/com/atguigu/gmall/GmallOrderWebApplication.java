package com.atguigu.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall")	//扫描dubbo注解，知道这是一个消费者
public class GmallOrderWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallOrderWebApplication.class, args);
	}
}
