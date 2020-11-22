package com.kakaopay.shinch.spread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan("com.kakaopay.shinch.spread")
@Slf4j
public class SpreadApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpreadApplication.class, args);
    }
}