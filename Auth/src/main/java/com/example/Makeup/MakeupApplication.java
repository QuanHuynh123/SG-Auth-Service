package com.example.Makeup;

import java.io.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@Slf4j
@SpringBootApplication
@EnableCaching
@EnableRabbit
public class MakeupApplication {

  public static void main(String[] args) throws IOException {
    SpringApplication.run(MakeupApplication.class, args);

    log.info("Auth Application Started Successfully");
  }
}
