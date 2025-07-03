package com.deveagles.be15_deveagles_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/*
@EnableScheduling
 해당 어노테이션하면 계속 스케쥴링 돌아서 우선 주석 처리 -> 전송되는지는 확인
*/
public class Be15DevEaglesBeApplication {

  public static void main(String[] args) {
    SpringApplication.run(Be15DevEaglesBeApplication.class, args);
  }
}
