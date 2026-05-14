package com.wealthwise.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wealthwise.server.mapper")
public class WealthWiseApplication {
    public static void main(String[] args) {
        SpringApplication.run(WealthWiseApplication.class, args);
    }
}
