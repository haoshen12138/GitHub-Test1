package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.jypt.mapper")
@ComponentScan("com.jypt")
public class SecondhandApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecondhandApplication.class, args);
    }
}
