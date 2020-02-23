package com.lzx.tmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

import com.lzx.tmall.util.PortUtil;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages= {"com.lzx.tmall"})
public class Application {
	static {
		PortUtil.checkPort(6379, "Redis 服务端", true);
	}
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
