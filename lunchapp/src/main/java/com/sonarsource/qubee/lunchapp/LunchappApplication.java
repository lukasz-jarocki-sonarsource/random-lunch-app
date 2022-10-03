package com.sonarsource.qubee.lunchapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LunchappApplication {

  public static void main(String[] args) {
    SpringApplication.run(LunchappApplication.class, args);
  }
}
