package com.schedulebob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ScheduleBob 프로젝트의 메인 클래스.
 * <p>
 * Spring Boot 어플리케이션을 구동합니다.
 */
@SpringBootApplication
public class ScheduleBobApplication {

    /**
     * Main entrypoint.
     *
     * @param args 커맨드라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(ScheduleBobApplication.class, args);
    }

}