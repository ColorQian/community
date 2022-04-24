package com.nowcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CommunityApplication {

	// 用来管理bean的生命周期的, 主要用来管理bean的初始化方法
	// 这个注解修饰的方法会在构造器调用完以后被执行
	@PostConstruct
	public void init() {
		// 解决netty启动冲突的问题
		// see Netty4Utils.setAvailableProcessors()
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}
