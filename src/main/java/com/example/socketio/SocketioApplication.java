package com.example.socketio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class SocketioApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketioApplication.class, args);
    }

}
